package com.building.temperaturecontrol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import com.building.temperaturecontrol.dto.ZoneDTO;
import com.building.temperaturecontrol.dto.ZoneTemperatureUpdateDTO;
import com.building.temperaturecontrol.service.ZoneService;


@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/zones")
public class ZoneController {
    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);
    private final String apiContentType = "application/vnd.temperaturecontrol.v1+json";
    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping(produces = apiContentType)
    public ResponseEntity<ZoneDTO> createZone(
            @PathVariable Long buildingId,
            @RequestBody @Valid ZoneDTO zoneDTO) {
        logger.info("Received request to create zone in building: {}", buildingId);
        zoneDTO.setBuildingId(buildingId);
        ZoneDTO created = zoneService.createZone(zoneDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping(produces = apiContentType)
    public ResponseEntity<List<ZoneDTO>> getZones(@PathVariable Long buildingId) {
        logger.info("Fetching zones for building: {}", buildingId);
        List<ZoneDTO> zones = zoneService.getZonesByBuilding(buildingId);
        return ResponseEntity.ok(zones);
    }

    @PutMapping(value = "/target-temp", produces = apiContentType)
    public ResponseEntity<List<ZoneDTO>> updateTargetTemperatureAll(
            @PathVariable Long buildingId,
            @RequestBody @Valid ZoneTemperatureUpdateDTO updateDTO) {
        logger.info("Updating target temperature for all zones in building: {}", buildingId);
        List<ZoneDTO> updatedZones = zoneService.updateTargetTemperatureAll(buildingId, updateDTO.getTargetTemperature());
        return ResponseEntity.ok(updatedZones);
    }

    @PatchMapping(value = "/{zoneId}/target-temp", produces = apiContentType)
    public ResponseEntity<ZoneDTO> updateTargetTemperature(
            @PathVariable Long buildingId,
            @PathVariable Long zoneId,
            @RequestBody @Valid ZoneTemperatureUpdateDTO updateDTO) {
        logger.info("Updating target temperature for zone: {} to: {}", zoneId, updateDTO.getTargetTemperature());
        ZoneDTO updated = zoneService.updateTemperature(buildingId, zoneId, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping(value = "/{zoneId}", produces = apiContentType)
    public ResponseEntity<ZoneDTO> getZone(
            @PathVariable Long buildingId,
            @PathVariable Long zoneId) {
        logger.info("Fetching zone: {} in building: {}", zoneId, buildingId);
        ZoneDTO zone = zoneService.getZone(buildingId, zoneId);
        return ResponseEntity.ok(zone);
    }

    @DeleteMapping(value = "/{zoneId}", produces = apiContentType)
    public ResponseEntity<Void> deleteZone(
            @PathVariable Long buildingId,
            @PathVariable Long zoneId) {
        logger.info("Received request to delete zone {} in building {}", zoneId, buildingId);
        zoneService.deleteZone(buildingId, zoneId);
        return ResponseEntity.noContent().build();
    }
}