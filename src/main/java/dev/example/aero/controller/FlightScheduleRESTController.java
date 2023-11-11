package dev.example.aero.controller;

import dev.example.aero.model.Flight;
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
    public ResponseEntity<List<Flight>> getFlightsScheduleOnDate (@RequestParam String from, @RequestParam String to, @RequestParam String date){
        return flightScheduleService.getFlightsOnDate(from, to, date);
    }

    @PostMapping
    public ResponseEntity<String> addFlightSchedule(@RequestBody FlightSchedule req){
        LocalDate flightDate = req.getFlightDate();
        List<String> flightIDs = req.getFlightIds();
        flightScheduleService.addOrUpdateFlightSchedule(flightDate,flightIDs);
        return ResponseEntity.ok("Schedule Updated Successfully!");
    }
}
