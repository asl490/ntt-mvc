package com.ntt.prueba.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ntt.prueba.exception.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
                ErrorResponse errorResponse = new ErrorResponse(

                                ex.getMessage(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
                List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.toList());

                ErrorResponse errorResponse = new ErrorResponse(
                                "Validation failed",
                                LocalDateTime.now(),
                                errors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
                String rawMessage = Optional.ofNullable(ex.getRootCause())
                                .orElse(ex)
                                .getMessage();
                String lowerMessage = Optional.ofNullable(rawMessage)
                                .map(String::toLowerCase)
                                .orElse("");

                Map<String, String> errorPatterns = Map.of(
                                "duplicate key|unique constraint",
                                "DUPLICATE_VALUE:Ya existe un registro con un valor que debe ser único.",
                                "foreign key constraint",
                                "FOREIGN_KEY_VIOLATION:El valor ingresado no tiene una relación válida en otra tabla.",
                                "cannot insert the value null|null value",
                                "NULL_VALUE_VIOLATION:Falta un valor obligatorio.",
                                "data would be truncated|string or binary data",
                                "VALUE_TOO_LONG:Un valor excede la longitud permitida.",
                                "check constraint",
                                "CHECK_CONSTRAINT_VIOLATION:Un valor no cumple con las reglas de validación.",
                                "primary key constraint",
                                "PRIMARY_KEY_VIOLATION:Ya existe un registro con esa clave primaria.");

                String[] errorParts = errorPatterns.entrySet().stream()
                                .filter(entry -> Arrays.stream(entry.getKey().split("\\|"))
                                                .anyMatch(lowerMessage::contains))
                                .findFirst()
                                .map(Map.Entry::getValue)
                                .orElse("DATA_INTEGRITY_ERROR:Error de integridad de datos.")
                                .split(":");

                ErrorResponse errorResponse = new ErrorResponse(

                                errorParts[1],
                                LocalDateTime.now(),
                                Collections.singletonList(rawMessage));

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
                ErrorResponse errorResponse = new ErrorResponse(

                                ex.getMessage(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
                ErrorResponse errorResponse = new ErrorResponse(

                                ex.getMessage(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
                ErrorResponse errorResponse = new ErrorResponse(

                                "An unexpected error occurred",
                                LocalDateTime.now(),
                                Collections.singletonList(ex.getMessage()));
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}