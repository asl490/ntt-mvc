package com.ntt.prueba.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.Permission;

@Repository
public interface SpringDataSqlServerPermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
