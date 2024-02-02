package dev.example.aero.controller;

import dev.example.aero.model.Passenger;
import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.TicketService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ConfirmFlightController {
    private final TicketService ticketService;

    public ConfirmFlightController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/confirmFlight")
    public String confirmFlight(@RequestParam(name = "scheduleId") Long scheduleId, Model model) {
        Passenger currentPassenger = UserProvider.getCurrentPassenger();
        try {
            ticketService.issueTicket(scheduleId, ticketService.getSelectedSeats(), currentPassenger);
            return "confirm_flight";
        } catch (ResponseStatusException e) {
            model.addAttribute("errMsg", e.getReason());
            return "ticket_confirmation_fail";
        }
    }
}
