package dev.example.aero.restcontroller;


import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.FlightScheduleRequest;
import dev.example.aero.model.Passenger;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.security.service.JwtService;
import dev.example.aero.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket")
public class TicketRESTController {
    private final TicketService ticketService;
    private final JwtService jwtService;
    private final PassengerRepository passengerRepository;
    private final FlightScheduleRepository flightScheduleRepository;

    public TicketRESTController(TicketService ticketService, JwtService jwtService, PassengerRepository passengerRepository, FlightScheduleRepository flightScheduleRepository) {
        this.ticketService = ticketService;
        this.jwtService = jwtService;
        this.passengerRepository = passengerRepository;
        this.flightScheduleRepository = flightScheduleRepository;
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<byte[]> confirmTicket (HttpServletRequest request, @RequestBody Map<String, Object> req) {
        Passenger p = jwtService.getPassengerFromRequest(request);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            long scheduleId = ((Integer) req.get("scheduleId")).longValue();
            FlightSchedule flightSchedule = flightScheduleRepository.findById(scheduleId).orElse(null);
            int totalSeats = (int) req.get("noPassengers");
            byte[] pdfTicket = ticketService.issueTicket(new FlightScheduleRequest(flightSchedule, totalSeats, p));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ticket.pdf");
            return new ResponseEntity<>(pdfTicket, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to issue ticket");
        }
    }

    @GetMapping("/{passengerid}")
    public List<TicketDetailsResponseDTO> getTickets(@PathVariable("passengerid") long passengerId) {
        return ticketService.getTicketsByPassenger(passengerRepository.findById(passengerId).orElse(null));
    }
}
