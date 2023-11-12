package dev.example.aero.controller;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class FlightScheduleRESTController {

    @Autowired
    private FlightScheduleService flightScheduleService;

    @GetMapping
    public ResponseEntity<List<FlightDetailsResponseDTO>> getFlightDetailsOnDate(
            @RequestParam String from, @RequestParam String to, @RequestParam String date,
            @RequestParam String classType, @RequestParam int noPassengers
    ){
        return flightScheduleService.getFlightDetailsOnDate(from,to,date,classType,noPassengers);
    }

    @PostMapping
    public ResponseEntity<String> addFlightSchedule(@RequestBody FlightSchedule req){
        LocalDate flightDate = req.getFlightDate();
        List<String> flightIDs = req.getFlightIds();
        flightScheduleService.addOrUpdateFlightSchedule(flightDate,flightIDs);
        return ResponseEntity.ok("Schedule Updated Successfully!");
    }
}
