package com.building.temperaturecontrol.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.building.temperaturecontrol.validation.TemperatureConstraint;

public class ZoneTemperatureUpdateDTO {
    @NotNull(message = "targetTemperature is required")
    @TemperatureConstraint
    private BigDecimal targetTemperature;

    public ZoneTemperatureUpdateDTO() {}

    public ZoneTemperatureUpdateDTO(BigDecimal targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public BigDecimal getTargetTemperature() { return targetTemperature; }
    public void setTargetTemperature(BigDecimal targetTemperature) { this.targetTemperature = targetTemperature; }
} 