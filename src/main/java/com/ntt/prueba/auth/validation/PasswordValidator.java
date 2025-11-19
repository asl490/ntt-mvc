package com.ntt.prueba.auth.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${validation.password.pattern:^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$}")
    private String passwordPattern;

    @Value("${validation.password.message:La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial}")
    private String validationMessage;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean isValid = password.matches(passwordPattern);

        if (!isValid) {
            // Personalizar el mensaje de error
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(validationMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
