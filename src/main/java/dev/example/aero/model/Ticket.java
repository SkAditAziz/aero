package dev.example.aero.model;

import dev.example.aero.custom.CustomTicketIDGenerator;
import dev.example.aero.enumeration.FlightStatus;
import dev.example.aero.enumeration.SeatClassType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TICKET")
public class Ticket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket-id-generator")
    @GenericGenerator(
            name = "ticket-id-generator",
            type = CustomTicketIDGenerator.class
    )
    @Column(name = "TICKET_ID")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FLIGHT_ID", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PASSENGER_ID", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    private FlightSchedule flightSchedule;

    @Column(name = "SEAT_CLASS", nullable = false)
    private SeatClassType seatClassType;

    @Column(name = "SEATS", nullable = false)
    private int totalSeats;

    @Column(name = "TOTAL_FARE", nullable = false)
    private BigDecimal totalFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private FlightStatus flightStatus;

    public Ticket(Flight flight, Passenger passenger, FlightSchedule flightSchedule, SeatClassType seatClassType, int totalSeats, BigDecimal totalFare, FlightStatus flightStatus) {
        this.flight = flight;
        this.passenger = passenger;
        this.flightSchedule = flightSchedule;
        this.seatClassType = seatClassType;
        this.totalSeats = totalSeats;
        this.totalFare = totalFare;
        this.flightStatus = flightStatus;
    }
}
