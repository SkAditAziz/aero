package dev.example.aero.model;

import dev.example.aero.custom.CustomTicketIDGenerator;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLIGHT_ID", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PASSENGER_ID", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    private FlightSchedule flightSchedule;

    @Column(name = "SEAT_CLASS", nullable = false)
    private SeatClassType seatClassType;

    @Column(name = "SEATS", nullable = false)
    private int totalSeats;

    @Column(name = "TOTAL_FARE", nullable = false)
    private double totalFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TicketStatus ticketStatus;
}
