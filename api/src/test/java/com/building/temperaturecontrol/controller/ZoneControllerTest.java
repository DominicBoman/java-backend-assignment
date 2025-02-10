package com.building.temperaturecontrol.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.building.temperaturecontrol.config.TestSecurityConfig;
import com.building.temperaturecontrol.dto.ZoneDTO;
import com.building.temperaturecontrol.dto.ZoneTemperatureUpdateDTO;
import com.building.temperaturecontrol.service.ZoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import com.building.temperaturecontrol.exception.ResourceNotFoundException;

@WebMvcTest(ZoneController.class)
@Import(TestSecurityConfig.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ZoneService zoneService;

    @Test
    @WithMockUser
    void createZone_WithValidData_ShouldReturnCreated() throws Exception {
        Long buildingId = 1L;
        ZoneDTO zoneDTO = new ZoneDTO(
            null,
            "Test Zone",
            "Test Description",
            buildingId,
            new BigDecimal("20.0"),
            new BigDecimal("22.0")
        );

        when(zoneService.createZone(any(ZoneDTO.class)))
            .thenReturn(new ZoneDTO(1L, "Test Zone", "Test Description", buildingId, 
                new BigDecimal("20.0"), new BigDecimal("22.0")));

        mockMvc.perform(post("/api/v1/buildings/{buildingId}/zones", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(zoneDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"));
    }

    @Test
    @WithMockUser
    void createZone_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Long buildingId = 1L;
        ZoneDTO zoneDTO = new ZoneDTO(
            null,
            "", // invalid name
            "Test Description",
            buildingId,
            new BigDecimal("-50.0"), // invalid temperature
            new BigDecimal("100.0") // invalid temperature
        );

        mockMvc.perform(post("/api/v1/buildings/{buildingId}/zones", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(zoneDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser
    void getZones_ShouldReturnListOfZones() throws Exception {
        Long buildingId = 1L;
        List<ZoneDTO> zones = Arrays.asList(
            new ZoneDTO(1L, "Zone 1", "Description 1", buildingId, new BigDecimal("20.0"), new BigDecimal("22.0")),
            new ZoneDTO(2L, "Zone 2", "Description 2", buildingId, new BigDecimal("21.0"), new BigDecimal("23.0"))
        );

        when(zoneService.getZonesByBuilding(buildingId)).thenReturn(zones);

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/zones", buildingId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser
    void getZone_WithValidId_ShouldReturnZone() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 1L;
        ZoneDTO zoneDTO = new ZoneDTO(
            zoneId,
            "Test Zone",
            "Test Description",
            buildingId,
            new BigDecimal("20.0"),
            new BigDecimal("22.0")
        );

        when(zoneService.getZone(buildingId, zoneId)).thenReturn(zoneDTO);

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/zones/{zoneId}", buildingId, zoneId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$.id").value(zoneId));
    }

    @Test
    @WithMockUser
    void getZone_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 999L;

        when(zoneService.getZone(buildingId, zoneId))
            .thenThrow(new ResourceNotFoundException("Zone not found"));

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/zones/{zoneId}", buildingId, zoneId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone not found"));
    }

    @Test
    @WithMockUser
    void updateTemperature_WithValidData_ShouldReturnOk() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 1L;
        ZoneTemperatureUpdateDTO updateDTO = new ZoneTemperatureUpdateDTO(new BigDecimal("23.5"));

        ZoneDTO updatedZone = new ZoneDTO(
            zoneId,
            "Test Zone",
            "Test Description",
            buildingId,
            new BigDecimal("23.5"),
            new BigDecimal("23.5")
        );

        when(zoneService.updateTemperature(any(Long.class), any(Long.class), any(ZoneTemperatureUpdateDTO.class)))
            .thenReturn(updatedZone);

        mockMvc.perform(patch("/api/v1/buildings/{buildingId}/zones/{zoneId}/target-temp", buildingId, zoneId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$.targetTemperature").value("23.5"));
    }

    @Test
    @WithMockUser
    void updateTemperature_WithInvalidAccess_ShouldReturnForbidden() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 1L;
        ZoneTemperatureUpdateDTO updateDTO = new ZoneTemperatureUpdateDTO(new BigDecimal("23.5"));

        when(zoneService.updateTemperature(any(Long.class), any(Long.class), any(ZoneTemperatureUpdateDTO.class)))
            .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(patch("/api/v1/buildings/{buildingId}/zones/{zoneId}/target-temp", buildingId, zoneId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @WithMockUser
    void deleteZone_WithValidId_ShouldReturnNoContent() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 1L;

        mockMvc.perform(delete("/api/v1/buildings/{buildingId}/zones/{zoneId}", buildingId, zoneId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteZone_WithoutPermission_ShouldReturnForbidden() throws Exception {
        Long buildingId = 1L;
        Long zoneId = 1L;

        doThrow(new AccessDeniedException("Access denied"))
            .when(zoneService).deleteZone(buildingId, zoneId);

        mockMvc.perform(delete("/api/v1/buildings/{buildingId}/zones/{zoneId}", buildingId, zoneId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @WithMockUser
    void updateTemperatureAll_WithValidData_ShouldReturnOk() throws Exception {
        Long buildingId = 1L;
        BigDecimal newTemp = new BigDecimal("23.5");
        ZoneTemperatureUpdateDTO updateDTO = new ZoneTemperatureUpdateDTO(newTemp);

        List<ZoneDTO> updatedZones = Arrays.asList(
            new ZoneDTO(1L, "Zone 1", "Description 1", buildingId, newTemp, newTemp),
            new ZoneDTO(2L, "Zone 2", "Description 2", buildingId, newTemp, newTemp)
        );

        when(zoneService.updateTargetTemperatureAll(any(Long.class), any(BigDecimal.class)))
            .thenReturn(updatedZones);

        mockMvc.perform(put("/api/v1/buildings/{buildingId}/zones/target-temp", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$[0].targetTemperature").value("23.5"))
                .andExpect(jsonPath("$[1].targetTemperature").value("23.5"));
    }

    @Test
    @WithMockUser
    void updateTemperatureAll_WithInvalidAccess_ShouldReturnForbidden() throws Exception {
        Long buildingId = 1L;
        ZoneTemperatureUpdateDTO updateDTO = new ZoneTemperatureUpdateDTO(new BigDecimal("23.5"));

        when(zoneService.updateTargetTemperatureAll(any(Long.class), any(BigDecimal.class)))
            .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(put("/api/v1/buildings/{buildingId}/zones/target-temp", buildingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }
} 