package com.building.temperaturecontrol.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String testUsername;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testUsername = "testuser";
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateToken(testUsername);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtService.generateToken(testUsername);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void generateToken_ShouldCreateTokenWithCorrectClaims() {
        // Act
        String token = jwtService.generateToken(testUsername);

        // Assert
        assertAll(
            () -> assertNotNull(jwtService.extractUsername(token)),
            () -> assertEquals(testUsername, jwtService.extractUsername(token))
        );
    }

    @Test
    void generateToken_ShouldCreateDifferentTokensForDifferentUsers() {
        // Arrange
        String user1 = "user1";
        String user2 = "user2";

        // Act
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    void generateToken_ShouldCreateDifferentTokensForSameUser() {
        // Act
        String token1 = jwtService.generateToken(testUsername);
        String token2 = jwtService.generateToken(testUsername);

        // Assert
        assertNotEquals(token1, token2, "Tokens should be different even for the same user");
    }
}
