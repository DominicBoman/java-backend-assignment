package com.building.temperaturecontrol.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthenticationRequestDTO() {}

    public AuthenticationRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
} 