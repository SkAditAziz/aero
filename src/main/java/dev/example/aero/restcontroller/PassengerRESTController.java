package dev.example.aero.restcontroller;

import dev.example.aero.model.Passenger;
import dev.example.aero.service.PassengerService;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/passenger")
public class PassengerRESTController {
    private final PassengerService passengerService;

    @Autowired
    public PassengerRESTController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public String registerPassenger(@Valid @RequestBody Passenger passenger, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        try {
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
        if (passengerService.login(passenger)) {
            return "logged in successfully";
        } else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password! Login Failed!");
    }
}
