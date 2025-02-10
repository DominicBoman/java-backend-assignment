package com.building.temperaturecontrol.repository;

import com.building.temperaturecontrol.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByOwnerId(Long ownerId);
} 