package dev.example.aero.controller;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.service.AirportService;
import dev.example.aero.service.FlightScheduleService;
import dev.example.aero.service.TicketService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private AirportService airportService;
    @Autowired
    private FlightScheduleService flightScheduleService;
    @Autowired
    private TicketService ticketService;
    @Value("${whatsapp.number}")
    private String whatsappNumber;

    @Value(("${whatsapp.api}"))
    private String whatsappAPI;

    @GetMapping("/")
    public String showIndexPage(@NotNull Model model) {
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("seatClasses", SeatClassType.getAllSeatClassType());
        model.addAttribute("highestPassenger", TicketService.HIGHEST_PERMISSIBLE_SEATS);
        model.addAttribute("flightSearchReqDTO", new FlightSearchReqDTO(LocalDate.now()));
        model.addAttribute("whatsappURL", whatsappAPI + whatsappNumber);
        return "index";
    }

    @PostMapping("/search")
    public String searchFlight(@ModelAttribute("flightSearchReqDTO") FlightSearchReqDTO flightSearchReqDTO, Model model, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return "redirect:/index";
//        }
        List<FlightDetailsResponseDTO> flightSchedules = flightScheduleService.getFlightDetailsOnDate(flightSearchReqDTO);
        ticketService.setSelectedSeats(flightSearchReqDTO.getNoPassengers());
        if(flightSchedules.isEmpty()) {
            return "no_flights";
        }
        model.addAttribute("flightSchedules", flightSchedules);
        return "flight_schedule";
    }

    @GetMapping("/admin")
    public String showAdmin(Model model) {
        return "admin";
    }
}
