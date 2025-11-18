package com.ntt.prueba.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;

    public ErrorResponse(String message, LocalDateTime timestamp) {

        this.message = message;
        this.timestamp = timestamp;
    }

}