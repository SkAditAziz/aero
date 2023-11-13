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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightService flightService;

    public List<Flight> getFlightsOnDate(String from, String to, String date) {
        LocalDate desiredDate = LocalDate.parse(date);
        List<String> flightsCodeOnDate = flightScheduleRepository.findCodesByDate(desiredDate);

        String regex = String.format("%s-%s-\\d{3}", from, to);
        Pattern pattern = Pattern.compile(regex);
        List<String> desiredFLightCodes = flightsCodeOnDate.stream()
                .filter(pattern.asPredicate())
                .toList();

        List<Flight> flightsOnTheDay = new ArrayList<>();
        if(desiredFLightCodes.isEmpty())
            return Collections.emptyList();
        else {
            for(String flightID : desiredFLightCodes) {
                flightsOnTheDay.add(flightRepository.findById(flightID).orElse(null));
            }
        }
        flightsOnTheDay.sort(Comparator.comparing(Flight::getDepartureTime));
        return flightsOnTheDay;
    }

    public void addOrUpdateFlightSchedule(LocalDate flightDate, List<String> flightIDs) throws DataIntegrityViolationException {
        FlightSchedule existedSchedule = flightScheduleRepository.findByFlightDate(flightDate);
        if (existedSchedule == null) {
            existedSchedule = new FlightSchedule(flightDate, flightIDs);
        } else {
            existedSchedule.getFlightIds().addAll(flightIDs);
        }
        flightScheduleRepository.save(existedSchedule);
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(String from, String to, String date, String classType, int noPassengers) {
        List<Flight> flightsOnTheDate = getFlightsOnDate(from,to,date);
        if (flightsOnTheDate.isEmpty()) {
            return Collections.emptyList();
        }
        FlightDetailsResponseDTOMapper flightDetailsResponseDTOMapper = new FlightDetailsResponseDTOMapper(classType, noPassengers);
        return flightsOnTheDate.stream()
                .map(flightDetailsResponseDTOMapper)
                .toList();
    }
}
