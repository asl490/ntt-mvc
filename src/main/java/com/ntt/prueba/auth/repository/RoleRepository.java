package com.ntt.prueba.auth.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface RoleRepository extends BaseJpaRepository<Role> {
    Optional<Role> findByName(String name);
}
