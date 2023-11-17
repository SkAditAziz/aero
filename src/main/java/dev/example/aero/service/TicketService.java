package dev.example.aero.service;

import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.SeatInfo;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    public void issueTicket(Ticket t) {
        ticketRepository.save(t);
        updateSeatAllocation(t);
    }

    private void updateSeatAllocation(Ticket ticket) {
        FlightSchedule desiredSchedule = ticket.getFlightSchedule();

        Flight flight = ticket.getFlight();
        Flight desiredFlight = (Flight) desiredSchedule.getFlights().stream()
                .filter(f -> f.getId().equals(flight.getId()))
                .findFirst()
                .orElse(null);

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
