package com.ntt.prueba.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ntt.prueba.auth.dto.AuthenticationRequest;
import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.auth.dto.RegisterRequest;
import com.ntt.prueba.auth.entity.AuthEventType;
import com.ntt.prueba.auth.entity.AuthenticationAudit;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.RefreshToken;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;

/**
 * Utility class for building test data objects
 */
public class TestDataBuilder {

    // ==================== User Builders ====================

    public static class TestUsers {
        public static final String VALID_EMAIL = "test@example.com";
        public static final String VALID_PASSWORD = "Password123@";
        public static final String INVALID_PASSWORD = "weak";
        public static final String ADMIN_EMAIL = "admin@example.com";
    }

    public static class TestPhones {
        public static final String VALID_NUMBER = "12345678";
        public static final String VALID_CITY_CODE = "123";
        public static final String VALID_COUNTRY_CODE = "58";
        public static final String INVALID_NUMBER = "ABC123";
        public static final String INVALID_CITY_CODE = "ABCD";
    }

    // ==================== Role Builders ====================

    public static User.UserBuilder<?, ?> defaultUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("test@example.com")
                .name("Test User")
                .password("$2a$10$XYZ123") // BCrypt encoded password
                .roles(List.of(defaultRole().build()))
                .phones(new ArrayList<>());
    }

    public static User.UserBuilder<?, ?> adminUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("admin@example.com")
                .name("Admin User")
                .password("$2a$10$XYZ123")
                .roles(List.of(adminRole().build()))
                .phones(new ArrayList<>());
    }

    // ==================== Phone Builders ====================

    public static Role.RoleBuilder<?, ?> defaultRole() {
        return Role.builder()
                .id(UUID.randomUUID())
                .name("USER");
    }

    public static Role.RoleBuilder<?, ?> adminRole() {
        return Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN");
    }

    // ==================== DTO Builders ====================

    public static Phone.PhoneBuilder<?, ?> defaultPhone() {
        return Phone.builder()
                .id(UUID.randomUUID())
                .number("12345678")
                .cityCode("123")
                .countryCode("58");
    }

    public static Phone.PhoneBuilder<?, ?> phoneWithUser(User user) {
        return defaultPhone().user(user);
    }

    public static RegisterRequest.RegisterRequestBuilder defaultRegisterRequest() {
        List<PhoneDTO.CreatePhoneUserDTO> phones = new ArrayList<>();
        PhoneDTO.CreatePhoneUserDTO phone = new PhoneDTO.CreatePhoneUserDTO();
        phone.setNumber("12345678");
        phone.setCityCode("123");
        phone.setCountryCode("58");
        phones.add(phone);

        return RegisterRequest.builder()
                .correo("newuser@example.com")
                .nombre("New User")
                .password("Password123@")
                .roleNames(List.of("USER"))
                .phones(phones);
    }

    public static RegisterRequest.RegisterRequestBuilder registerRequestWithInvalidEmail() {
        return defaultRegisterRequest()
                .correo("invalid-email");
    }

    public static RegisterRequest.RegisterRequestBuilder registerRequestWithInvalidPassword() {
        return defaultRegisterRequest()
                .password("weak");
    }

    // ==================== RefreshToken Builders ====================

    public static RegisterRequest.RegisterRequestBuilder registerRequestWithInvalidPhone() {
        List<PhoneDTO.CreatePhoneUserDTO> phones = new ArrayList<>();
        PhoneDTO.CreatePhoneUserDTO phone = new PhoneDTO.CreatePhoneUserDTO();
        phone.setNumber("ABC123"); // Invalid: contains letters
        phone.setCityCode("12");
        phone.setCountryCode("58");
        phones.add(phone);

        return defaultRegisterRequest()
                .phones(phones);
    }

    public static AuthenticationRequest.AuthenticationRequestBuilder defaultAuthRequest() {
        return AuthenticationRequest.builder()
                .username("test@example.com")
                .password("Password123@");
    }

    // ==================== AuthenticationAudit Builders ====================

    public static RefreshToken.RefreshTokenBuilder<?, ?> defaultRefreshToken(User user) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(86400)); // 24 hours
    }

    public static RefreshToken.RefreshTokenBuilder<?, ?> expiredRefreshToken(User user) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().minusSeconds(3600)); // Expired 1 hour ago
    }

    // ==================== Phone DTO Builders ====================

    public static AuthenticationAudit.AuthenticationAuditBuilder<?, ?> defaultAudit(User user) {
        return AuthenticationAudit.builder()
                .id(UUID.randomUUID())
                .user(user)
                .eventType(AuthEventType.LOGIN)
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla/5.0")
                .successful(true);
    }

    public static AuthenticationAudit.AuthenticationAuditBuilder<?, ?> failedLoginAudit(User user) {
        return defaultAudit(user)
                .eventType(AuthEventType.FAILED_LOGIN)
                .successful(false);
    }

    // ==================== Predefined Test Data ====================

    public static PhoneDTO.CreatePhoneUserDTO createValidPhoneDTO() {
        PhoneDTO.CreatePhoneUserDTO phone = new PhoneDTO.CreatePhoneUserDTO();
        phone.setNumber("12345678");
        phone.setCityCode("123");
        phone.setCountryCode("58");
        return phone;
    }

    public static PhoneDTO.CreatePhoneUserDTO createInvalidPhoneDTO() {
        PhoneDTO.CreatePhoneUserDTO phone = new PhoneDTO.CreatePhoneUserDTO();
        phone.setNumber("ABC123"); // Invalid: contains letters
        phone.setCityCode("ABCD"); // Invalid: contains letters
        phone.setCountryCode("XY"); // Invalid: contains letters
        return phone;
    }
}
