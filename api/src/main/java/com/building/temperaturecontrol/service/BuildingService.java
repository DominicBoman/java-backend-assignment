package com.building.temperaturecontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.building.temperaturecontrol.model.Building;
import com.building.temperaturecontrol.model.User;
import com.building.temperaturecontrol.repository.BuildingRepository;
import com.building.temperaturecontrol.repository.UserRepository;
import com.building.temperaturecontrol.dto.BuildingDTO;
import com.building.temperaturecontrol.dto.ZoneDTO;
import com.building.temperaturecontrol.repository.ZoneRepository;

import java.util.List;
import java.util.stream.Collectors;

// Building Service
// This service is responsible for managing buildings in the system.
// It provides methods to create, update, delete, and retrieve buildings.
// It also verifies that the user has access to the building.
@Service
public class BuildingService {
    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;

    public BuildingService(BuildingRepository buildingRepository, UserRepository userRepository, ZoneRepository zoneRepository) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
    }

    // Create building
    public BuildingDTO createBuilding(BuildingDTO buildingDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Building building = new Building(
            buildingDTO.getName(),
            buildingDTO.getCity(),
            buildingDTO.getStreet(),
            buildingDTO.getPostalCode(),
            owner
        );
        building = buildingRepository.save(building);
        
        logger.info("Created new building: {} for owner: {}", building.getName(), owner.getUsername());
        
        // Return the BuildingDTO without zones
        return new BuildingDTO(
            building.getId(),
            building.getName(),
            building.getOwner().getId(),
            building.getCity(),
            building.getStreet(),
            building.getPostalCode(),
            List.of() // Return an empty list for zones
        );
    }

    // Get Current User Buildings
    public List<BuildingDTO> getCurrentUserBuildings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        List<Building> buildings = buildingRepository.findByOwnerId(owner.getId());
        return buildings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get Building
    public BuildingDTO getBuilding(Long buildingId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        verifyUserHasAccessToBuilding(building, username);

        return convertToDTO(building);
    }

    // Delete Building
    public void deleteBuilding(Long buildingId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        verifyUserHasAccessToBuilding(building, username);

        buildingRepository.delete(building);
        logger.info("Deleted building: {} and its associated zones", building.getName());
    }

    // Verify user has access to building
    private void verifyUserHasAccessToBuilding(Building building, String username) {
        if (!building.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to access this building");
        }
    }

    // Convert Building to BuildingDTO
    private BuildingDTO convertToDTO(Building building) {
        List<ZoneDTO> zones = zoneRepository.findByBuildingId(building.getId()).stream()
                .map(zone -> new ZoneDTO(
                        zone.getId(),
                        zone.getName(),
                        zone.getDescription(),
                        zone.getBuilding().getId(),
                        zone.getTargetTemperature(),
                        zone.getCurrentTemperature()
                ))
                .collect(Collectors.toList());

        return new BuildingDTO(
            building.getId(),
            building.getName(),
            building.getOwner().getId(),
            building.getCity(),
            building.getStreet(),
            building.getPostalCode(),
            zones
        );
    }
} 