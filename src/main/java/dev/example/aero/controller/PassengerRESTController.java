package dev.example.aero.controller;

import dev.example.aero.model.Passenger;
import dev.example.aero.service.PassengerService;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/passenger")
public class PassengerRESTController {
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public String registerPassenger(@Valid @RequestBody Passenger passenger) {
        try {
            String encodedPassword = passwordEncoder.encode(passenger.getPassword());
            passenger.setPassword(encodedPassword);
            passengerService.insertPassenger(passenger);
            return "Passenger inserted";
        }
        catch (DataIntegrityViolationException e) {
            // cannot catch the ConstraintViolationException since JPA will wrap it as a PersistenceException
            if (e.getCause() instanceof PersistenceException pe) {
                if (pe.getMessage().contains("unique_contact"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Try another Contact No");
                if (pe.getMessage().contains("unique_email"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Try another Email");
            }
        }
        return "Internal Error!";
    }

    @PostMapping("/login")
    public String loginPassenger(@RequestBody Passenger passenger) {
        String contactNo = passenger.getContactNo();
        String email = passenger.getEmail();
        String rawPassword = passenger.getPassword();
        String encodedPassword = "";

        if ((contactNo != null) && (passengerService.getPassengerByContact(contactNo) != null)) {
            encodedPassword = passengerService.getPassengerPasswordByContact(contactNo);
        } else if ((email != null) && (passengerService.getPassengerByEmail(email) != null)) {
            encodedPassword = passengerService.getPassengerPasswordByEmail(email);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide correct Contact no or Email");
        }

        if (passwordEncoder.matches(rawPassword,encodedPassword)) {
            return "logged in successfully";
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password! Login Failed!");
    }
}
