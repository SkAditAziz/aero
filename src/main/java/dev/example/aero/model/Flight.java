package dev.example.aero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FLIGHT", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueDeparture", columnNames = {"FROM_AIRPORT_CODE","DEPART_TIME"}),
        @UniqueConstraint(name = "UniqueArrival", columnNames = {"TO_AIRPORT_CODE","ARRIVAL_TIME"})
//        @UniqueConstraint(name = "UniqueRunwayUseDeparture", columnNames = {"FROM_AIRPORT_CODE","ARRIVAL_TIME","DEPART_TIME"}),
//        @UniqueConstraint(name = "UniqueRunwayUseArrival", columnNames = {"TO_AIRPORT_CODE","ARRIVAL_TIME", "DEPART_TIME"})
})
public class Flight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom-flight-id-generator")
    @GenericGenerator(
            name = "custom-flight-id-generator",
            strategy = "dev.example.aero.custom.CustomFlightIDGenerator"
    )
    @Column(name = "FLIGHT_ID")
    private String id;

    @Column(name = "AIRLINE_NAME")
    private String airline;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "FROM_AIRPORT_CODE", nullable = false)
    private Airport fromAirport;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "TO_AIRPORT_CODE", nullable = false)
    private Airport toAirport;

    @Column(name = "DEPART_TIME", nullable = false)
    private LocalTime departureTime;

    @Column(name = "DEPART_TIME_ZONE", nullable = false)
    private ZoneId departureTimeZone;

    @Column(name = "ARRIVAL_TIME", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "ARRIVAL_TIME_ZONE", nullable = false)
    private ZoneId arrivalTimeZone;

    @Column(name="DISTANCE(KM)")
    private double distance;
}
