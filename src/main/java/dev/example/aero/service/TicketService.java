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
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@Getter
@Setter
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
    public static final int HIGHEST_PERMISSIBLE_SEATS = 4;
    private int selectedSeats = 0;


    @Transactional
    public byte[] issueTicket(long scheduleId, int totalSeats, Long passengerId) {
        Passenger p = passengerService.getPassengerById(passengerId);
        FlightSchedule fs = flightScheduleRepository.findById(scheduleId).orElse(null);

        int alreadyBoughtSeats = ticketRepository.alreadyBoughtSeats(fs.getId(), p.getId());
        if ((totalSeats + alreadyBoughtSeats) > HIGHEST_PERMISSIBLE_SEATS) {
            System.out.println("A passenger cannot bought more than " + HIGHEST_PERMISSIBLE_SEATS + " tickets on a flight!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A passenger cannot bought more than " + HIGHEST_PERMISSIBLE_SEATS + " tickets on a flight!");
        }

        Flight f = flightRepository.findById(fs.getFlightID()).orElse(null);
        SeatClassType sct = fs.getSeatClassType();
        double totalFare = fs.getTotalFare(totalSeats);
        TicketStatus ts = TicketStatus.UPCOMING;
        Ticket ticket = new Ticket("",f,p,fs,sct,totalSeats,totalFare,ts);

        byte[] pdfTicket = new byte[0];
        try {
            ticketRepository.save(ticket);
            updateSeatAllocation(ticket);
            TicketPDFGenerator ticketPDFGenerator = new TicketPDFGenerator(ticket);
            pdfTicket = ticketPDFGenerator.generatePDF();
            jmsTemplate.convertAndSend("messagequeue.q", new TicketWrapper(ticket, pdfTicket));
        } catch (Exception e) {
            System.out.println("Exception in generating pdf..................");
            e.printStackTrace();
        }
        return pdfTicket;
    }

    public String saveTicketPdf(Ticket ticket, byte[] pdfTicket) {
        try {
            String resourcesDirectory = getClass().getClassLoader().getResource("").getPath();
            String ticketsDirectoryPath = resourcesDirectory + "tickets";
            Files.createDirectories(Paths.get(ticketsDirectoryPath));
            String filePath = ticketsDirectoryPath + "/" + ticket.getId() + ".pdf";

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfTicket);
            }
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTicketPath(String ticketId) {
        String resourcesDirectory = getClass().getClassLoader().getResource("").getPath();
        String ticketsDirectoryPath = resourcesDirectory + "tickets";
        return ticketsDirectoryPath + "/" + ticketId + ".pdf";
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
}
