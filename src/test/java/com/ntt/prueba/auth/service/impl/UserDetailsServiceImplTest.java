package com.ntt.prueba.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        roles = new ArrayList<>();

        Role adminRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ROLE_ADMIN")
                .build();

        Role userRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ROLE_USER")
                .build();

        roles.add(adminRole);
        roles.add(userRole);

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encodedPassword123")
                .name("Test User")
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword123", result.getPassword());
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void testLoadUserByUsername_NoRoles() {
        // Arrange
        User userWithoutRoles = User.builder()
                .id(UUID.randomUUID())
                .username("noroleuser")
                .password("password123")
                .name("No Role User")
                .roles(new ArrayList<>())
                .build();

        when(userRepository.findByUsername("noroleuser")).thenReturn(Optional.of(userWithoutRoles));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("noroleuser");

        // Assert
        assertNotNull(result);
        assertEquals("noroleuser", result.getUsername());
        assertEquals(0, result.getAuthorities().size());

        verify(userRepository, times(1)).findByUsername("noroleuser");
    }

    @Test
    @DisplayName("Should strip ROLE_ prefix from role names")
    void testLoadUserByUsername_RolePrefixStripped() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        // Spring Security's User.builder().roles() automatically adds ROLE_ prefix
        // So we verify the authorities contain the full role names
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
