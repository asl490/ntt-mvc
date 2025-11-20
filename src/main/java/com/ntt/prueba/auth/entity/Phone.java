package com.ntt.prueba.auth.entity;

import com.ntt.prueba.shared.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // @Column(length = 14)
    private String number;

    // @Column(length = 4)
    private String cityCode;

    // @Column(length = 4)
    private String countryCode;

}