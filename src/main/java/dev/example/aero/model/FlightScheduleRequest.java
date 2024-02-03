package dev.example.aero.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlightScheduleRequest {
    private FlightSchedule flightSchedule;
    private int totalSeats;
    private Passenger passenger;
}
