package com.ntt.prueba.auth.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.prueba.auth.dto.UserDTO;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.mapper.UserMapper;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.auth.service.UserCrudService;
import com.ntt.prueba.exception.exception.BaseException;
import com.ntt.prueba.shared.BaseServiceImpl;

@Service
@Transactional
public class UserCrudServiceImpl
        extends BaseServiceImpl<User, UserDTO.CreateUserDTO, UserDTO.UpdateUserDTO, UserDTO, UserDTO.FiltersUserDTO>
        implements UserCrudService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserCrudServiceImpl(UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
        this.userRepository = repository;
    }

    @Override
    public UserDTO create(UserDTO.CreateUserDTO request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BaseException("Username already exists", HttpStatus.CONFLICT);
        }

        // Map DTO to entity
        User user = mapper.toEntity(request);

        // Encode password
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Set bidirectional relationship for phones
        if (user.getPhones() != null) {
            for (Phone phone : user.getPhones()) {
                phone.setUser(user);
            }
        }

        // Save and return
        User savedUser = repository.save(user);
        return mapper.toDTO(savedUser);
    }

    @Override
    public UserDTO update(String id, UserDTO.UpdateUserDTO request) {
        User existingUser = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BaseException("User not found", HttpStatus.NOT_FOUND));

        // Check if username is being changed and if it already exists
        if (request.getUsername() != null &&
                !request.getUsername().equals(existingUser.getUsername()) &&
                userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BaseException("Username already exists", HttpStatus.CONFLICT);
        }

        // Update basic fields
        if (request.getUsername() != null) {
            existingUser.setUsername(request.getUsername());
        }
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoleNames() != null) {
            mapper.updateEntityFromDTO(request, existingUser);
        }

        // Update phones if provided
        if (request.getPhones() != null) {
            // Remove old phones
            if (existingUser.getPhones() != null) {
                existingUser.getPhones().clear();
            }

            // Add new phones
            List<Phone> newPhones = request.getPhones().stream()
                    .map(dto -> Phone.builder()
                            .number(dto.getNumber())
                            .cityCode(dto.getCityCode())
                            .countryCode(dto.getCountryCode())
                            .user(existingUser)
                            .build())
                    .collect(Collectors.toList());

            if (existingUser.getPhones() != null) {
                existingUser.getPhones().addAll(newPhones);
            } else {
                existingUser.setPhones(newPhones);
            }
        }

        User savedUser = repository.save(existingUser);
        return mapper.toDTO(savedUser);
    }
}
