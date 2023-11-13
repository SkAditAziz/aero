package dev.example.aero.repository;

import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// @RepositoryRestResource(path="f") // for using a custom path instead of 'flights'
public interface FlightRepository extends JpaRepository<Flight,String> {
    @Query(value = "select f from Flight f where f.id like %:airportCode%")
    List<Flight> findOnAirport(@Param("airportCode")String airportCode);
    List<Flight> findByFromAirportAndToAirport(Airport byID, Airport byID1);
}
