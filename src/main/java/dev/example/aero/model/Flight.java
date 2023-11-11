package dev.example.aero.model;

import dev.example.aero.custom.CustomFlightIDGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
/*
    It's a temporary solution. when I was returning a bunch of data from FlightScheduleService.getFlightsOnDate()
    com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0]->dev.example.aero.model.Flight$HibernateProxy$fDX4dqIt["hibernateLazyInitializer"])
    Briefly : is related to the serialization of Hibernate proxy objects. When you fetch an entity from the database using Hibernate, it may be wrapped in a proxy object (in this case, a ByteBuddyInterceptor) for lazy loading
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // TODO: add FlightDTO and remove this ignore property
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

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatInfo> seatInfoList = new ArrayList<>();

    public Flight(String id, String airline, Airport fromAirport, Airport toAirport, LocalTime departureTime, ZoneId departureTimeZone, LocalTime arrivalTime, ZoneId arrivalTimeZone, double distance) {
        this.id = id;
        this.airline = airline;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.departureTime = departureTime;
        this.departureTimeZone = departureTimeZone;
        this.arrivalTime = arrivalTime;
        this.arrivalTimeZone = arrivalTimeZone;
        this.distance = distance;
    }
}
