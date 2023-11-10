package dev.example.aero.repository;

import dev.example.aero.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// @RepositoryRestResource(path="f") // for using a custom path instead of 'flights'
public interface FlightRepository extends JpaRepository<Flight,String> {
}
