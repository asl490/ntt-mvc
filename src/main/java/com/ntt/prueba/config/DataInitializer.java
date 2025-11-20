package com.ntt.prueba.config;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.RoleRepository;
import com.ntt.prueba.auth.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        // Rol de Usuario (USER)
        createRoleIfNotFound("USER");

        // Rol de Administrador (ADMIN)
        createRoleIfNotFound("ADMIN");

        // Usuario Admin
        createUserIfNotFound("admin@admin.com", "admin", "ADMIN");
    }

    @Transactional
    private void createRoleIfNotFound(String name) {
        Optional<Role> roleOptional = roleRepository.findByName(name);
        if (roleOptional.isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .build();
            roleRepository.save(role);
        }
    }

    @Transactional
    private void createUserIfNotFound(String username, String password, String roleName) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));

            User user = User.builder()
                    .name("Admin")
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
        }
    }
}