package com.building.temperaturecontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "building")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String street;

    @NotBlank
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @OneToMany(mappedBy = "building")
    private List<Zone> zones;

    // Constructors
    public Building() {}

    public Building(String name, String city, String street, String postalCode, User owner) {
        this(null, name, city, street, postalCode, owner);
    }

    public Building(Long id, String name, String city, String street, String postalCode, User owner) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.owner = owner;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
} 