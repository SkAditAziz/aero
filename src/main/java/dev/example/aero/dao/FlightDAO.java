package dev.example.aero.dao;

import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.RequestEntity;

import java.util.List;

public interface FlightDAO extends JpaRepository<Flight,String> {
    @Query(value = "select f from Flight f where f.id like %:airportCode%")
    List<Flight> findOnAirport(@Param("airportCode")String airportCode);

    List<Flight> findByFromAirportAndToAirport(Airport byID, Airport byID1);
}
