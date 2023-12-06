package dev.example.aero.service;

import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.dto.mapper.TicketDetailsResponseDTOMapper;
import dev.example.aero.model.*;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import dev.example.aero.util.TicketPDFGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public byte[] issueTicket(Map<String,Object> req) {
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
        byte[] pdfTicket = new byte[0];
        try {
            ticketRepository.save(ticket);
            updateSeatAllocation(ticket);
            TicketPDFGenerator ticketPDFGenerator = new TicketPDFGenerator(ticket);
            pdfTicket = ticketPDFGenerator.generatePDF();
            jmsTemplate.convertAndSend("messagequeue.q", ticket);
        } catch (Exception e) {
            System.out.println("Exception in generating pdf..................");
            e.printStackTrace();
        }
        return pdfTicket;
    }

    private void updateSeatAllocation(Ticket ticket) {
        FlightSchedule desiredSchedule = ticket.getFlightSchedule();
        desiredSchedule.setAvailableSeats(desiredSchedule.getAvailableSeats() - ticket.getTotalSeats());
        flightScheduleRepository.save(desiredSchedule);
    }

    public List<TicketDetailsResponseDTO> getTicketsByPassengerId(long passengerId) {
        List<Ticket> tickets = ticketRepository.getTicketsByPassengerId(passengerId);
        return tickets.stream()
                .map(new TicketDetailsResponseDTOMapper())
                .collect(Collectors.toList());
    }

    public Map<String, String> getEmailDetails(Ticket ticket) {
        return Map.of(
                "to", ticket.getPassenger().getEmail(),
                "sub", "Your Aero Ticket",
                "body", "Dear " + ticket.getPassenger().getFirstName() + " " +
                        ticket.getPassenger().getLastName() + ",\n\n" +
                        "Your ticket has been confirmed.\n\n" +
                        "Wishing a great journey!\n" +
                        "-Team Aero"
        );
    }
}
