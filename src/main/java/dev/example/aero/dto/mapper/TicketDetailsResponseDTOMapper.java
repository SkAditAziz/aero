package dev.example.aero.dto.mapper;

import dev.example.aero.dto.TicketDetailsResponseDTO;
import dev.example.aero.model.Ticket;

import java.util.function.Function;

public class TicketDetailsResponseDTOMapper implements Function<Ticket,TicketDetailsResponseDTO> {
    @Override
    public TicketDetailsResponseDTO apply(Ticket ticket) {
        return new TicketDetailsResponseDTO(
                ticket.getId(),
                ticket.getFlightSchedule().getFlightDate(),
                ticket.getFlight().getFromAirport().getCity(),
                ticket.getFlight().getToAirport().getCity(),
                ticket.getSeatClassType(),
                ticket.getTotalSeats(),
                ticket.getTotalFare(),
                ticket.getFlightStatus()
        );
    }
}
