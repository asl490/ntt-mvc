package com.ntt.prueba.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.RefreshToken;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface RefreshTokenRepository extends BaseJpaRepository<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID userId);

    void deleteByUser(User user);
}