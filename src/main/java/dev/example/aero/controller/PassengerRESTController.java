package dev.example.aero.controller;

import dev.example.aero.model.Passenger;
import dev.example.aero.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
public class PassengerRESTController {
    @Autowired
    private PassengerService passengerService;

    @PostMapping()
    public String insertPassenger(@Valid @RequestBody Passenger passenger){
        return "Passenger inserted";
    }
}
