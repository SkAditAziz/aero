package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightService flightService;

    public void addOrUpdateFlightSchedule(LocalDate flightDate, List<String> flightIDs) throws DataIntegrityViolationException {
        FlightSchedule existedSchedule = flightScheduleRepository.findByFlightDate(flightDate);
        List<Flight> flightList = flightIDs.stream().
                map(id -> flightRepository.findById(id).orElse(null))
                .toList();

        if (existedSchedule == null) {
            existedSchedule = new FlightSchedule(flightDate, flightList);
        } else {
            existedSchedule.getFlights().addAll(flightList);
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
                .toList();

        if (desiredFlightsOnTheDay.isEmpty()) {
            return Collections.emptyList();
        }
        FlightDetailsResponseDTOMapper flightDetailsResponseDTOMapper = new FlightDetailsResponseDTOMapper(classType, noPassengers);
        return desiredFlightsOnTheDay.stream()
                .map(flightDetailsResponseDTOMapper)
                .toList();
    }

    public FlightSchedule getScheduleByDate(LocalDate date) {
        return flightScheduleRepository.findByFlightDate(date);
    }
}
