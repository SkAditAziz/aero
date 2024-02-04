package dev.example.aero.dto;

import dev.example.aero.enumeration.SeatClassType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDetailsResponseDTO {
    private String id;
    private String airlineName;
    private LocalTime departureTime;
    private String duration;
    private SeatClassType seatClassType;
    private boolean seatAvailable;
    private BigDecimal totalFare;
    private long scheduleId;
}
