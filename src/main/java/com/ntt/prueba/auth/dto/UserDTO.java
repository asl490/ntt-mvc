package com.ntt.prueba.auth.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ntt.prueba.auth.validation.ValidPassword;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;

    private String username;

    private String name;

    private List<String> roles;

    private List<PhoneDTO> phones;

    private LocalDateTime lastlogin;

    private Boolean isActive;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateUserDTO {

        @Email(message = "Debe ser un correo electrónico válido")
        @NotBlank(message = "El username no puede estar vacío")
        private String username;

        @NotBlank(message = "El nombre no puede estar vacío")
        private String name;

        @ValidPassword
        private String password;

        private List<String> roleNames;

        @Valid
        private List<PhoneDTO.CreatePhoneUserDTO> phones;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateUserDTO {

        @Email(message = "Debe ser un correo electrónico válido")
        private String username;

        private String name;

        @ValidPassword
        private String password;

        private List<String> roleNames;

        @Valid
        private List<PhoneDTO.CreatePhoneUserDTO> phones;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FiltersUserDTO {

        private String username;

        private String name;
    }
}
