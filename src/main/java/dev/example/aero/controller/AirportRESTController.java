package dev.example.aero.controller;

import dev.example.aero.model.Airport;
import dev.example.aero.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AirportRESTController {
    @Autowired
    private AirportService airportService;

    @GetMapping("/airports")
    public ResponseEntity<List<Airport>> airportsList() {
        return airportService.getAllAirports();
    }

    @GetMapping("/airports/{code}")
    public ResponseEntity<Airport> airportByCode(@PathVariable("code") String code) {
        return airportService.findById(code);
    }
}
