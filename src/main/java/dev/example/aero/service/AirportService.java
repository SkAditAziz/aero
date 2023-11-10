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

    public ResponseEntity<List<Airport>> getAllAirports() {
        List<Airport> airportList = airportDao.findAll();
        if (airportList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(airportList);
    }

    public ResponseEntity<Airport> findById(String code) {
        Airport a = airportDao.findByID(code);
        if(a == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(a);
    }

    public ResponseEntity deleteById(String code){
        if(airportDao.deleteByID(code))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }
}