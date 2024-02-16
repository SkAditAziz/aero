package dev.example.aero.controller;

import dev.example.aero.dto.AddFlightReqDTO;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final FlightScheduleService flightScheduleService;

    public AdminController(FlightScheduleService flightScheduleService) {
        this.flightScheduleService = flightScheduleService;
    }

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

    @PostMapping("/upload")
    public String addFlightByUploadingFile(@RequestParam MultipartFile file, Model model) {
        try {
            flightScheduleService.addOrUpdateFlightScheduleWithFile(file);
        } catch (Exception e) {
            String errMsg;
            if (e instanceof IllegalArgumentException)
                errMsg = e.getMessage();
            else
                errMsg = "Can not upload file";
            model.addAttribute("errMsg", errMsg);
            return "flight_add_fail";
        }
        return "confirm_add_flight";
    }

    @PostMapping("/cancel_flight")
    public String cancelFlight(AddFlightReqDTO addFlightReqDTO, Model model) {
        try {
            flightScheduleService.cancelFlightsOnDate(addFlightReqDTO.getJourneyDate(), addFlightReqDTO.getFlightIDs());
        } catch (Exception e) {
            model.addAttribute("errMsg", "No flight Selected!");
            return "flight_add_fail";
        }
        return "confirm_add_flight";
    }
}
