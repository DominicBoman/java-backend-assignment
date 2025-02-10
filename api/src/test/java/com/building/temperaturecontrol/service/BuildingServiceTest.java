package com.building.temperaturecontrol.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.building.temperaturecontrol.model.Building;
import com.building.temperaturecontrol.model.User;
import com.building.temperaturecontrol.repository.BuildingRepository;
import com.building.temperaturecontrol.repository.UserRepository;
import com.building.temperaturecontrol.repository.ZoneRepository;
import com.building.temperaturecontrol.dto.BuildingDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BuildingService buildingService;

    private User testUser;
    private Building testBuilding;
    private BuildingDTO testBuildingDTO;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "password", "John", "Doe");
        testBuilding = new Building("Test Building", "Test City", "Test Street", "12345", testUser);
        testBuildingDTO = new BuildingDTO(1L, "Test Building", 1L, "Test City", "Test Street", "12345", List.of());

        // Setup Security Context
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createBuilding_WithValidData_ShouldReturnBuildingDTO() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(buildingRepository.save(any(Building.class))).thenReturn(testBuilding);

        // Act
        BuildingDTO result = buildingService.createBuilding(testBuildingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testBuildingDTO.getName(), result.getName());
        assertEquals(testBuildingDTO.getCity(), result.getCity());
    }

    @Test
    void getCurrentUserBuildings_ShouldReturnListOfBuildings() {
        // Arrange
        List<Building> buildings = Arrays.asList(testBuilding);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(buildingRepository.findByOwnerId(testUser.getId())).thenReturn(buildings);

        // Act
        List<BuildingDTO> result = buildingService.getCurrentUserBuildings();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBuildingDTO.getName(), result.get(0).getName());
    }

    @Test
    void getBuilding_WithValidIdAndAccess_ShouldReturnBuilding() {
        // Arrange
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(testBuilding));

        // Act
        BuildingDTO result = buildingService.getBuilding(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testBuildingDTO.getName(), result.getName());
    }

    @Test
    void getBuilding_WithoutAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "password", "Jane", "Smith");
        Building otherBuilding = new Building(1L,"Other Building", "Other City", "Other Street", "54321", otherUser);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(otherBuilding));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            buildingService.getBuilding(1L)
        );
    }

    @Test
    void deleteBuilding_WithValidIdAndAccess_ShouldDeleteBuilding() {
        // Arrange
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(testBuilding));
        doNothing().when(buildingRepository).delete(testBuilding);

        // Act & Assert
        assertDoesNotThrow(() -> buildingService.deleteBuilding(1L));
        verify(buildingRepository).delete(testBuilding);
    }

    @Test
    void deleteBuilding_WithoutAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "password", "Jane", "Smith");
        Building otherBuilding = new Building("Other Building", "Other City", "Other Street", "54321", otherUser);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(otherBuilding));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            buildingService.deleteBuilding(1L)
        );
        verify(buildingRepository, never()).delete(any());
    }
} 