package dev.example.aero.controller;


import dev.example.aero.dto.TicketDetailsResponseDTO;
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
        int passengerId = jwtService.getUserIdFromRequest(request);
        if (passengerId == 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            byte[] pdfTicket = ticketService.issueTicket(req, passengerId);
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
