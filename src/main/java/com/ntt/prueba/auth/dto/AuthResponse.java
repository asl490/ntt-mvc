package com.ntt.prueba.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private UUID id;
    private String accessToken;
    private String refreshToken;

    private LocalDateTime ultimoLogin;
    private LocalDateTime creado;
    private LocalDateTime modificado;
    private Boolean activo;

}