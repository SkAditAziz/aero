package dev.example.aero.dto;

import dev.example.aero.model.Enumaration.SeatClassType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FlightSearchReqDTO {
    private String fromCode;
    private String toCode;
    private LocalDate journeyDate;
    private SeatClassType seatClass;
    private int noPassengers;
}
