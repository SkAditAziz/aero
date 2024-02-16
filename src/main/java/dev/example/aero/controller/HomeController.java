package dev.example.aero.controller;

import dev.example.aero.dto.AddFlightReqDTO;
import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.enumeration.SeatClassType;
import dev.example.aero.repository.AirportRepository;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.service.FlightScheduleService;
import dev.example.aero.service.TicketService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private final AirportRepository airportRepository;
    private final FlightScheduleService flightScheduleService;
    private final TicketService ticketService;
    private final FlightRepository flightRepository;
    private final String whatsappNumber;
    private final String whatsappAPI;

    public HomeController(AirportRepository airportRepository, FlightScheduleService flightScheduleService, TicketService ticketService, FlightRepository flightRepository, @Value("${whatsapp.number}") String whatsappNumber, @Value(("${whatsapp.api}")) String whatsappAPI) {
        this.airportRepository = airportRepository;
        this.flightScheduleService = flightScheduleService;
        this.ticketService = ticketService;
        this.flightRepository = flightRepository;
        this.whatsappNumber = whatsappNumber;
        this.whatsappAPI = whatsappAPI;
    }

    @GetMapping("/")
    public String showIndexPage(@NotNull Model model) {
        model.addAttribute("airports", airportRepository.findAll());
        model.addAttribute("seatClasses", SeatClassType.getAllSeatClassType());
        model.addAttribute("highestPassenger", TicketService.HIGHEST_PERMISSIBLE_SEATS);
        model.addAttribute("flightSearchReqDTO", new FlightSearchReqDTO(LocalDate.now()));
        model.addAttribute("whatsappURL", whatsappAPI + whatsappNumber);
        return "index";
    }

    @PostMapping("/search")
    public String searchFlight(FlightSearchReqDTO flightSearchReqDTO, Model model, BindingResult bindingResult) {
        //if (bindingResult.hasErrors()) {
        //    return "redirect:/index";
        //}
        List<FlightDetailsResponseDTO> flightSchedules = flightScheduleService.getFlightDetailsOnDate(flightSearchReqDTO);
        // issues with multiple tab!
        // a user select x numbers of seat in one tab, go to another tab and select y numbers of seat
        // then comes back to the first tab and confirm the ticket
        // he will buy y tickets (NOT x) through this approach!
        // TODO fix this issue
        ticketService.setSelectedSeats(flightSearchReqDTO.getNoPassengers());

        if(flightSchedules.isEmpty()) {
            return "no_flights";
        }
        model.addAttribute("flightSchedules", flightSchedules);
        return "flight_schedule";
    }

    @GetMapping("/admin")
    public String showAdmin(@NotNull Model model) {
        model.addAttribute("flightIDs", flightRepository.findAllIds());
        model.addAttribute("addFlightReqDTO", new AddFlightReqDTO());
        model.addAttribute("flightIDsToCancel", new ArrayList<String>());
        return "admin";
    }
}
