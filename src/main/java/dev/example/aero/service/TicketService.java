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

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Comparator;
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
    public byte[] issueTicket(FlightScheduleRequest flightScheduleRequest) {
        checkPassengersAbilityToBuyTicket(flightScheduleRequest);
        Ticket ticket = initializeTicket(flightScheduleRequest);
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

    private void checkPassengersAbilityToBuyTicket(FlightScheduleRequest flightScheduleRequest) {
        int alreadyBoughtSeats = ticketRepository.alreadyBoughtSeats(flightScheduleRequest.getFlightSchedule(), flightScheduleRequest.getPassenger());
        if ((flightScheduleRequest.getTotalSeats() + alreadyBoughtSeats) > HIGHEST_PERMISSIBLE_SEATS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A passenger cannot bought more than " + HIGHEST_PERMISSIBLE_SEATS + " tickets on a flight!");
        }
    }

    private Ticket initializeTicket(FlightScheduleRequest flightScheduleRequest) {
        FlightSchedule flightSchedule = flightScheduleRequest.getFlightSchedule();
        Flight flight = flightRepository.findById(flightSchedule.getFlightID()).orElse(null);
        SeatClassType seatClassType = flightSchedule.getSeatClassType();
        BigDecimal totalFare = flightSchedule.getTotalFare(flightScheduleRequest.getTotalSeats());
        TicketStatus ticketStatus = TicketStatus.UPCOMING;
        return new Ticket(flight, flightScheduleRequest.getPassenger(), flightSchedule,seatClassType, flightScheduleRequest.getTotalSeats(), totalFare, ticketStatus);
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
                .map(new TicketDetailsResponseDTOMapper()).sorted(Comparator.comparing(TicketDetailsResponseDTO::getDate).reversed())
                .collect(Collectors.toList());
    }
}
