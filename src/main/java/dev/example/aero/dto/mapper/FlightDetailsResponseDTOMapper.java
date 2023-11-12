package dev.example.aero.dto.mapper;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Flight;
import dev.example.aero.model.SeatInfo;

import java.util.function.Function;

public class FlightDetailsResponseDTOMapper implements Function<Flight, FlightDetailsResponseDTO> {
    private final String classType;
    private final int noPassengers;

    public FlightDetailsResponseDTOMapper(String classType, int noPassengers) {
        this.classType = classType;
        this.noPassengers = noPassengers;
    }
    @Override
    public FlightDetailsResponseDTO apply(Flight flight) {
        return new FlightDetailsResponseDTO(
                flight.getId(),
                flight.getAirline(),
                flight.getDepartureTime(),
                flight.getDuration(),
                flight.getSeatInfoList()
                        .stream()
                        .filter(seatInfo -> SeatClassType.fromCode(classType) == seatInfo.getSeatClassType())
                        .findFirst()
                        .map(SeatInfo::getSeatClassType)
                        .orElse(null),
                flight.getSeatInfoList()
                        .stream()
                        .filter(seatInfo -> SeatClassType.fromCode(classType) == seatInfo.getSeatClassType())
                        .anyMatch(seatInfo -> seatInfo.getAvailableSeats() >= noPassengers),
                flight.getSeatInfoList()
                        .stream()
                        .filter(seatInfo -> SeatClassType.fromCode(classType) == seatInfo.getSeatClassType() &&
                                seatInfo.getAvailableSeats() >= noPassengers)
                        .mapToDouble(seatInfo -> noPassengers * seatInfo.getFare())
                        .findFirst()
                        .orElse(0.0));
    }
}
