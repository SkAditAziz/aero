package dev.example.aero.dto.mapper;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.service.FlightService;

import java.util.function.Function;

public class FlightDetailsResponseDTOMapper implements Function<FlightSchedule, FlightDetailsResponseDTO> {
    private final int noPassengers;
    private final FlightRepository flightRepository;  // is there any better way?
    public FlightDetailsResponseDTOMapper(int noPassengers, FlightRepository flightRepository) {
        this.noPassengers = noPassengers;
        this.flightRepository = flightRepository;
    }
    @Override
    public FlightDetailsResponseDTO apply(FlightSchedule flightSchedule) {
        Flight flight = flightRepository.findById(flightSchedule.getFlightID()).orElse(null);
        return new FlightDetailsResponseDTO(
                flightSchedule.getFlightID(),
                flight.getAirline(),
                flight.getDepartureTime(),
                flight.getDuration(),
                flightSchedule.getSeatClassType(),
                flightSchedule.isSeatAvailable(noPassengers),
                flightSchedule.getTotalFare(noPassengers),
                flightSchedule.getId()
        );
    }
}
