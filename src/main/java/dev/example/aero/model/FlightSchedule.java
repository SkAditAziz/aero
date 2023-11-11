package dev.example.aero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
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

    @ElementCollection
    @CollectionTable(name = "SCHEDULED_FLIGHTS", joinColumns = @JoinColumn(name = "SCHEDULE_ID"),
            uniqueConstraints = @UniqueConstraint(name = "unique-date-flight", columnNames = {"SCHEDULE_ID", "FLIGHT_ID"}))
    @Column(name = "FLIGHT_ID")
    private List<String> flightIds;

    public FlightSchedule(LocalDate flightDate, List<String> flightIds) {
        this.flightDate = flightDate;
        this.flightIds = flightIds;
    }
}
