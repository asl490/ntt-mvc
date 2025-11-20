package com.ntt.prueba.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.exception.exception.BaseException;
import com.ntt.prueba.util.TestDataBuilder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.defaultUser().build();
    }

    @Test
    @DisplayName("Should create user with encoded password")
    void testCreateUser() {
        // Arrange
        User userToCreate = TestDataBuilder.defaultUser()
                .password("PlainPassword123@")
                .build();

        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        verify(passwordEncoder, times(1)).encode("PlainPassword123@");
        verify(userRepository, times(1)).save(userToCreate);
    }

    @Test
    @DisplayName("Should get user by ID when user exists")
    void testGetUserById_UserExists() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(testUser.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by ID")
    void testGetUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(testUser.getId());

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    @DisplayName("Should get user by username when user exists")
    void testGetUserByUsername_UserExists() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByUsername(testUser.getUsername());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by username")
    void testGetUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByUsername("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        // Arrange
        User updatedData = TestDataBuilder.defaultUser()
                .username("updated@example.com")
                .name("Updated Name")
                .password("NewPassword123@")
                .build();

        String encodedPassword = "$2a$10$newEncodedPassword";
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPassword123@")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(testUser.getId(), updatedData);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(passwordEncoder, times(1)).encode("NewPassword123@");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not encode password when password is null during update")
    void testUpdateUser_NullPassword() {
        // Arrange
        User updatedData = TestDataBuilder.defaultUser()
                .password(null)
                .build();

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(testUser.getId(), updatedData);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not encode password when password is empty during update")
    void testUpdateUser_EmptyPassword() {
        // Arrange
        User updatedData = TestDataBuilder.defaultUser()
                .password("")
                .build();

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(testUser.getId(), updatedData);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUser_UserNotFound() {
        // Arrange
        User updatedData = TestDataBuilder.defaultUser().build();
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            userService.updateUser(testUser.getId(), updatedData);
        });

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUser(testUser.getId());

        // Assert
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            userService.deleteUser(testUser.getId());
        });

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, never()).deleteById(any());
    }
}
