package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Autowired
    private FlightService flightService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PassengerRepository passengerRepository;

    public void addOrUpdateFlightSchedule(Map<String,Object> req) {
        LocalDate flightDate = LocalDate.parse((String) req.get("flightDate"));
        List<String> flightIDs = (List<String>) req.get("flightIds");

        if (flightIDs == null || flightIDs.isEmpty() || flightIDs.stream().anyMatch(String::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Flight Inserted");

        for (String flightId : flightIDs) {
            Flight flight = flightRepository.findById(flightId).orElse(null);

            if (flight != null) {
                List<FlightSchedule> flightSchedules = flight.getSeatInfoList().stream()
                        .map(seatInfo -> new FlightSchedule(flightDate, flightId, seatInfo.getSeatClassType(), seatInfo.getAvailableSeats(), seatInfo.getFare()))
                        .collect(Collectors.toList());
                flightScheduleRepository.saveAll(flightSchedules);
            }
        }
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(String from, String to, String date, String classType, int noPassengers) {
        List<FlightDetailsResponseDTO> result = flightScheduleRepository.findAllByFlightDateAndFromAndToAndClass(
                        LocalDate.parse(date), from, to, classType).stream()
                .map(new FlightDetailsResponseDTOMapper(noPassengers, flightService))
                .collect(Collectors.toList());

        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    @Transactional
    public void updateTicketStatusAndDistance() {
        List<Ticket> ticketsToUpdate = ticketRepository.completedFlightTickets();
        for (Ticket t: ticketsToUpdate) {
            ticketRepository.updateToComplete(t);
            passengerRepository.addDistanceFlied(t.getPassenger(), t.getFlight());
        }
    }
}