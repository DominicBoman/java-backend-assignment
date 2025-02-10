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

import java.util.Arrays;
import java.util.List;

import com.building.temperaturecontrol.config.TestSecurityConfig;
import com.building.temperaturecontrol.dto.BuildingDTO;
import com.building.temperaturecontrol.service.BuildingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import com.building.temperaturecontrol.exception.ResourceNotFoundException;

@WebMvcTest(BuildingController.class)
@Import(TestSecurityConfig.class)
class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BuildingService buildingService;

    @Test
    @WithMockUser
    void createBuilding_WithValidData_ShouldReturnCreated() throws Exception {
        BuildingDTO buildingDTO = new BuildingDTO(
            null, 
            "Test Building",
            null,
            "Test City",
            "Test Street",
            "12345",
            null
        );

        when(buildingService.createBuilding(any(BuildingDTO.class)))
            .thenReturn(new BuildingDTO(1L, "Test Building", 1L, "Test City", "Test Street", "12345", null));

        mockMvc.perform(post("/api/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(buildingDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"));
    }

    @Test
    @WithMockUser
    void createBuilding_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        BuildingDTO buildingDTO = new BuildingDTO(
            null,
            "", // invalid name
            null,
            "", // invalid city
            "", // invalid street
            "", // invalid postal code
            null
        );

        mockMvc.perform(post("/api/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.temperaturecontrol.v1+json")
                .content(objectMapper.writeValueAsString(buildingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser
    void getBuilding_WithValidId_ShouldReturnBuilding() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = new BuildingDTO(
            buildingId,
            "Test Building",
            1L,
            "Test City",
            "Test Street",
            "12345",
            null
        );

        when(buildingService.getBuilding(buildingId)).thenReturn(buildingDTO);

        mockMvc.perform(get("/api/v1/buildings/{id}", buildingId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$.id").value(buildingId));
    }

    @Test
    @WithMockUser
    void getBuilding_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long buildingId = 999L;
        when(buildingService.getBuilding(buildingId))
            .thenThrow(new ResourceNotFoundException("Building not found"));

        mockMvc.perform(get("/api/v1/buildings/{id}", buildingId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Building not found"));
    }

    @Test
    @WithMockUser
    void getMyBuildings_ShouldReturnListOfBuildings() throws Exception {
        List<BuildingDTO> buildings = Arrays.asList(
            new BuildingDTO(1L, "Building 1", 1L, "City 1", "Street 1", "12345", null),
            new BuildingDTO(2L, "Building 2", 1L, "City 2", "Street 2", "67890", null)
        );

        when(buildingService.getCurrentUserBuildings()).thenReturn(buildings);

        mockMvc.perform(get("/api/v1/buildings")
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser
    void deleteBuilding_WithValidId_ShouldReturnNoContent() throws Exception {
        Long buildingId = 1L;

        mockMvc.perform(delete("/api/v1/buildings/{id}", buildingId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteBuilding_WithoutPermission_ShouldReturnForbidden() throws Exception {
        Long buildingId = 1L;
        
        doThrow(new AccessDeniedException("Access denied"))
            .when(buildingService).deleteBuilding(buildingId);

        mockMvc.perform(delete("/api/v1/buildings/{id}", buildingId)
                .accept("application/vnd.temperaturecontrol.v1+json"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }
} 