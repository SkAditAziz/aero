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
        Passenger passengerByContactNo = passengerRepository.findByContactNo(contactNo);
        Passenger passengerByEmail = passengerRepository.findByEmail(email);

        if ((contactNo != null) && (passengerByContactNo != null)) {
            encodedPassword = passengerByContactNo.getPassword();
        } else if ((email != null) && (passengerByEmail != null)) {
            encodedPassword = passengerByEmail.getPassword();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide correct Contact no or Email");
        }
        return (passwordEncoder.matches(rawPassword, encodedPassword));
    }

    public boolean isContactNo(String username) {
        String contactNoRegex = "^(01\\d{9})$";
        return username.matches(contactNoRegex);
    }

    public Passenger getPassengerByUsername(String username) {
        return passengerRepository.findById(passengerRepository.findByUsername(username).getId()).orElse(null);
    }
}
