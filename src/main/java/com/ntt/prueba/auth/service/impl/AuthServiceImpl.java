package com.ntt.prueba.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ntt.prueba.auth.dto.AuthResponse;
import com.ntt.prueba.auth.dto.AuthenticationRequest;
import com.ntt.prueba.auth.dto.RefreshTokenRequest;
import com.ntt.prueba.auth.dto.RegisterRequest;
import com.ntt.prueba.auth.entity.AuthEventType;
import com.ntt.prueba.auth.entity.AuthenticationAudit;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.RefreshToken;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.AuthenticationAuditRepository;
import com.ntt.prueba.auth.repository.RefreshTokenRepository;
import com.ntt.prueba.auth.repository.RoleRepository;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.auth.service.AuthService;
import com.ntt.prueba.auth.service.UserService;
import com.ntt.prueba.exception.exception.BaseException;
import com.ntt.prueba.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final UserService userService;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final RefreshTokenRepository refreshTokenRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationAuditRepository auditRepository;

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                List<Role> roles = roleRepository.findByName("USER").stream().toList();
                // habilitar para mandar roles desde el request
                // if (request.getRoleNames() == null || request.getRoleNames().isEmpty()) {
                // roles.add(roleRepository.findByName("USER")
                // .orElseThrow(() -> new BaseException("Role USER not found")));
                // } else {
                // request.getRoleNames().forEach(roleName ->
                // roles.add(roleRepository.findByName(roleName)
                // .orElseThrow(() -> new BaseException(
                // "Role " + roleName + " not found", HttpStatus.NOT_FOUND))));
                // }
                User existingUser = userRepository.findByUsername(request.getCorreo()).orElse(null);
                if (existingUser != null) {
                        throw new BaseException("El correo ya esta registrado", HttpStatus.CONFLICT);
                }

                // Crear el usuario
                User user = User.builder()
                                .username(request.getCorreo())
                                .name(request.getNombre())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roles(roles)
                                .lastlogin(LocalDateTime.now())
                                .build();

                // Crear los teléfonos y establecer la relación bidireccional ANTES de guardar
                if (request.getPhones() != null && !request.getPhones().isEmpty()) {
                        List<Phone> phones = request.getPhones().stream()
                                        .map(phoneDTO -> Phone.builder()
                                                        .number(phoneDTO.getNumber())
                                                        .cityCode(phoneDTO.getCityCode())
                                                        .countryCode(phoneDTO.getCountryCode())
                                                        .build())
                                        .collect(Collectors.toList());

                        // Establecer la relación bidireccional
                        phones.forEach(phone -> phone.setUser(user));
                        user.setPhones(phones);
                }

                // Guardar el usuario - cascade se encarga de guardar los teléfonos
                // automáticamente
                User createdUser = userRepository.save(user);

                String jwt = jwtService.generateToken(createdUser);
                RefreshToken refreshToken = createRefreshToken(createdUser);

                // Register audit event for successful registration/login
                registerAuditEvent(createdUser, AuthEventType.LOGIN, jwt, refreshToken.getId(), true, null);

                return AuthResponse.builder()
                                .accessToken(jwt)
                                .activo(!user.getIsDeleted())
                                .creado(user.getCreatedDate())
                                .modificado(user.getLastModifiedDate())
                                .ultimoLogin(user.getLastlogin())
                                .id(user.getId())
                                .refreshToken(refreshToken.getToken())
                                .build();
        }

        @Override
        @Transactional
        public AuthResponse authenticate(AuthenticationRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(request.getUsername(),
                                                        request.getPassword()));
                        User user = userService.getUserByUsername(request.getUsername())
                                        .orElseThrow(() -> new BaseException("User not found", HttpStatus.NOT_FOUND));
                        String jwt = jwtService.generateToken(user);
                        RefreshToken refreshToken = createRefreshToken(user);
                        user.setLastlogin(LocalDateTime.now());
                        userRepository.save(user);

                        // Register audit event for successful login
                        registerAuditEvent(user, AuthEventType.LOGIN, jwt, refreshToken.getId(), true, null);

                        return AuthResponse.builder()
                                        .accessToken(jwt)
                                        .activo(!user.getIsDeleted())
                                        .creado(user.getCreatedDate())
                                        .modificado(user.getLastModifiedDate())
                                        .ultimoLogin(user.getLastlogin())
                                        .id(user.getId())
                                        .refreshToken(refreshToken.getToken())
                                        .build();
                } catch (BadCredentialsException e) {
                        // Register audit event for failed login
                        userService.getUserByUsername(request.getUsername())
                                        .ifPresent(user -> registerAuditEvent(user, AuthEventType.FAILED_LOGIN, null,
                                                        null,
                                                        false, "Invalid credentials"));
                        throw e;
                }
        }

        @Override
        @Transactional
        public AuthResponse refreshToken(RefreshTokenRequest request) {
                RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                                .orElseThrow(() -> new BaseException("Refresh Token is not in DB!",
                                                HttpStatus.NOT_FOUND));

                if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
                        // Register audit event for expired token
                        registerAuditEvent(refreshToken.getUser(), AuthEventType.TOKEN_EXPIRED, null,
                                        refreshToken.getId(), false, "Token expired");
                        refreshTokenRepository.delete(refreshToken);
                        throw new BaseException("Refresh token was expired. Please make a new signin request",
                                        HttpStatus.UNAUTHORIZED);
                }

                refreshTokenRepository.delete(refreshToken);
                User user = refreshToken.getUser();
                String newAccessToken = jwtService.generateToken(user);
                RefreshToken newRefreshToken = createRefreshToken(user);

                // Register audit event for token refresh
                registerAuditEvent(user, AuthEventType.TOKEN_REFRESH, newAccessToken, newRefreshToken.getId(), true,
                                null);

                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken.getToken())
                                .build();
        }

        private RefreshToken createRefreshToken(User user) {
                RefreshToken refreshToken = RefreshToken.builder()
                                .user(user)
                                .token(UUID.randomUUID().toString())
                                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshExpiration()))
                                .build();
                return refreshTokenRepository.save(refreshToken);
        }

        @Override
        @Transactional
        public void logout(RefreshTokenRequest request) {
                refreshTokenRepository.findByToken(request.getRefreshToken())
                                .ifPresent(refreshToken -> {
                                        // Register audit event for logout BEFORE deleting the token
                                        registerAuditEvent(refreshToken.getUser(), AuthEventType.LOGOUT, null,
                                                        refreshToken.getId(), true, null);
                                        refreshTokenRepository.delete(refreshToken);
                                });
        }

        /**
         * Register an authentication audit event
         */
        private void registerAuditEvent(User user, AuthEventType eventType, String jwt, UUID refreshTokenId,
                        boolean successful, String failureReason) {
                try {
                        AuthenticationAudit audit = AuthenticationAudit.builder()
                                        .user(user)
                                        .eventType(eventType)
                                        .accessTokenHash(jwt != null ? hashToken(jwt) : null)
                                        .refreshTokenId(refreshTokenId)
                                        .ipAddress(getClientIp())
                                        .userAgent(getUserAgent())
                                        .eventTime(LocalDateTime.now())
                                        .successful(successful)
                                        .build();

                        auditRepository.save(audit);
                } catch (Exception e) {
                        // Log the error but don't fail the authentication process
                        System.err.println("Failed to register audit event: " + e.getMessage());
                }
        }

        /**
         * Hash a token using SHA-256
         */
        private String hashToken(String token) {
                try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
                        return Base64.getEncoder().encodeToString(hash);
                } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException("Error hashing token", e);
                }
        }

        /**
         * Get client IP address from HTTP request
         */
        private String getClientIp() {
                try {
                        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                                        .getRequestAttributes();
                        if (attributes != null) {
                                var request = attributes.getRequest();
                                String ip = request.getHeader("X-Forwarded-For");
                                if (ip == null || ip.isEmpty()) {
                                        ip = request.getRemoteAddr();
                                }
                                return ip;
                        }
                } catch (Exception e) {
                        // Request context not available (e.g., in tests)
                }
                return "unknown";
        }

        /**
         * Get user agent from HTTP request
         */
        private String getUserAgent() {
                try {
                        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                                        .getRequestAttributes();
                        if (attributes != null) {
                                return attributes.getRequest().getHeader("User-Agent");
                        }
                } catch (Exception e) {
                        // Request context not available (e.g., in tests)
                }
                return "unknown";
        }
}