package dev.example.aero.dao;

import dev.example.aero.model.Airport;

import java.util.List;

public interface AirportDao {
    void save(Airport airport);
    List<Airport> findAll();
    Airport findByID(String code);
}
