package com.ntt.prueba.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.prueba.auth.entity.Permission;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.repository.PermissionRepository;
import com.ntt.prueba.auth.repository.RoleRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // --- Crear Permisos ---
        Permission readUsers = createPermissionIfNotFound("READ_USERS");
        Permission createUsers = createPermissionIfNotFound("CREATE_USERS");
        Permission readProducts = createPermissionIfNotFound("READ_PRODUCTS");
        Permission writeProducts = createPermissionIfNotFound("WRITE_PRODUCTS");

        // --- Crear Roles y Asignar Permisos ---

        // Rol de Usuario (USER)
        createRoleIfNotFound("USER", new HashSet<>(Arrays.asList(readProducts)));

        // Rol de Administrador (ADMIN)
        createRoleIfNotFound("ADMIN", new HashSet<>(Arrays.asList(
                readUsers,
                createUsers,
                readProducts,
                writeProducts)));
    }

    @Transactional
    private Permission createPermissionIfNotFound(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(Permission.builder().name(name).build()));
    }

    @Transactional
    private void createRoleIfNotFound(String name, Set<Permission> permissions) {
        Optional<Role> roleOptional = roleRepository.findByName(name);
        if (roleOptional.isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .permissions(permissions)
                    .build();
            roleRepository.save(role);
        }
    }
}