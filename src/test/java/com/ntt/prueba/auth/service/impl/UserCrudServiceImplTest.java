package com.ntt.prueba.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.auth.dto.UserDTO;
import com.ntt.prueba.auth.dto.UserDTO.CreateUserDTO;
import com.ntt.prueba.auth.dto.UserDTO.UpdateUserDTO;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.mapper.UserMapper;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.exception.exception.BaseException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCrudServiceImpl Tests")
class UserCrudServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCrudServiceImpl userCrudService;

    private User user;
    private UserDTO userDTO;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    private UUID userId;
    private List<Role> roles;
    private List<Phone> phones;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        roles = new ArrayList<>();
        Role userRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ROLE_USER")
                .build();
        roles.add(userRole);

        phones = new ArrayList<>();
        Phone phone = Phone.builder()
                .id(UUID.randomUUID())
                .number("123456789")
                .cityCode("11")
                .countryCode("57")
                .build();
        phones.add(phone);

        user = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .password("encodedPassword")
                .roles(roles)
                .phones(phones)
                .build();

        // Set bidirectional relationship
        for (Phone p : phones) {
            p.setUser(user);
        }

        userDTO = UserDTO.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .build();

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("newuser");
        createUserDTO.setName("New User");
        createUserDTO.setPassword("password123");

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setName("Updated User");
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(CreateUserDTO.class))).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.create(createUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void testCreateUser_UsernameExists() {
        // Arrange
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(user));

        // Act & Assert
        BaseException exception = assertThrows(
                BaseException.class,
                () -> userCrudService.create(createUserDTO));

        assertEquals("Username already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user with phones successfully")
    void testCreateUser_WithPhones() {
        // Arrange
        User userWithPhones = User.builder()
                .id(userId)
                .username("newuser")
                .name("New User")
                .password("rawPassword")
                .phones(phones)
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(CreateUserDTO.class))).thenReturn(userWithPhones);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userWithPhones);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.create(createUserDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        // Verify that bidirectional relationship was set
        for (Phone phone : userWithPhones.getPhones()) {
            assertEquals(userWithPhones, phone.getUser());
        }
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.update(userId.toString(), updateUserDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(
                BaseException.class,
                () -> userCrudService.update(userId.toString(), updateUserDTO));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating username to existing one")
    void testUpdateUser_UsernameExists() {
        // Arrange
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .username("updateduser")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.of(anotherUser));

        // Act & Assert
        BaseException exception = assertThrows(
                BaseException.class,
                () -> userCrudService.update(userId.toString(), updateUserDTO));

        assertEquals("Username already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user password when provided")
    void testUpdateUser_WithPassword() {
        // Arrange
        updateUserDTO.setPassword("newPassword123");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.update(userId.toString(), updateUserDTO);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user with roles when provided")
    void testUpdateUser_WithRoles() {
        // Arrange
        List<String> roleNames = List.of("ROLE_ADMIN", "ROLE_USER");
        updateUserDTO.setRoleNames(roleNames);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        doNothing().when(userMapper).updateEntityFromDTO(any(UpdateUserDTO.class), any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.update(userId.toString(), updateUserDTO);

        // Assert
        assertNotNull(result);
        verify(userMapper, times(1)).updateEntityFromDTO(updateUserDTO, user);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user with phones when provided")
    void testUpdateUser_WithPhones() {
        // Arrange
        List<PhoneDTO.CreatePhoneUserDTO> phoneDTOs = new ArrayList<>();
        PhoneDTO.CreatePhoneUserDTO phoneDTO = new PhoneDTO.CreatePhoneUserDTO();
        phoneDTO.setNumber("987654321");
        phoneDTO.setCityCode("1");
        phoneDTO.setCountryCode("1");
        phoneDTOs.add(phoneDTO);

        updateUserDTO.setPhones(phoneDTOs);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.update(userId.toString(), updateUserDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow updating username to same value")
    void testUpdateUser_SameUsername() {
        // Arrange
        updateUserDTO.setUsername("testuser"); // Same as existing username

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userCrudService.update(userId.toString(), updateUserDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByUsername(anyString()); // Should not check for existing username
        verify(userRepository, times(1)).save(any(User.class));
    }
}
