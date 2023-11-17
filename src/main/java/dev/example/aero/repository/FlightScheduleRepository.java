package dev.example.aero.repository;

import dev.example.aero.model.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule,Long> {
    FlightSchedule findByFlightDate(LocalDate flightDate);
}
