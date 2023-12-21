package dev.example.aero.viewcontroller;

import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.service.AirportService;
import dev.example.aero.service.FlightScheduleService;
import dev.example.aero.service.TicketService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private AirportService airportService;
    @Autowired
    private FlightScheduleService flightScheduleService;
    @GetMapping("/")
    public String showIndexPage(@NotNull Model model) {
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("seatClasses", SeatClassType.getAllSeatClassType());
        model.addAttribute("highestPassenger", TicketService.HIGHEST_PERMISSIBLE_SEATS);
        model.addAttribute("flightSearchReqDTO", new FlightSearchReqDTO());
        return "index";
    }

    @PostMapping("/search")
    public String searchFlight(@ModelAttribute("flightSearchReqDTO") @NotNull FlightSearchReqDTO flightSearchReqDTO, Model model) {
        return "flight_schedule";
    }
}
