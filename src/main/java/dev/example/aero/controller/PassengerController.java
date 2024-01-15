package dev.example.aero.controller;

import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.model.Passenger;
import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.TicketService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/passenger")
public class PassengerController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/info")
    public String showPassengerInfo(@NotNull Model model) {
        Passenger currentPassenger = UserProvider.getCurrentPassenger();
        List<TicketDetailsResponseDTO> ticketsOfCurrentPassenger = ticketService.getTicketsByPassengerId(currentPassenger.getId());
        model.addAttribute("mytickets", ticketsOfCurrentPassenger);
        return "passenger_info";
    }
}
