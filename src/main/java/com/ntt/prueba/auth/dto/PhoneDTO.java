package com.ntt.prueba.auth.dto;

import java.util.UUID;

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

        private String number;

        private String cityCode;

        private String countryCode;

    }

    @Data
    public static class CreatePhoneUserDTO {

        private String number;

        private String cityCode;

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