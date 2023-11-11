package dev.example.aero.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.SeatClassTypeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="SEAT_INFORMATION")
@JsonIgnoreProperties({"flight"})   // Flight in SeatInfo creates a circular dependency, so ignore this while serialization
public class SeatInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_INFO_ID")
    private long id;

    @Convert(converter = SeatClassTypeConverter.class)
    @Column(name = "SEAT_CLASS_TYPE", nullable = false)
    private SeatClassType seatClassType;

    @Column(name = "AVAILABLE_SEATS", nullable = false)
    private int availableSeats;

    @Column(name = "FARE", nullable = false)
    private double fare;

    @ManyToOne
    @JoinColumn(name = "FLIGHT_ID", nullable = false)
    private Flight flight;

    public SeatInfo(SeatClassType seatClassType, int availableSeats, double fare) {
        this.seatClassType = seatClassType;
        this.availableSeats = availableSeats;
        this.fare = fare;
    }

    @Override
    public String toString() {
        return "SeatInfo{" +
                "id=" + id +
                ", seatClassType=" + seatClassType +
                ", availableSeats=" + availableSeats +
                ", fare=" + fare +
                '}';
    }
}
