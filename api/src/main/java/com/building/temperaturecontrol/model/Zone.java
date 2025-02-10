package com.building.temperaturecontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "zone")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "current_temp", precision = 5, scale = 2)
    private BigDecimal currentTemperature = new BigDecimal("20.00");  // Default to 20°C

    @Column(name = "target_temp", precision = 5, scale = 2)
    private BigDecimal targetTemperature;

    // Constructors
    public Zone() {}

    public Zone(String name, String description, Building building) {
        this(null, name, description, building);
    }

    public Zone(Long id, String name, String description, Building building) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.building = building;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public BigDecimal getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(BigDecimal currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public BigDecimal getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(BigDecimal targetTemperature) {
        this.targetTemperature = targetTemperature;
        // Set currentTemperature as no real data available
        this.currentTemperature = targetTemperature;
    }
} 