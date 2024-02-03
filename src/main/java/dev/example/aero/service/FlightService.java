package dev.example.aero.service;

import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import dev.example.aero.repository.AirportRepository;
import dev.example.aero.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
    }

    public List<Flight> findFlightsToFrom(String to, String from) {
        Airport fromAirport = airportRepository.findById(from).orElse(null);
        Airport toAirport = airportRepository.findById(to).orElse(null);
        return flightRepository.findByFromAirportAndToAirport(fromAirport, toAirport);
    }
}
