package dev.example.aero.repository;

import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// @RepositoryRestResource(path="f") // for using a custom path instead of 'flights'
public interface FlightRepository extends JpaRepository<Flight,String> {

    List<Flight> findByFromAirportAndToAirport(Airport byID, Airport byID1);

    @Query("Select f.id from Flight f")
    List<String> findAllIds();
}
