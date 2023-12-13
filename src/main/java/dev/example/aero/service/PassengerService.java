package dev.example.aero.service;

import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.JwtService;
import dev.example.aero.security.userdetails.PassengerDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class PassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public void insertPassenger(Passenger passenger) throws DataIntegrityViolationException {
        String encodedPassword = passwordEncoder.encode(passenger.getPassword());
        passenger.setPassword(encodedPassword);
        if (passenger.getContactNo().startsWith("1"))
           passenger.setContactNo("0" + passenger.getContactNo());
        passengerRepository.save(passenger);
    }

    public boolean login(Passenger passenger) {
        String contactNo = passenger.getContactNo();
        String email = passenger.getEmail();
        String rawPassword = passenger.getPassword();
        String encodedPassword = "";

        if ((contactNo != null) && (getPassengerByContact(contactNo) != null)) {
            encodedPassword = getPassengerPasswordByContact(contactNo);
        } else if ((email != null) && (getPassengerByEmail(email) != null)) {
            encodedPassword = getPassengerPasswordByEmail(email);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide correct Contact no or Email");
        }
        return (passwordEncoder.matches(rawPassword,encodedPassword));
    }

    public String getPassengerPasswordByContact(String contactNo) {
        return passengerRepository.findPasswordByContactNo(contactNo);
    }

    public String getPassengerPasswordByEmail(String email) {
        return passengerRepository.findPasswordByEmail(email);
    }

    public Passenger getPassengerByContact(String contactNo) {
        return passengerRepository.findByContactNo(contactNo);
    }

    public Passenger getPassengerByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }

    public Passenger getPassengerById(long userId) {
        return passengerRepository.findById(userId).orElse(null);
    }

    public AuthenticationResponse register(Passenger passenger) {
        insertPassenger(passenger);
        return new AuthenticationResponse(jwtService.generateToken(new PassengerDetails(passenger)));
    }

    public AuthenticationResponse authenticate(Map<String, Object> request) {
        String username = (String) request.get("username");
        String pPassword = (String) request.get("password");
        // temporarily accepting both email and contact no! but how to enable both using PassengerDetails?
        if (isContactNo(username)) {
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

    private boolean isContactNo(String username) {
        String contactNoRegex = "^(01\\d{9})$";
        return username.matches(contactNoRegex);
    }
}
