package dev.example.aero.repository;

import dev.example.aero.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport,String> {
}
