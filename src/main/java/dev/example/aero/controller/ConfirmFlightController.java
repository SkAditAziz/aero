package dev.example.aero.controller;

import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.FlightScheduleRequest;
import dev.example.aero.model.Passenger;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.TicketService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ConfirmFlightController {
    private final TicketService ticketService;
    private final FlightScheduleRepository flightScheduleRepository;

    public ConfirmFlightController(TicketService ticketService, FlightScheduleRepository flightScheduleRepository) {
        this.ticketService = ticketService;
        this.flightScheduleRepository = flightScheduleRepository;
    }

    @PostMapping("/confirmFlight")
    public String confirmFlight(long scheduleId, Model model) {
        FlightSchedule flightSchedule = flightScheduleRepository.findById(scheduleId).orElse(null);
        Passenger currentPassenger = UserProvider.getCurrentPassenger();
        try {
            /* TODO if I reload the confirm_flight page, this ticketService.getSelectedSeats() is becoming 0
                new Ticket is being created with empty seat!
            */
            ticketService.issueTicket(new FlightScheduleRequest(flightSchedule, ticketService.getSelectedSeats(), currentPassenger));
            return "confirm_flight";
        } catch (ResponseStatusException e) {
            model.addAttribute("errMsg", e.getReason());
            return "ticket_confirmation_fail";
        }
    }
}
