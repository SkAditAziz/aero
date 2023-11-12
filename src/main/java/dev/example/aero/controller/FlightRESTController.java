package dev.example.aero.controller;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.dao.FlightDAO;
import dev.example.aero.model.Flight;
import dev.example.aero.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightRESTController {

    @Autowired
    private FlightService flightService;
    @GetMapping
    public ResponseEntity<List<Flight>> getFlight(@RequestParam String from, @RequestParam String to){
        List<Flight> availableFlights = flightService.findFlightsToFrom(to,from);
        if(availableFlights.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(availableFlights);
    }
}
