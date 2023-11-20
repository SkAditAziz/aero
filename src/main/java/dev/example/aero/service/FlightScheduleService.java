package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;

    public void addOrUpdateFlightSchedule(Map<String,Object> req) throws InvalidDataAccessApiUsageException {
        LocalDate flightDate = LocalDate.parse((String) req.get("flightDate"));
        List<String> flightIDs = (List<String>) req.get("flightIds");

        if (flightIDs == null || flightIDs.isEmpty() || flightIDs.stream().anyMatch(String::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Flight Inserted");

        FlightSchedule existedSchedule = flightScheduleRepository.findByFlightDate(flightDate);
        List<Flight> flightList = flightIDs.stream()
                .map(id -> flightRepository.findById(id).orElse(null))
                .collect(Collectors.toList());

        // Deep copied flight list so the seatInfo of FlightSchedule->Flight->SeatInfo is some other to the actual flight
        //TODO Still not working! why?
        Set<Flight> copiedFlightSet = flightList.stream()
                .map(Flight::deepCopy)
                .collect(Collectors.toSet());

        if (existedSchedule == null) {
            existedSchedule = new FlightSchedule(flightDate, copiedFlightSet);
        } else {
            existedSchedule.getFlights().addAll(copiedFlightSet);
        }
        flightScheduleRepository.save(existedSchedule);
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(String from, String to, String date, String classType, int noPassengers) {
        FlightSchedule desiredSchedule = flightScheduleRepository.findByFlightDate(LocalDate.parse(date));
        if (desiredSchedule == null) {
            return Collections.emptyList();
        }

        List<Flight> desiredFlightsOnTheDay = desiredSchedule.getFlights().stream()
                .filter(f -> f.getFromAirport().getCode().equals(from) && f.getToAirport().getCode().equals(to))
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .toList();

        return desiredFlightsOnTheDay.isEmpty()
                ? Collections.emptyList()
                : desiredFlightsOnTheDay.stream()
                    .map(new FlightDetailsResponseDTOMapper(classType, noPassengers))
                    .toList();
    }

    public FlightSchedule getScheduleByDate(LocalDate date) {
        return flightScheduleRepository.findByFlightDate(date);
    }
}
