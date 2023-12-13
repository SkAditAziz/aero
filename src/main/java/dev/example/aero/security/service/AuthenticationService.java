package dev.example.aero.security.service;

import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.userdetails.PassengerDetails;
import dev.example.aero.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class AuthenticationService {
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PassengerRepository passengerRepository;

    public AuthenticationResponse register(Passenger passenger) {
        passengerService.insertPassenger(passenger);
        return new AuthenticationResponse(jwtService.generateToken(new PassengerDetails(passenger)));
    }

    public AuthenticationResponse authenticate(Map<String, Object> request) {
        String username = (String) request.get("username");
        String pPassword = (String) request.get("password");
        // temporarily accepting both email and contact no! but how to enable both using PassengerDetails?
        if (passengerService.isContactNo(username)) {
            username = passengerRepository.findEmailByContactNo(username);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, pPassword));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw e;
        }
        Passenger passenger = passengerRepository.findByEmail(username);
        return new AuthenticationResponse(jwtService.generateToken(new PassengerDetails(passenger)));
    }
}
