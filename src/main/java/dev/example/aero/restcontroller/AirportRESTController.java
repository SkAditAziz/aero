package dev.example.aero.restcontroller;

import dev.example.aero.model.Airport;
import dev.example.aero.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping({"/airports", "/airports/"})
public class AirportRESTController {
    @Autowired
    private AirportService airportService;

    @GetMapping
    public List<Airport> airportsList() {
        return airportService.getAllAirports();
    }

    @GetMapping("/{code}")
    public Airport airportByCode(@PathVariable("code") String code) {
        Airport a = airportService.findById(code);
        if (a == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No airport found by with the code");
        return a;
    }

    @DeleteMapping("/{code}")
    public String deleteAirportByCode(@PathVariable("code") String code) {
        if (airportService.findById(code) != null) {
            airportService.deleteById(code);
            return "The Airport is deleted!";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No airport found by with the code");
        }
    }
}
