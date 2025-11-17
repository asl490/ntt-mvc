package com.ntt.prueba.auth.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface UserRepository extends BaseJpaRepository<User> {
    Optional<User> findByUsername(String username);
}
