package com.ntt.prueba.auth.dto;

import java.util.UUID;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDTO {
    private UUID id;

    private UUID user;

    private String number;

    private String cityCode;

    private String countryCode;

    @Data
    public static class CreatePhoneDTO {
        private UUID user;

        @Pattern(regexp = "\\d{8,12}", message = "El número debe tener entre 8 y 12 dígitos")
        private String number;

        @Pattern(regexp = "\\d{1,4}", message = "El código de ciudad debe tener entre 1 y 4 dígitos")
        private String cityCode;

        @Pattern(regexp = "\\d{1,4}", message = "El código de país debe tener entre 1 y 4 dígitos")
        private String countryCode;

    }

    @Data
    public static class CreatePhoneUserDTO {

        @Pattern(regexp = "\\d{8,12}", message = "El número debe tener entre 8 y 12 dígitos")
        private String number;

        @Pattern(regexp = "\\d{1,4}", message = "El código de ciudad debe tener entre 1 y 4 dígitos")
        private String cityCode;

        @Pattern(regexp = "\\d{1,4}", message = "El código de país debe tener entre 1 y 4 dígitos")
        private String countryCode;

    }

    @Data
    public static class UpdatePhoneDTO {

        private UUID user;

        private String number;

        private String cityCode;

        private String countryCode;

    }

    @Data
    public static class FiltersPhoneDTO {

        private String number;

        private String cityCode;

        private String countryCode;

    }
}