package dev.example.aero.controller;

import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.TicketService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ConfirmFlightController {
    private final PassengerRepository passengerRepository;
    private final TicketService ticketService;

    public ConfirmFlightController(PassengerRepository passengerRepository, TicketService ticketService) {
        this.passengerRepository = passengerRepository;
        this.ticketService = ticketService;
    }

    @PostMapping("/confirmFlight")
    public String confirmFlight(@RequestParam(name = "scheduleId") Long scheduleId, Model model) {
        Long currentPassengerId = null;
        String currentUsername = UserProvider.getCurrentUsername();
        if (currentUsername != null) {
            currentPassengerId = passengerRepository.findByUsername(currentUsername).getId();
        }
        try {
            ticketService.issueTicket(scheduleId, ticketService.getSelectedSeats(), currentPassengerId);
            return "confirm_flight";
        } catch (ResponseStatusException e) {
            model.addAttribute("errMsg", e.getReason());
            return "ticket_confirmation_fail";
        }
    }
}
