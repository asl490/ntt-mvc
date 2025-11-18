package com.ntt.prueba.exception.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;
    private Exception cause;

    public BaseException(String message, HttpStatus status, Exception cause) {
        super(message);
        this.message = message;
        this.httpStatus = status;
        this.cause = cause;
    }

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.httpStatus = status;
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}