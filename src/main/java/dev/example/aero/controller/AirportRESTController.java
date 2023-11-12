package dev.example.aero.controller;

import dev.example.aero.model.Airport;
import dev.example.aero.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/airports", "/airports/"})
public class AirportRESTController {
    @Autowired
    private AirportService airportService;

    @GetMapping("")
    public ResponseEntity<List<Airport>> airportsList() {
        List<Airport> airportList = airportService.getAllAirports();
        if (airportList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(airportList);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Airport> airportByCode(@PathVariable("code") String code) {
        Airport a = airportService.findById(code);
        if(a == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(a);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity deleteAirportByCode(@PathVariable("code") String code){
        if(airportService.deleteById(code))
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}
