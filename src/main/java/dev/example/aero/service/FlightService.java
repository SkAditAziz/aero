package dev.example.aero.service;

import dev.example.aero.model.Flight;
import dev.example.aero.repository.AirportRepository;
import dev.example.aero.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirportRepository airportRepository;

    public List<Flight> findFlightsToFrom(String to, String from) {
        return flightRepository.findByFromAirportAndToAirport(airportRepository.findById(from).orElse(null), airportRepository.findById(to).orElse(null));
    }
}
