package dev.example.aero.repository;

import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.Passenger;
import dev.example.aero.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findTicketsByPassenger(Passenger passenger);

    @Query(value = "SELECT t.* FROM ticket t " +
            "JOIN flight_schedule fs ON t.schedule_id = fs.schedule_id " +
            "JOIN flight f ON t.flight_id = f.flight_id " +
            "WHERE (t.status = 'UPCOMING') " +
            "AND (fs.flight_date < CONVERT_TZ(now(), 'UTC', f.arrival_time_zone))" , nativeQuery = true)
    List<Ticket> completedFlightTickets();

    @Query("SELECT COALESCE(SUM(t.totalSeats), 0) " +
            "FROM Ticket t " +
            "WHERE t.flightSchedule = :schedule " +
            "AND t.passenger = :passenger")
    int alreadyBoughtSeats(FlightSchedule schedule, Passenger passenger);

    List<Ticket> findByflightSchedule(FlightSchedule schedule);
}
