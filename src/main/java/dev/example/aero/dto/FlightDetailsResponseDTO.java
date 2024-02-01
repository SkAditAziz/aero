package dev.example.aero.dto;

import dev.example.aero.enumeration.SeatClassType;
import lombok.*;

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
    private Double totalFare;
    private long scheduleId;
}
