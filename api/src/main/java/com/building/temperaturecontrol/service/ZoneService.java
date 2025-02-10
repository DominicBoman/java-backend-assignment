package com.building.temperaturecontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.building.temperaturecontrol.model.Zone;
import com.building.temperaturecontrol.model.Building;
import com.building.temperaturecontrol.repository.ZoneRepository;
import com.building.temperaturecontrol.repository.BuildingRepository;
import com.building.temperaturecontrol.dto.ZoneDTO;
import com.building.temperaturecontrol.dto.ZoneTemperatureUpdateDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// Zone Service
// This service is responsible for managing zones in a building.
// It provides methods to create, update, delete, and retrieve zones.
// It also verifies that the user has access to the building and zone.
@Service
public class ZoneService {
    private static final Logger logger = LoggerFactory.getLogger(ZoneService.class);

    private final ZoneRepository zoneRepository;
    private final BuildingRepository buildingRepository;

    public ZoneService(ZoneRepository zoneRepository, BuildingRepository buildingRepository) {
        this.zoneRepository = zoneRepository;
        this.buildingRepository = buildingRepository;
    }

    // Create Zone
    public ZoneDTO createZone(ZoneDTO zoneDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Building building = buildingRepository.findById(zoneDTO.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Building not found"));

        verifyUserHasAccessToBuilding(building, username);

        Zone zone = new Zone(zoneDTO.getName(), zoneDTO.getDescription(), building);
        zone.setTargetTemperature(zoneDTO.getTargetTemperature());
        zone = zoneRepository.save(zone);
        
        logger.info("Created new zone: {} in building: {}", zone.getName(), building.getId());
        return convertToDTO(zone);
    }

    // Get Zones by Building
    public List<ZoneDTO> getZonesByBuilding(Long buildingId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        verifyUserHasAccessToBuilding(building, username);

        return zoneRepository.findByBuildingId(buildingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get Zone
    public ZoneDTO getZone(Long buildingId, Long zoneId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        verifyZoneBelongsToBuilding(zone, buildingId);
        verifyUserHasAccessToBuilding(zone.getBuilding(), username);

        return convertToDTO(zone);
    }

    // Update target temperature for a zone
    public ZoneDTO updateTemperature(Long buildingId, Long zoneId, ZoneTemperatureUpdateDTO updateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        verifyZoneBelongsToBuilding(zone, buildingId);
        verifyUserHasAccessToBuilding(zone.getBuilding(), username);

        zone.setTargetTemperature(updateDTO.getTargetTemperature());

        zone = zoneRepository.save(zone);
        
        logger.info("Updated target temperature for zone: {} to: {}", zone.getId(), updateDTO.getTargetTemperature());
        return convertToDTO(zone);
    }

    // Update target temperature for all zones in a building
    public List<ZoneDTO> updateTargetTemperatureAll(Long buildingId, BigDecimal newTargetTemperature) {
        List<Zone> zones = zoneRepository.findByBuildingId(buildingId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (zones.isEmpty()) {
            throw new RuntimeException("No zones found for the specified building.");
        }

        for (Zone zone : zones) {
            verifyUserHasAccessToBuilding(zone.getBuilding(), username);

            zone.setTargetTemperature(newTargetTemperature);
            zoneRepository.save(zone);
        }       
        return zones.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Delete Zone
    public void deleteZone(Long buildingId, Long zoneId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        verifyZoneBelongsToBuilding(zone, buildingId);
        verifyUserHasAccessToBuilding(zone.getBuilding(), username);

        zoneRepository.delete(zone);
        logger.info("Deleted zone: {}", zone.getName());
    }

    // Convert Zone to ZoneDTO
    private ZoneDTO convertToDTO(Zone zone) {
        return new ZoneDTO(
            zone.getId(),
            zone.getName(),
            zone.getDescription(),
            zone.getBuilding().getId(),
            zone.getTargetTemperature(),
            zone.getCurrentTemperature()
        );
    }

    // Verify zone belongs to building
    private void verifyZoneBelongsToBuilding(Zone zone, Long buildingId) {
        if (!zone.getBuilding().getId().equals(buildingId)) {
            throw new RuntimeException("Zone not found in this building");
        }
    }

    // Verify user has access to building
    private void verifyUserHasAccessToBuilding(Building building, String username) {
        if (!building.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this zones for this building");
        }
    }
} 