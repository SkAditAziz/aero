package dev.example.aero.security.service;

import dev.example.aero.model.Passenger;
import dev.example.aero.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserProvider {
    @Autowired
    private static PassengerService passengerService;

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

    public static Passenger getCurrentPassenger() {
        String username = getCurrentUsername();
        return passengerService.getPassengerByUsername(username);
    }

    public static boolean isLoggedIn() {
        return getCurrentUsername() != null;
    }
}
