package com.ntt.prueba.auth.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.auth.dto.UserDTO;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.RoleRepository;
import com.ntt.prueba.shared.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = PhoneMapper.class)
public abstract class UserMapper implements BaseMapper<User, UserDTO, UserDTO.CreateUserDTO, UserDTO.UpdateUserDTO> {

    @Autowired
    protected RoleRepository roleRepository;

    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToRoleNames")
    @Mapping(target = "isActive", expression = "java(!entity.getIsDeleted())")
    public abstract UserDTO toDTO(User entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "lastlogin", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be set by service
    @Mapping(target = "roles", source = "roleNames", qualifiedByName = "roleNamesToRoles")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "createPhoneDTOsToPhones")
    public abstract User toEntity(UserDTO.CreateUserDTO dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "lastlogin", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be set by service if provided
    @Mapping(target = "roles", source = "roleNames", qualifiedByName = "roleNamesToRoles")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "createPhoneDTOsToPhones")
    public abstract void updateEntityFromDTO(UserDTO.UpdateUserDTO dto, @MappingTarget User entity);

    @Named("rolesToRoleNames")
    protected List<String> rolesToRoleNames(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Named("roleNamesToRoles")
    protected List<Role> roleNamesToRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return null;
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + name)))
                .collect(Collectors.toList());
    }

    @Named("createPhoneDTOsToPhones")
    protected List<Phone> createPhoneDTOsToPhones(List<PhoneDTO.CreatePhoneUserDTO> phoneDTOs) {
        if (phoneDTOs == null) {
            return null;
        }
        return phoneDTOs.stream()
                .map(dto -> Phone.builder()
                        .number(dto.getNumber())
                        .cityCode(dto.getCityCode())
                        .countryCode(dto.getCountryCode())
                        .build())
                .collect(Collectors.toList());
    }
}
