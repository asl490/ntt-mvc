package com.ntt.prueba.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.prueba.auth.dto.UserDTO;
import com.ntt.prueba.auth.service.UserCrudService;
import com.ntt.prueba.shared.BaseController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
@PreAuthorize(value = "hasRole('ADMIN')")
public class UserController
        extends BaseController<UserDTO.CreateUserDTO, UserDTO.UpdateUserDTO, UserDTO, UserDTO.FiltersUserDTO> {

    public UserController(UserCrudService service) {
        super(service);
    }

    @Override
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information. Requires authentication.")
    public ResponseEntity<UserDTO> create(UserDTO.CreateUserDTO request) {
        return super.create(request);
    }

    @Override
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier. Requires authentication.")
    public ResponseEntity<UserDTO> getById(String id) {
        return super.getById(id);
    }

    @Override
    @Operation(summary = "Get all users", description = "Retrieves all users. Requires authentication.")
    public ResponseEntity<List<UserDTO>> getAll() {
        return super.getAll();
    }

    @Override
    @Operation(summary = "Get all users (paginated)", description = "Retrieves all users with pagination. Requires authentication.")
    public ResponseEntity<com.ntt.prueba.shared.PagedResponse<UserDTO>> getAllPaged(int page,
            int size) {
        return super.getAllPaged(page, size);
    }

    @Override
    @Operation(summary = "Get filtered users (paginated)", description = "Retrieves filtered users with pagination. Requires authentication.")
    public ResponseEntity<com.ntt.prueba.shared.PagedResponse<UserDTO>> getAllFilteredPagedByDto(
            int page, int size, UserDTO.FiltersUserDTO filters) {
        return super.getAllFilteredPagedByDto(page, size, filters);
    }

    @Override
    @Operation(summary = "Update user", description = "Updates an existing user. Requires authentication.")
    public ResponseEntity<UserDTO> update(String id, UserDTO.UpdateUserDTO request) {
        return super.update(id, request);
    }

    @Override
    @Operation(summary = "Delete user", description = "Soft deletes a user. Requires authentication.")
    public ResponseEntity<Void> delete(String id) {
        return super.delete(id);
    }
}
