package dev.example.aero.service;

import dev.example.aero.model.Enumaration.Role;
import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void insertPassenger(Passenger passenger) throws DataIntegrityViolationException {
        String encodedPassword = passwordEncoder.encode(passenger.getPassword());
        passenger.setPassword(encodedPassword);
        passenger.setRole(Role.PASSENGER);
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

    public boolean isContactNo(String username) {
        String contactNoRegex = "^(01\\d{9})$";
        return username.matches(contactNoRegex);
    }
}
