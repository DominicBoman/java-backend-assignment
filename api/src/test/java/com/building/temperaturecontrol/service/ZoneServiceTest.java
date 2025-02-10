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

import com.building.temperaturecontrol.model.Zone;
import com.building.temperaturecontrol.model.Building;
import com.building.temperaturecontrol.model.User;
import com.building.temperaturecontrol.repository.ZoneRepository;
import com.building.temperaturecontrol.repository.BuildingRepository;
import com.building.temperaturecontrol.dto.ZoneDTO;
import com.building.temperaturecontrol.dto.ZoneTemperatureUpdateDTO;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ZoneService zoneService;

    private User testUser;
    private Building testBuilding;
    private Zone testZone;
    private ZoneDTO testZoneDTO;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "password", "John", "Doe");
        testBuilding = new Building(1L, "Test Building", "Test City", "Test Street", "12345", testUser);
        testZone = new Zone(1L, "Test Zone", "Test Description", testBuilding);
        testZone.setTargetTemperature(new BigDecimal("22.0"));
        testZoneDTO = new ZoneDTO(1L, "Test Zone", "Test Description", 1L, 
            new BigDecimal("22.0"), new BigDecimal("21.0"));

        // Setup Security Context
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createZone_WithValidData_ShouldReturnZoneDTO() {
        // Arrange
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(testBuilding));
        when(zoneRepository.save(any(Zone.class))).thenReturn(testZone);

        // Act
        ZoneDTO result = zoneService.createZone(testZoneDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testZoneDTO.getName(), result.getName());
        assertEquals(testZoneDTO.getTargetTemperature(), result.getTargetTemperature());
    }

    @Test
    void getZonesByBuilding_WithValidAccess_ShouldReturnZones() {
        // Arrange
        List<Zone> zones = Arrays.asList(testZone);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(testBuilding));
        when(zoneRepository.findByBuildingId(1L)).thenReturn(zones);

        // Act
        List<ZoneDTO> result = zoneService.getZonesByBuilding(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testZoneDTO.getName(), result.get(0).getName());
    }

    @Test
    void updateTemperature_WithValidAccess_ShouldUpdateTemperature() {
        // Arrange
        BigDecimal newTemp = new BigDecimal("23.5");
        ZoneTemperatureUpdateDTO updateDTO = new ZoneTemperatureUpdateDTO(newTemp);
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(testZone));
        when(zoneRepository.save(any(Zone.class))).thenReturn(testZone);

        // Act
        ZoneDTO result = zoneService.updateTemperature(1L, 1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(newTemp, result.getTargetTemperature());
    }

    @Test
    void updateTemperature_WithoutAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "password", "Jane", "Smith");
        Building otherBuilding = new Building(2L, "Other Building", "Other City", "Other Street", "54321", otherUser);
        Zone otherZone = new Zone(1L, "Other Zone", "Other Description", otherBuilding);
        otherZone.setTargetTemperature(new BigDecimal("20.0"));
        
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(otherZone));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            zoneService.updateTemperature(2L, 1L, new ZoneTemperatureUpdateDTO(new BigDecimal("23.5")))
        );
    }

    @Test
    void deleteZone_WithValidAccess_ShouldDeleteZone() {
        // Arrange
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(testZone));
        doNothing().when(zoneRepository).delete(testZone);

        // Act & Assert
        assertDoesNotThrow(() -> zoneService.deleteZone(1L, 1L));
        verify(zoneRepository).delete(testZone);
    }

    @Test
    void deleteZone_WithoutAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "password", "Jane", "Smith");
        Building otherBuilding = new Building(1L,"Other Building", "Other City", "Other Street", "54321", otherUser);
        Zone otherZone = new Zone(1L,"Other Zone", "Other Description", otherBuilding);
        
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(otherZone));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            zoneService.deleteZone(1L, 1L)
        );
        verify(zoneRepository, never()).delete(any());
    }
} 