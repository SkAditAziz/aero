package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;

    public void addOrUpdateFlightSchedule(LocalDate flightDate, List<String> flightIDs) throws InvalidDataAccessApiUsageException {
        FlightSchedule existedSchedule = flightScheduleRepository.findByFlightDate(flightDate);
        List<Flight> flightList = flightIDs.stream()
                .map(id -> flightRepository.findById(id).orElse(null))
                .collect(Collectors.toList());
        // Deep copied flight list so the seatInfo of FlightSchedule->Flight->SeatInfo is some other to the actual flight
        Set<Flight> copiedFlightSet = flightList.stream()
                .map(Flight::deepCopy)
                .collect(Collectors.toSet());
        //TODO Still not working! why?
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
                .collect(Collectors.toList());

        if (desiredFlightsOnTheDay.isEmpty()) {
            return Collections.emptyList();
        }
        desiredFlightsOnTheDay.sort(Comparator.comparing(Flight::getDepartureTime));
        FlightDetailsResponseDTOMapper flightDetailsResponseDTOMapper = new FlightDetailsResponseDTOMapper(classType, noPassengers);
        return desiredFlightsOnTheDay.stream()
                .map(flightDetailsResponseDTOMapper)
                .toList();
    }

    public FlightSchedule getScheduleByDate(LocalDate date) {
        return flightScheduleRepository.findByFlightDate(date);
    }
}
