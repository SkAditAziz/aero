package dev.example.aero.viewcontroller;

import dev.example.aero.security.service.UserProvider;
import dev.example.aero.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmFlightController {
    @Autowired
    private PassengerService passengerService;
    @PostMapping("/confirmFlight")
    public String confirmFlight(@RequestParam(name = "scheduleId") Long scheduleId, Model model) {
        Long currentUserId = null;
        String currentUsername = UserProvider.getCurrentUsername();
        if (currentUsername != null) {
            currentUserId = passengerService.getIdByUsername(currentUsername);
        }
        return "confirm_flight";
    }
}
