package dev.example.aero.service;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.model.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirportService {
    @Autowired
    private AirportDao airportDao;

    public List<Airport> getAllAirports() {
        return airportDao.findAll();
    }

    public Airport findById(String code) {
        return airportDao.findByID(code);

    }

    public boolean deleteById(String code){
        return airportDao.deleteByID(code);
    }
}