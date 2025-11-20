package com.ntt.prueba.auth.service;

import com.ntt.prueba.auth.dto.UserDTO;
import com.ntt.prueba.shared.BaseService;

public interface UserCrudService
        extends BaseService<UserDTO.CreateUserDTO, UserDTO.UpdateUserDTO, UserDTO, UserDTO.FiltersUserDTO> {
    // Additional custom methods can be added here if needed
}
