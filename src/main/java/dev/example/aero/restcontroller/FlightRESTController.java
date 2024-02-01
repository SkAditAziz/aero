package dev.example.aero.restcontroller;

import dev.example.aero.model.Flight;
import dev.example.aero.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightRESTController {
    private final FlightService flightService;

    @Autowired
    public FlightRESTController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public List<Flight> getFlight(@RequestParam String from, @RequestParam String to) {
        List<Flight> availableFlights = flightService.findFlightsToFrom(to,from);
        if (availableFlights.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No flight found on the route");
        return availableFlights;
    }
}
