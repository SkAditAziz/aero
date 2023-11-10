package dev.example.aero.controller;

import dev.example.aero.model.Flight;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
