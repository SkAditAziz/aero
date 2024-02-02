package dev.example.aero.security.service;

import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserProvider {
    private static PassengerRepository passengerRepository;

    public UserProvider(PassengerRepository passengerRepository) {
        UserProvider.passengerRepository = passengerRepository;
    }

    public static Passenger getCurrentPassenger() {
        String username = getCurrentUsername();
        return passengerRepository.findByUsername(username);
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                username = userDetails.getUsername();
            }
        }
        return username;
    }
}
