package dev.example.aero.service;

import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    public void insertPassenger(Passenger passenger) throws DataIntegrityViolationException {
        if (passenger.getContactNo().startsWith("1"))
           passenger.setContactNo("0" + passenger.getContactNo());
        passengerRepository.save(passenger);
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
}
