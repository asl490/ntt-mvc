package com.ntt.prueba.config;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.repository.RoleRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;

    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        // Rol de Usuario (USER)
        createRoleIfNotFound("USER");

        // Rol de Administrador (ADMIN)
        createRoleIfNotFound("ADMIN");
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
}