package dev.example.aero.model;

import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.SeatClassTypeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="SEAT_INFORMATION")
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
}
