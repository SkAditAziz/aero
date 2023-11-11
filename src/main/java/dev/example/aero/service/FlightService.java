package dev.example.aero.service;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.dao.FlightDAO;
import dev.example.aero.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    @Autowired
    private FlightDAO flightDAO;
    @Autowired
    private AirportDao airportDao;

    public ResponseEntity<List<Flight>> findFlightsToFrom(String to, String from) {
        List<Flight> availableFlights =  flightDAO.findByFromAirportAndToAirport(airportDao.findByID(from), airportDao.findByID(to));
        if(availableFlights.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(availableFlights);
    }
}
