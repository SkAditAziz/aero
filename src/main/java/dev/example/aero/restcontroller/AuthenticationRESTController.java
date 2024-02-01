package dev.example.aero.restcontroller;

import dev.example.aero.model.Passenger;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationRESTController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationRESTController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@Valid @RequestBody Passenger passenger, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return authenticationService.register(passenger);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody Map<String, Object> loginRequest) {
        try {
            return authenticationService.authenticate((String) loginRequest.get("username"), (String) loginRequest.get("password"));
        } catch (Exception e) {
            // TODO why these message is not being sent as response?
            if (e instanceof UsernameNotFoundException) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide correct email or contact number!!!");
            } else if (e instanceof BadCredentialsException) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password! Login failed!!!");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }
}
