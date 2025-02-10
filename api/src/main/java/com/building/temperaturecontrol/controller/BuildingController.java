package com.building.temperaturecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import com.building.temperaturecontrol.dto.BuildingDTO;
import com.building.temperaturecontrol.service.BuildingService;

@RestController
@RequestMapping("/api/v1/buildings")
public class BuildingController {
    private static final Logger logger = LoggerFactory.getLogger(BuildingController.class);
    private final String apiContentType = "application/vnd.temperaturecontrol.v1+json";
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping(produces = apiContentType)
    public ResponseEntity<BuildingDTO> createBuilding(@RequestBody @Valid BuildingDTO buildingDTO) {
        logger.info("Received request to create building: {}", buildingDTO.getName());
        BuildingDTO created = buildingService.createBuilding(buildingDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping(produces = apiContentType)
    public ResponseEntity<List<BuildingDTO>> getMyBuildings() {
        logger.info("Fetching buildings for current user");
        List<BuildingDTO> buildings = buildingService.getCurrentUserBuildings();
        return ResponseEntity.ok(buildings);
    }

    @GetMapping(value = "/{buildingId}", produces = apiContentType)
    public ResponseEntity<BuildingDTO> getBuilding(@PathVariable Long buildingId) {
        logger.info("Fetching building: {}", buildingId);
        BuildingDTO building = buildingService.getBuilding(buildingId);
        return ResponseEntity.ok(building);
    }

    @DeleteMapping(value = "/{buildingId}", produces = apiContentType)
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long buildingId) {
        logger.info("Received request to delete building: {}", buildingId);
        buildingService.deleteBuilding(buildingId);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
} 