package com.building.temperaturecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

import com.building.temperaturecontrol.service.UserService;
import com.building.temperaturecontrol.dto.UserDTO;
import com.building.temperaturecontrol.dto.UserRegistrationDTO;
import com.building.temperaturecontrol.dto.AuthenticationRequestDTO;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final String apiContentType = "application/vnd.temperaturecontrol.v1+json";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", produces = apiContentType)
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        logger.info("Received registration request for username: {}", userRegistrationDTO.getUsername());
        UserDTO createdUser = userService.createUser(
                userRegistrationDTO.getUsername(),
                userRegistrationDTO.getPassword(),
                userRegistrationDTO.getFirstName(),
                userRegistrationDTO.getLastName()
        );
        logger.info("Successfully registered user: {}", userRegistrationDTO.getUsername());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping(value = "/authenticate", produces = apiContentType)
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody @Valid AuthenticationRequestDTO authRequest) {
        String token = userService.authenticate(
                authRequest.getUsername(),
                authRequest.getPassword()
        );
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping(value = "/{userId}", produces = apiContentType)
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDTO userDTO) {
        UserDTO updated = userService.updateUser(userDTO);
        return ResponseEntity.ok(updated);
    }
}
