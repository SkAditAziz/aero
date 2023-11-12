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

    public List<Flight> findFlightsToFrom(String to, String from) {
        return flightDAO.findByFromAirportAndToAirport(airportDao.findByID(from), airportDao.findByID(to));
    }
}
