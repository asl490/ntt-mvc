package com.ntt.prueba.auth.entity;

import com.ntt.prueba.shared.Auditable;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Phone extends Auditable {

    private Long person;

    private String number;

    private String cityCode;

    private String countryCode;

}