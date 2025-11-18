package com.ntt.prueba.auth.service;

import java.util.Optional;
import java.util.UUID;

import com.ntt.prueba.auth.entity.User;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByUsername(String username);

    User updateUser(UUID id, User user);

    void deleteUser(UUID id);
}
