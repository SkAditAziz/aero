package dev.example.aero.model;

import dev.example.aero.custom.CustomFlightIDGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FLIGHT", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueDeparture", columnNames = {"FROM_AIRPORT_CODE","DEPART_TIME"}),
        @UniqueConstraint(name = "UniqueArrival", columnNames = {"TO_AIRPORT_CODE","ARRIVAL_TIME"})
})
public class Flight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom-flight-id-generator")
    @GenericGenerator(
            name = "custom-flight-id-generator",
            type = CustomFlightIDGenerator.class
    )
    @Column(name = "FLIGHT_ID")
    private String id;

    @Column(name = "AIRLINE_NAME")
    private String airline;

    @ManyToOne
    @JoinColumn(name = "FROM_AIRPORT_CODE", nullable = false)
    private Airport fromAirport;

    @ManyToOne
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatInfo> seatInfoList = new ArrayList<>();

    public String getDuration() {
        ZonedDateTime departureZonedDateTime = ZonedDateTime.of(ZonedDateTime.now().toLocalDate(),this.departureTime,this.departureTimeZone);
        ZonedDateTime arrivalZonedDateTime = ZonedDateTime.of(ZonedDateTime.now().toLocalDate(),this.arrivalTime,this.arrivalTimeZone);
        // Ensure arrival is after departure
        if (arrivalZonedDateTime.isBefore(departureZonedDateTime)) {
            arrivalZonedDateTime = arrivalZonedDateTime.plusDays(1);
        }
        Duration duration = Duration.between(arrivalZonedDateTime,departureZonedDateTime);
        long days = Math.abs(duration.toDays());
        long hours = Math.abs(duration.toHoursPart());
        long minutes = Math.abs(duration.toMinutesPart());
        return (days == 0) ? ((hours != 0) ? (hours + " H " + minutes + " M") : (minutes + " M")) : (days + " D " +  hours + " H " + minutes + " M");
    }
}
