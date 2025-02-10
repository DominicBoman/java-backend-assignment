package com.building.temperaturecontrol.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.building.temperaturecontrol.model.User;
import com.building.temperaturecontrol.repository.UserRepository;
import com.building.temperaturecontrol.dto.UserDTO;
import com.building.temperaturecontrol.exception.UsernameAlreadyExistsException;
import com.building.temperaturecontrol.exception.InvalidCredentialsException;
import com.building.temperaturecontrol.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "hashedPassword", "John", "Doe");
        testUserDTO = new UserDTO(1L, "testuser", "John", "Doe");
    }

    @Test
    void createUser_WithValidData_ShouldReturnUserDTO() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.createUser("testuser", "password123", "John", "Doe");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> 
            userService.createUser("testuser", "password123", "John", "Doe")
        );
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        String token = userService.authenticate("testuser", "password123");

        // Assert
        assertEquals(expectedToken, token);
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () ->
            userService.authenticate("testuser", "wrongpassword")
        );
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(
            new User(1L, "testuser", "hashedPassword", "Jane", "Smith")
        );

        // Act
        UserDTO result = userService.updateUser(testUserDTO);

        // Assert
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowException() {
        // Arrange
        UserDTO updateDTO = new UserDTO(999L, "testuser", "Jane", "Smith");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            userService.updateUser(updateDTO)
        );
    }
} 