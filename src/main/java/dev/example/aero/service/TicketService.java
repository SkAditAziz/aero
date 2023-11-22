package dev.example.aero.service;

import dev.example.aero.model.*;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import dev.example.aero.util.TicketPDFGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private PassengerService passengerService;

    @Transactional
    public void issueTicket(Map<String,Object> req) {
        // TODO use jwt authToken to retrieve passenger
        Passenger p = passengerService.getPassengerById(((Integer) req.get("userId")).longValue());
        FlightSchedule fs = flightScheduleRepository.findById(((Integer) req.get("scheduleId")).longValue()).orElse(null);
        int totalSeats = (int) req.get("noPassengers");

        Flight f = flightRepository.findById(fs.getFlightID()).orElse(null);
        SeatClassType sct = fs.getSeatClassType();
        double totalFare = fs.getTotalFare(totalSeats);
        TicketStatus ts = TicketStatus.UPCOMING;
        Ticket ticket = new Ticket("",f,p,fs,sct,totalSeats,totalFare,ts);

        //TODO check if the passenger has already bought 4 tickets on the same flight on the same schedule
        try {
            ticketRepository.save(ticket);
            updateSeatAllocation(ticket);
            TicketPDFGenerator ticketPDFGenerator = new TicketPDFGenerator(ticket);
            ticketPDFGenerator.generatePDF();
        } catch (Exception e) {
            System.out.println("Exception in generating pdf..................");
            e.printStackTrace();
        }
    }

    private void updateSeatAllocation(Ticket ticket) {
        FlightSchedule desiredSchedule = ticket.getFlightSchedule();
        desiredSchedule.setAvailableSeats(desiredSchedule.getAvailableSeats() - ticket.getTotalSeats());
        flightScheduleRepository.save(desiredSchedule);
    }
}
