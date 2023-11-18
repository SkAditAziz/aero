package dev.example.aero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FLIGHT_SCHEDULE", uniqueConstraints = @UniqueConstraint(name = "unique-date-schedule", columnNames = "FLIGHT_DATE"))
public class FlightSchedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long id;

    @Column(name = "FLIGHT_DATE", nullable = false)
    private LocalDate flightDate;

// This didn't work, as we need flight seats to manipulate for each of the schedule
//    @ElementCollection
//    @CollectionTable(name = "SCHEDULED_FLIGHTS", joinColumns = @JoinColumn(name = "SCHEDULE_ID"),
//            uniqueConstraints = @UniqueConstraint(name = "unique-date-flight", columnNames = {"SCHEDULE_ID", "FLIGHT_ID"}))
//    @Column(name = "FLIGHT_ID")
//    private List<String> flightIds;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<Flight> flights = new HashSet<>();

    public FlightSchedule(LocalDate flightDate, Set<Flight> flights) {
        this.flightDate = flightDate;
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "FlightSchedule{" +
                "id=" + id +
                ", flightDate=" + flightDate +
                ", flights=" + Arrays.toString(flights.toArray()) +
                '}';
    }
}
