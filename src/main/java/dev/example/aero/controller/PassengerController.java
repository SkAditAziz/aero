package dev.example.aero.controller;

import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.model.Passenger;
import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.TicketService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/passenger")
public class PassengerController {
    private final TicketService ticketService;

    public PassengerController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/info")
    public String showPassengerInfo(@NotNull Model model) {
        Passenger currentPassenger = UserProvider.getCurrentPassenger();
        List<TicketDetailsResponseDTO> ticketsOfCurrentPassenger = ticketService.getTicketsByPassenger(currentPassenger);
        model.addAttribute("passengerName", currentPassenger.getFirstName() + " " + currentPassenger.getLastName());
        model.addAttribute("myTickets", ticketsOfCurrentPassenger);
        return "passenger_info";
    }

    @GetMapping("/showTicket/{ticketId}")
    public ResponseEntity<InputStreamResource> showTicket(@PathVariable(name = "ticketId") String ticketId, Model model) throws FileNotFoundException {
        try {
            FileInputStream fileInputStream = new FileInputStream(ticketService.getTicketPath(ticketId));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(new InputStreamResource(fileInputStream));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
