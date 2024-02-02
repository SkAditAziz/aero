package dev.example.aero.service;

import dev.example.aero.enumeration.Role;
import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    public PassengerService(PassengerRepository passengerRepository, PasswordEncoder passwordEncoder) {
        this.passengerRepository = passengerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void insertPassenger(Passenger inputPassenger) throws DataIntegrityViolationException {
        Passenger passenger = setPassengerProperties(inputPassenger);
        passengerRepository.save(passenger);
    }

    private Passenger setPassengerProperties(Passenger passenger) {
        String encodedPassword = passwordEncoder.encode(passenger.getPassword());
        passenger.setPassword(encodedPassword);
        passenger.setRole(Role.PASSENGER);
        if (passenger.getContactNo().startsWith("1"))
            passenger.setContactNo("0" + passenger.getContactNo());
        return passenger;
    }

    public boolean login(Passenger inputPassenger) {
        String inputPassengerContactNo = inputPassenger.getContactNo();
        String inputPassengerEmail = inputPassenger.getEmail();
        String inputPassengerPassword = inputPassenger.getPassword();

        Passenger passenger = null;
        if (inputPassengerContactNo != null) {
            passenger = passengerRepository.findByContactNo(inputPassengerContactNo);
        } else if (inputPassengerEmail != null) {
            passenger = passengerRepository.findByEmail(inputPassengerEmail);
        }

        if (passenger == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide correct Contact no or Email");
        }

        return (passwordEncoder.matches(inputPassengerPassword, passenger.getPassword()));
    }

    public boolean isContactNo(String username) {
        String contactNoRegex = "^(01\\d{9})$";
        return username.matches(contactNoRegex);
    }
}