package com.ntt.prueba.auth.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.ConstraintValidatorContext;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PasswordValidator Tests")
class PasswordValidatorTest {

    @Autowired
    private PasswordValidator passwordValidator;

    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        context = org.mockito.Mockito.mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = org.mockito.Mockito
                .mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(org.mockito.Mockito.anyString()))
                .thenReturn(builder);
    }

    @Test
    @DisplayName("Should accept valid password with all requirements")
    void testValidPassword() {
        String validPassword = "Password123@";
        assertTrue(passwordValidator.isValid(validPassword, context),
                "Password with uppercase, lowercase, number and special char should be valid");
    }

    @Test
    @DisplayName("Should accept password with different special characters")
    void testValidPasswordWithDifferentSpecialChars() {
        assertTrue(passwordValidator.isValid("Password123#", context));
        assertTrue(passwordValidator.isValid("Password123$", context));
        assertTrue(passwordValidator.isValid("Password123%", context));
        assertTrue(passwordValidator.isValid("Password123^", context));
        assertTrue(passwordValidator.isValid("Password123&", context));
        assertTrue(passwordValidator.isValid("Password123+", context));
        assertTrue(passwordValidator.isValid("Password123=", context));
    }

    @Test
    @DisplayName("Should reject password without uppercase letter")
    void testPasswordWithoutUppercase() {
        String password = "password123@";
        assertFalse(passwordValidator.isValid(password, context),
                "Password without uppercase should be invalid");
    }

    @Test
    @DisplayName("Should reject password without lowercase letter")
    void testPasswordWithoutLowercase() {
        String password = "PASSWORD123@";
        assertFalse(passwordValidator.isValid(password, context),
                "Password without lowercase should be invalid");
    }

    @Test
    @DisplayName("Should reject password without number")
    void testPasswordWithoutNumber() {
        String password = "Password@@@";
        assertFalse(passwordValidator.isValid(password, context),
                "Password without number should be invalid");
    }

    @Test
    @DisplayName("Should reject password without special character")
    void testPasswordWithoutSpecialChar() {
        String password = "Password123";
        assertFalse(passwordValidator.isValid(password, context),
                "Password without special character should be invalid");
    }

    @Test
    @DisplayName("Should reject password shorter than 8 characters")
    void testPasswordTooShort() {
        String password = "Pass1@";
        assertFalse(passwordValidator.isValid(password, context),
                "Password shorter than 8 characters should be invalid");
    }

    @Test
    @DisplayName("Should reject null password")
    void testNullPassword() {
        assertFalse(passwordValidator.isValid(null, context),
                "Null password should be invalid");
    }

    @Test
    @DisplayName("Should reject empty password")
    void testEmptyPassword() {
        assertFalse(passwordValidator.isValid("", context),
                "Empty password should be invalid");
    }

    @Test
    @DisplayName("Should accept long password with all requirements")
    void testLongValidPassword() {
        String longPassword = "ThisIsAVeryLongPassword123@WithAllRequirements";
        assertTrue(passwordValidator.isValid(longPassword, context),
                "Long password with all requirements should be valid");
    }

    @Test
    @DisplayName("Should reject password with only spaces")
    void testPasswordWithOnlySpaces() {
        String password = "        ";
        assertFalse(passwordValidator.isValid(password, context),
                "Password with only spaces should be invalid");
    }
}
