package com.building.temperaturecontrol.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.building.temperaturecontrol.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}