package dev.example.aero.dto.mapper;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.service.FlightService;

import java.util.function.Function;

public class FlightDetailsResponseDTOMapper implements Function<FlightSchedule, FlightDetailsResponseDTO> {
    private final int noPassengers;
    private final FlightService flightService;  // is there any better way?
    public FlightDetailsResponseDTOMapper(int noPassengers, FlightService flightService) {
        this.noPassengers = noPassengers;
        this.flightService = flightService;
    }
    @Override
    public FlightDetailsResponseDTO apply(FlightSchedule flightSchedule) {
        Flight flight = flightService.getFlightById(flightSchedule.getFlightID());
        return new FlightDetailsResponseDTO(
                flightSchedule.getFlightID(),
                flight.getAirline(),
                flight.getDepartureTime(),
                flight.getDuration(),
                flightSchedule.getSeatClassType(),
                flightSchedule.isSeatAvailable(noPassengers),
                flightSchedule.getTotalFare(noPassengers),
                flightSchedule.getId());
    }
}
