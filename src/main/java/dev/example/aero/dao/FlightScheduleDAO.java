package dev.example.aero.dao;

import dev.example.aero.model.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FlightScheduleDAO extends JpaRepository<FlightSchedule,Long> {
    @Query("SELECT fs.flightIds FROM FlightSchedule fs WHERE fs.flightDate = :desiredDate")
    List<String> findCodesByDate(LocalDate desiredDate);

    FlightSchedule findByFlightDate(LocalDate flightDate);
}
