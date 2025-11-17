package com.ntt.prueba.auth.service;

import java.util.Optional;

import com.ntt.prueba.auth.entity.User;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
