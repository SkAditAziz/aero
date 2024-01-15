package dev.example.aero.restcontroller;


import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.model.Passenger;
import dev.example.aero.security.service.JwtService;
import dev.example.aero.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private TicketService ticketService;
    @Autowired
    private JwtService jwtService;
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('ROLE_PASSENGER')")
    public ResponseEntity<byte[]> confirmTicket (HttpServletRequest request, @RequestBody Map<String, Object> req) {
        Passenger p = jwtService.getPassengerFromRequest(request);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            long scheduleId = ((Integer) req.get("scheduleId")).longValue();
            int totalSeats = (int) req.get("noPassengers");
            byte[] pdfTicket = ticketService.issueTicket(scheduleId, totalSeats, p.getId());
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
        return ticketService.getTicketsByPassengerId(passengerId);
    }
}
