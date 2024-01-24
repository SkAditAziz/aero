package dev.example.aero.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class AddFlightReqDTO {
    private LocalDate journeyDate;
    private List<String> flightIDs;
}
