package dev.example.aero.service;

import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.SeatInfo;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final Session session;

    public TicketService(EntityManager entityManager) {
        if (entityManager == null || (this.session = entityManager.unwrap(Session.class)) == null)
            throw new NullPointerException();
    }

    public void issueTicket(Ticket t) {
        //TODO check if the passenger has already bought 4 tickets on the same flight on the same schedule
        ticketRepository.save(t);
        updateSeatAllocation(t);
    }

    @Transactional
    private void updateSeatAllocation(Ticket ticket) {
        FlightSchedule desiredSchedule = ticket.getFlightSchedule();

        Flight desiredFlight = desiredSchedule.getFlights().stream()
                .filter(f -> f.getId().equals(ticket.getFlight().getId()))
                .findFirst()
                .orElse(null);

        if (desiredFlight != null) {
            session.detach(desiredFlight);
        }

        assert desiredFlight != null;
        SeatInfo seatInfo = desiredFlight.getSeatInfoList().stream()
                .filter(s -> s.getSeatClassType() == ticket.getSeatClassType())
                .findFirst()
                .orElse(null);

        assert seatInfo != null;
        seatInfo.setAvailableSeats(seatInfo.getAvailableSeats() - ticket.getTotalSeats());
        flightScheduleRepository.save(desiredSchedule);
    }
}
