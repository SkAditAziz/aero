package dev.example.aero.restcontroller;

import dev.example.aero.model.Airport;
import dev.example.aero.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping({"/airports", "/airports/"})
public class AirportRESTController {
    @Autowired
    private AirportRepository airportRepository;

    @GetMapping
    public List<Airport> airportsList() {
        return airportRepository.findAll();
    }

    @GetMapping("/{code}")
    public Airport airportByCode(@PathVariable("code") String code) {
        Airport a = airportRepository.findById(code).orElse(null);
        if (a == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No airport found by with the code");
        return a;
    }

    @DeleteMapping("/{code}")
    public String deleteAirportByCode(@PathVariable("code") String code) {
        if (airportRepository.findById(code).isPresent()) {
            airportRepository.deleteById(code);
            return "The Airport is deleted!";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No airport found by with the code");
        }
    }
}
