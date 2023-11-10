package dev.example.aero.controller;

import dev.example.aero.model.Airport;
import dev.example.aero.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/airports", "/airports/"})
public class AirportRESTController {
    @Autowired
    private AirportService airportService;

    @GetMapping("")
    public ResponseEntity<List<Airport>> airportsList() {
        return airportService.getAllAirports();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Airport> airportByCode(@PathVariable("code") String code) {
        return airportService.findById(code);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity deleteAirportByCode(@PathVariable("code") String code){
        return airportService.deleteById(code);
    }
}
