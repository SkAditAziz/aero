package dev.example.aero.service;

import dev.example.aero.repository.AirportRepository;
import dev.example.aero.model.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {
    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport findById(String code) {
        return airportRepository.findById(code).orElse(null);
    }

    public void deleteById(String code) {
        airportRepository.deleteById(code);
    }
}