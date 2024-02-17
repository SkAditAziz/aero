package dev.example.aero.model;

import dev.example.aero.enumeration.FlightStatus;
import dev.example.aero.enumeration.SeatClassType;
import dev.example.aero.enumeration.SeatClassTypeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FLIGHT_SCHEDULE", uniqueConstraints = @UniqueConstraint(name = "unique-date-schedule", columnNames = {"FLIGHT_DATE", "FLIGHT_ID", "SEAT_CLASS_TYPE"}))
public class FlightSchedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long id;

    @Column(name = "FLIGHT_DATE", nullable = false)
    private LocalDate flightDate;

    @Column(name = "FLIGHT_ID")
    private String flightID;

    @Convert(converter = SeatClassTypeConverter.class)
    @Column(name = "SEAT_CLASS_TYPE", nullable = false)
    private SeatClassType seatClassType;

    @Column(name = "AVAILABLE_SEATS", nullable = false)
    private int availableSeats;

    @Column(name = "FARE", nullable = false)
    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private FlightStatus flightStatus;

    public FlightSchedule(LocalDate flightDate, String flightID, SeatClassType seatClassType, int availableSeats, BigDecimal fare, FlightStatus flightStatus) {
        this.flightDate = flightDate;
        this.flightID = flightID;
        this.seatClassType = seatClassType;
        this.availableSeats = availableSeats;
        this.fare = fare;
        this.flightStatus = flightStatus;
    }

    public BigDecimal getTotalFare(int noPassenger) {
        return isSeatAvailable(noPassenger) ? fare.multiply(BigDecimal.valueOf(noPassenger)) : BigDecimal.valueOf(0);
    }

    public boolean isSeatAvailable(int noPassenger) {
        return availableSeats >= noPassenger;
    }
}
