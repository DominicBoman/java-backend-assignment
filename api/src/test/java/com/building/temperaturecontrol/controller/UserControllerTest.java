package com.building.temperaturecontrol.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.building.temperaturecontrol.config.TestSecurityConfig;
import com.building.temperaturecontrol.dto.UserRegistrationDTO;
import com.building.temperaturecontrol.dto.AuthenticationRequestDTO;
import com.building.temperaturecontrol.dto.UserDTO;
import com.building.temperaturecontrol.exception.UsernameAlreadyExistsException;
import com.building.temperaturecontrol.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.building.temperaturecontrol.exception.InvalidCredentialsException;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void register_WithValidData_ShouldReturnCreated() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "testuser",
            "password123",
            "John",
            "Doe"
        );

        when(userService.createUser(
            any(),
            any(),
            any(),
            any()
        )).thenReturn(new UserDTO(1L, "testuser", "John", "Doe"));

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"));
    }

    @Test
    void register_WithExistingUsername_ShouldReturnConflict() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "existinguser",
            "password123",
            "John",
            "Doe"
        );

        when(userService.createUser(any(), any(), any(), any()))
            .thenThrow(new UsernameAlreadyExistsException("Username already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "", // invalid username
            "pwd", // too short password
            "",
            ""
        );

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() throws Exception {
        String username = "testuser";
        String password = "password123";
        String token = "test.jwt.token";

        when(userService.authenticate(username, password)).thenReturn(token);

        mockMvc.perform(post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(new AuthenticationRequestDTO(username, password))))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        String username = "testuser";
        String password = "wrongpassword";

        when(userService.authenticate(username, password))
            .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(new AuthenticationRequestDTO(username, password))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void authenticate_WithMissingCredentials_ShouldReturnBadRequest() throws Exception {
        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("", "");

        mockMvc.perform(post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
} 