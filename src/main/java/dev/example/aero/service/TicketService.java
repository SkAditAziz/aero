package dev.example.aero.service;

import com.itextpdf.text.DocumentException;
import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.dto.mapper.TicketDetailsResponseDTOMapper;
import dev.example.aero.model.*;
import dev.example.aero.enumeration.SeatClassType;
import dev.example.aero.enumeration.TicketStatus;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.repository.TicketRepository;
import dev.example.aero.util.TicketPDFGenerator;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@Getter
@Setter
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final JmsTemplate jmsTemplate;
    public static final int HIGHEST_PERMISSIBLE_SEATS = 4;
    private int selectedSeats = 0;

    public TicketService(TicketRepository ticketRepository, FlightScheduleRepository flightScheduleRepository, FlightRepository flightRepository, PassengerRepository passengerRepository, JmsTemplate jmsTemplate) {
        this.ticketRepository = ticketRepository;
        this.flightScheduleRepository = flightScheduleRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    public byte[] issueTicket(long scheduleId, int totalSeats, Passenger passenger) {
        FlightSchedule flightSchedule = flightScheduleRepository.findById(scheduleId).orElse(null);

        checkPassengersAbilityToBuyTicket(flightSchedule, totalSeats, passenger);

        Ticket ticket = initializeTicket(flightSchedule, totalSeats, passenger);

        byte[] pdfTicket = new byte[0];
        try {
            saveTicketAndSendConfirmationMail(ticket, pdfTicket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pdfTicket;
    }

    private void saveTicketAndSendConfirmationMail(Ticket ticket, byte[] pdfTicket) throws DocumentException, URISyntaxException, IOException {
        ticketRepository.save(ticket);
        updateSeatAllocation(ticket);
        TicketPDFGenerator ticketPDFGenerator = new TicketPDFGenerator(ticket);
        pdfTicket = ticketPDFGenerator.generatePDF();
        jmsTemplate.convertAndSend("messagequeue.q", new TicketWrapper(ticket, pdfTicket));
    }

    private void checkPassengersAbilityToBuyTicket(FlightSchedule flightSchedule,int totalSeats, Passenger passenger) {
        int alreadyBoughtSeats = ticketRepository.alreadyBoughtSeats(flightSchedule, passenger);
        if ((totalSeats + alreadyBoughtSeats) > HIGHEST_PERMISSIBLE_SEATS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A passenger cannot bought more than " + HIGHEST_PERMISSIBLE_SEATS + " tickets on a flight!");
        }
    }

    private Ticket initializeTicket(FlightSchedule flightSchedule, int totalSeats, Passenger passenger) {
        Flight f = flightRepository.findById(flightSchedule.getFlightID()).orElse(null);
        SeatClassType seatClassType = flightSchedule.getSeatClassType();
        double totalFare = flightSchedule.getTotalFare(totalSeats);
        TicketStatus ticketStatus = TicketStatus.UPCOMING;
        return new Ticket(f,passenger,flightSchedule,seatClassType,totalSeats,totalFare,ticketStatus);
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

    public List<TicketDetailsResponseDTO> getTicketsByPassenger(Passenger passenger) {
        List<Ticket> tickets = ticketRepository.findTicketsByPassenger(passenger);
        return tickets.stream()
                .map(new TicketDetailsResponseDTOMapper())
                .collect(Collectors.toList());
    }
}
