package dev.example.aero.dto;

import dev.example.aero.enumeration.SeatClassType;
import dev.example.aero.enumeration.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailsResponseDTO {
    private String id;
    private LocalDate date;
    private String from;
    private String to;
    private SeatClassType seatClassType;
    private int seats;
    private BigDecimal totalFare;
    private TicketStatus ticketStatus;
}
