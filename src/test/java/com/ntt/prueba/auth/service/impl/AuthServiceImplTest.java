package com.ntt.prueba.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntt.prueba.auth.dto.AuthResponse;
import com.ntt.prueba.auth.dto.AuthenticationRequest;
import com.ntt.prueba.auth.dto.RefreshTokenRequest;
import com.ntt.prueba.auth.dto.RegisterRequest;
import com.ntt.prueba.auth.entity.RefreshToken;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.AuthenticationAuditRepository;
import com.ntt.prueba.auth.repository.RefreshTokenRepository;
import com.ntt.prueba.auth.repository.RoleRepository;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.auth.service.UserService;
import com.ntt.prueba.exception.exception.BaseException;
import com.ntt.prueba.security.JwtService;
import com.ntt.prueba.util.TestDataBuilder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationAuditRepository auditRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role userRole;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.defaultUser().build();
        userRole = TestDataBuilder.defaultRole().build();
        testRefreshToken = TestDataBuilder.defaultRefreshToken(testUser).build();
    }

    // ==================== Register Tests ====================

    @Test
    @DisplayName("Should register new user successfully with default USER role")
    void testRegister_Success_DefaultRole() {
        // Arrange
        RegisterRequest request = TestDataBuilder.defaultRegisterRequest()
                .roleNames(null)
                .build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByUsername(request.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(roleRepository, times(1)).findByName("USER");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        verify(auditRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should register new user with specified roles")
    void testRegister_Success_WithRoles() {
        // Arrange
        Role adminRole = TestDataBuilder.adminRole().build();
        RegisterRequest request = TestDataBuilder.defaultRegisterRequest()
                .roleNames(List.of("ADMIN"))
                .build();

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByUsername(request.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with existing email")
    void testRegister_DuplicateEmail() {
        // Arrange
        RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByUsername(request.getCorreo())).thenReturn(Optional.of(testUser));

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            authService.register(request);
        });

        assertTrue(exception.getMessage().contains("correo ya esta registrado"));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role not found")
    void testRegister_RoleNotFound() {
        // Arrange
        RegisterRequest request = TestDataBuilder.defaultRegisterRequest()
                .roleNames(List.of("NONEXISTENT_ROLE"))
                .build();

        when(roleRepository.findByName("NONEXISTENT_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            authService.register(request);
        });

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user with phones when phones provided")
    void testRegister_WithPhones() {
        // Arrange
        RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByUsername(request.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ==================== Authenticate Tests ====================

    @Test
    @DisplayName("Should authenticate user successfully")
    void testAuthenticate_Success() {
        // Arrange
        AuthenticationRequest request = TestDataBuilder.defaultAuthRequest().build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername(request.getUsername())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).save(testUser);
        verify(auditRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception with invalid credentials")
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        AuthenticationRequest request = TestDataBuilder.defaultAuthRequest().build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        when(userService.getUserByUsername(request.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        verify(auditRepository, times(1)).save(any()); // Failed login audit
    }

    @Test
    @DisplayName("Should throw exception when user not found during authentication")
    void testAuthenticate_UserNotFound() {
        // Arrange
        AuthenticationRequest request = TestDataBuilder.defaultAuthRequest().build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            authService.authenticate(request);
        });
    }

    // ==================== Refresh Token Tests ====================

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshToken_Success() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(testRefreshToken.getToken())
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(testRefreshToken));
        when(jwtService.generateToken(any(User.class))).thenReturn("new-jwt-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        AuthResponse response = authService.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals("new-jwt-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(refreshTokenRepository, times(1)).delete(testRefreshToken);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        verify(auditRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when refresh token not found")
    void testRefreshToken_TokenNotFound() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid-token")
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            authService.refreshToken(request);
        });

        assertTrue(exception.getMessage().contains("not in DB"));
    }

    @Test
    @DisplayName("Should throw exception when refresh token is expired")
    void testRefreshToken_ExpiredToken() {
        // Arrange
        RefreshToken expiredToken = TestDataBuilder.expiredRefreshToken(testUser).build();
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(expiredToken.getToken())
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(expiredToken));

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            authService.refreshToken(request);
        });

        assertTrue(exception.getMessage().contains("expired"));
        verify(refreshTokenRepository, times(1)).delete((RefreshToken) expiredToken);
        verify(auditRepository, times(1)).save(any()); // Expired token audit
    }

    // ==================== Logout Tests ====================

    @Test
    @DisplayName("Should logout successfully")
    void testLogout_Success() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(testRefreshToken.getToken())
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(testRefreshToken));

        // Act
        authService.logout(request);

        // Assert
        verify(refreshTokenRepository, times(1)).delete(testRefreshToken);
        verify(auditRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should handle logout with invalid token gracefully")
    void testLogout_InvalidToken() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid-token")
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.empty());

        // Act
        authService.logout(request);

        // Assert
        verify(refreshTokenRepository, times(0)).delete(any(RefreshToken.class));
        verify(auditRepository, times(0)).save(any());
    }
}
