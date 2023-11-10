package dev.example.aero.dao;

import dev.example.aero.model.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightScheduleDAO extends JpaRepository<FlightSchedule,Long> {
}
