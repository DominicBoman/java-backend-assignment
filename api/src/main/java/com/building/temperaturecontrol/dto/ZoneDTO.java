package com.building.temperaturecontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import com.building.temperaturecontrol.validation.TemperatureConstraint;

public class ZoneDTO {
    
    private Long id;

    @NotBlank(message = "Zone name is required")
    @Size(min = 2, max = 100, message = "Zone name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Long buildingId;

    @TemperatureConstraint
    private BigDecimal targetTemperature;

    @TemperatureConstraint
    private BigDecimal currentTemperature;

    // Constructors
    public ZoneDTO() {}

    public ZoneDTO(Long id, String name, String description, Long buildingId, BigDecimal targetTemperature, BigDecimal currentTemperature) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.buildingId = buildingId;
        this.targetTemperature = targetTemperature;
        this.currentTemperature = currentTemperature;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public BigDecimal getTargetTemperature() { return targetTemperature; }
    public void setTargetTemperature(BigDecimal targetTemperature) { this.targetTemperature = targetTemperature; }

    public BigDecimal getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(BigDecimal currentTemperature) { this.currentTemperature = currentTemperature; }
} 