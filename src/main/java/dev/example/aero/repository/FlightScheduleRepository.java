package dev.example.aero.repository;

import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule,Long> {
    FlightSchedule findByFlightDate(LocalDate flightDate);
    @Query(value = "SELECT fs.* FROM flight_schedule fs " +
            "JOIN flight f ON fs.flight_id = f.flight_id " +
            "WHERE fs.flight_date = :flightDate " +
            "AND f.from_airport_code = :fromAirportCode " +
            "AND f.to_airport_code = :toAirportCode " +
            "AND fs.seat_class_type = :classType", nativeQuery = true)
    List<FlightSchedule> findAllByFlightDateAndFromAndToAndClass(LocalDate flightDate, String fromAirportCode, String toAirportCode, String classType);
}
