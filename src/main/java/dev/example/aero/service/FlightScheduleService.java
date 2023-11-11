package dev.example.aero.service;

import dev.example.aero.dao.FlightDAO;
import dev.example.aero.dao.FlightScheduleDAO;
import dev.example.aero.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleDAO flightScheduleDAO;
    @Autowired
    private FlightDAO flightDAO;
    @Autowired
    private FlightService flightService;


    public ResponseEntity<List<Flight>> getFlightsOnDate(String from, String to, String date) {
        LocalDate desiredDate = LocalDate.parse(date);
        List<String> flightsCodeOnDate = flightScheduleDAO.findCodesByDate(desiredDate);
        List<Flight> flightsOnTheDay = new ArrayList<>();
        if(flightsCodeOnDate.isEmpty())
            return ResponseEntity.noContent().build();
        else{
            for( String flightID : flightsCodeOnDate){
                String[] airportsCodes = flightID.split("-");
                if(from.equals(airportsCodes[0]) && to.equals(airportsCodes[1])) {
                    flightsOnTheDay.add(flightDAO.findById(flightID).orElse(null));
                }
            }
        }
        if(flightsOnTheDay.isEmpty())
            return ResponseEntity.noContent().build();
        flightsOnTheDay.sort(Comparator.comparing(Flight::getDepartureTime));
        return ResponseEntity.ok(flightsOnTheDay);
    }
}
