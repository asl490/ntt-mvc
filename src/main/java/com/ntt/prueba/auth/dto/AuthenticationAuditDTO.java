package com.ntt.prueba.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ntt.prueba.auth.entity.AuthEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationAuditDTO {

    private UUID id;

    private AuthEventType eventType;

    private LocalDateTime eventTime;

    private String ipAddress;

    private String userAgent;

    private Boolean successful;
}
