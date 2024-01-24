package dev.example.aero.controller;

import dev.example.aero.dto.AddFlightReqDTO;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FlightScheduleService flightScheduleService;
    @PostMapping("/add_flight")
    public String addFlight(AddFlightReqDTO addFlightReqDTO, Model model) {
        try {
            flightScheduleService.addOrUpdateFlightSchedule(addFlightReqDTO.getJourneyDate(), addFlightReqDTO.getFlightIDs());
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errMsg", "Already have the Flight(s) On the day");
            return "flight_add_fail";
        }
        return "confirm_add_flight";
    }
}
