package dev.example.aero.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserProvider {

    public static String getCurrentUsername () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("User Authed...");
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                System.out.println("Getting username...");
                username = userDetails.getUsername();
            }
        }
        return username;
    }
}
