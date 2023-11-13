package dev.example.aero.controller;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class FlightScheduleRESTController {

    @Autowired
    private FlightScheduleService flightScheduleService;

    @GetMapping
    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(
            @RequestParam String from, @RequestParam String to, @RequestParam String date,
            @RequestParam String classType, @RequestParam int noPassengers) {
        List<FlightDetailsResponseDTO> flightResponseDTOList = flightScheduleService.getFlightDetailsOnDate(from,to,date,classType,noPassengers);
        if (flightResponseDTOList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,"No flight found");
        return flightResponseDTOList;
    }

    @PostMapping
    public String addFlightSchedule(@RequestBody FlightSchedule req) {
        LocalDate flightDate = req.getFlightDate();
        List<String> flightIDs = req.getFlightIds();
        try {
            flightScheduleService.addOrUpdateFlightSchedule(flightDate,flightIDs);
            return "Schedule Updated Successfully!";
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Already have the Flight(s) On the day");
        }
    }
}
