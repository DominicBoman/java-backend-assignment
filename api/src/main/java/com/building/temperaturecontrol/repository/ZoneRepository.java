package com.building.temperaturecontrol.repository;

import com.building.temperaturecontrol.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByBuildingId(Long buildingId);
} 