package com.ntt.prueba.auth.repository;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface PhoneRepository extends BaseJpaRepository<Phone> {
}