package com.ntt.prueba.auth.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.Permission;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface PermissionRepository extends BaseJpaRepository<Permission> {
    Optional<Permission> findByName(String name);
}
