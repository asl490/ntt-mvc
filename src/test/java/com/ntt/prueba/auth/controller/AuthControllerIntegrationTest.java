package com.ntt.prueba.auth.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.prueba.auth.dto.AuthenticationRequest;
import com.ntt.prueba.auth.dto.RegisterRequest;
import com.ntt.prueba.auth.entity.Role;
import com.ntt.prueba.auth.repository.RoleRepository;
import com.ntt.prueba.auth.repository.UserRepository;
import com.ntt.prueba.util.TestDataBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @BeforeEach
        void setUp() {
                // Clean database
                userRepository.deleteAll();
                roleRepository.deleteAll();

                // Create default roles
                Role userRole = Role.builder().name("USER").build();
                Role adminRole = Role.builder().name("ADMIN").build();
                roleRepository.save(userRole);
                roleRepository.save(adminRole);
        }

        // ==================== Register Tests ====================

        @Test
        @DisplayName("POST /api/v1/auth/register - Should register new user successfully")
        void testRegister_Success() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", notNullValue()))
                                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should fail with invalid email")
        void testRegister_InvalidEmail() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.registerRequestWithInvalidEmail().build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should fail with invalid password")
        void testRegister_InvalidPassword() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.registerRequestWithInvalidPassword().build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should fail with empty nombre")
        void testRegister_EmptyNombre() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.defaultRegisterRequest()
                                .nombre("")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should fail with invalid phone number")
        void testRegister_InvalidPhoneNumber() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.registerRequestWithInvalidPhone().build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should fail with duplicate email")
        void testRegister_DuplicateEmail() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();

                // First registration
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                // Act & Assert - Second registration with same email
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Should register with multiple phones")
        void testRegister_MultiplePhones() throws Exception {
                // Arrange
                RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();
                request.getPhones().add(TestDataBuilder.createValidPhoneDTO());

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", notNullValue()));
        }

        // ==================== Authenticate Tests ====================

        @Test
        @DisplayName("POST /api/v1/auth/authenticate - Should fail with invalid credentials")
        void testAuthenticate_InvalidCredentials() throws Exception {
                // Arrange - First register a user
                RegisterRequest registerRequest = TestDataBuilder.defaultRegisterRequest().build();
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk());

                AuthenticationRequest authRequest = AuthenticationRequest.builder()
                                .username(registerRequest.getCorreo())
                                .password("WrongPassword123@")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/auth/authenticate - Should fail with non-existent user")
        void testAuthenticate_UserNotFound() throws Exception {
                // Arrange
                AuthenticationRequest authRequest = AuthenticationRequest.builder()
                                .username("nonexistent@example.com")
                                .password("Password123@")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                                .andExpect(status().isUnauthorized());
        }

        // ==================== Refresh Token Tests ====================

        @Test
        @DisplayName("POST /api/v1/auth/refresh-token - Should refresh token successfully")
        void testRefreshToken_Success() throws Exception {
                // Arrange - Register and get refresh token
                RegisterRequest registerRequest = TestDataBuilder.defaultRegisterRequest().build();
                String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", notNullValue()))
                                .andExpect(jsonPath("$.refreshToken", notNullValue()));
        }

        @Test
        @DisplayName("POST /api/v1/auth/refresh-token - Should fail with invalid token")
        void testRefreshToken_InvalidToken() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"invalid-token\"}"))
                                .andExpect(status().isNotFound());
        }

        // ==================== Logout Tests ====================

        @Test
        @DisplayName("POST /api/v1/auth/logout - Should logout successfully")
        void testLogout_Success() throws Exception {
                // Arrange - Register and get refresh token
                RegisterRequest registerRequest = TestDataBuilder.defaultRegisterRequest().build();
                String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                                .andExpect(status().isOk());

                // Verify token is invalidated by trying to refresh
                mockMvc.perform(post("/api/v1/auth/refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/v1/auth/logout - Should handle invalid token gracefully")
        void testLogout_InvalidToken() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"refreshToken\":\"invalid-token\"}"))
                                .andExpect(status().isOk()); // Should not fail, just do nothing
        }
}
