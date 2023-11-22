package dev.example.aero.repository;

import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// @RepositoryRestResource(path="f") // for using a custom path instead of 'flights'
public interface FlightRepository extends JpaRepository<Flight,String> {
    List<Flight> findByFromAirportAndToAirport(Airport byID, Airport byID1);
}
