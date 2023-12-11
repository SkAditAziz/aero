package dev.example.aero.security;

import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PassengerDetailsService implements UserDetailsService {
    private final PassengerRepository passengerRepository;

    public PassengerDetailsService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findByEmail(username);
        if (passenger == null) {
            throw new UsernameNotFoundException("Passenger with email " + username + " not found");
        }
        return new PassengerDetails(passenger);
    }
}
