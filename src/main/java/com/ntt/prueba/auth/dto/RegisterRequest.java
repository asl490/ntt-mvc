package com.ntt.prueba.auth.dto;

import java.util.List;

import com.ntt.prueba.auth.validation.ValidPassword;

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
public class RegisterRequest {

    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @ValidPassword
    private String password;

    private List<String> roleNames;

    private List<PhoneDTO.CreatePhoneUserDTO> phones;
}