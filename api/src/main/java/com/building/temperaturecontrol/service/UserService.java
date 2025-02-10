package com.building.temperaturecontrol.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.building.temperaturecontrol.model.User;
import com.building.temperaturecontrol.repository.UserRepository;
import com.building.temperaturecontrol.dto.UserDTO;
import com.building.temperaturecontrol.exception.UsernameAlreadyExistsException;
import com.building.temperaturecontrol.exception.ResourceNotFoundException;
import com.building.temperaturecontrol.exception.InvalidCredentialsException;

// User Service
// This service is responsible for managing users in the system.
// It provides methods to create, update, and authenticate users.
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Create User
    public UserDTO createUser(String username, String password, String firstName, String lastName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username '" + username + "' is already taken");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword, firstName, lastName);
        userRepository.save(user);
        return convertToDTO(user);
    }

    // Authenticate User
    public String authenticate(String username, String rawPassword) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            logger.info("Successful authentication for user: {}", username);
            return jwtService.generateToken(username);
        }
        logger.warn("Failed authentication attempt for user: {}", username);
        throw new InvalidCredentialsException("Invalid username or password");
    }

    // Update User
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        return convertToDTO(userRepository.save(user));
    }

    // Convert User to UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName()
        );
    }
}
