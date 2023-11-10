package dev.example.aero.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AIRPORT")
public class Airport {
    @Id
    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME",length = 1000,nullable = false)
    private String name;

    @Column(name = "CITY",nullable = false)
    private String city;
}
