package com.building.temperaturecontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class BuildingDTO {
    private Long id;
    
    @NotBlank(message = "Building name is required")
    @Size(min = 2, max = 100, message = "Building name must be between 2 and 100 characters")
    private String name;
    
    private Long ownerId;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(min = 2, max = 200, message = "Street address must be between 2 and 200 characters")
    private String street;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    private List<ZoneDTO> zones;

    // Constructors
    public BuildingDTO() {}

    public BuildingDTO(Long id, String name, Long ownerId, String city, String street, String postalCode, List<ZoneDTO> zones) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.zones = zones;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public List<ZoneDTO> getZones() { return zones; }
    public void setZones(List<ZoneDTO> zones) { this.zones = zones; }
} 