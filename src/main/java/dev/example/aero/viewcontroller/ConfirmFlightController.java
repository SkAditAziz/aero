package dev.example.aero.viewcontroller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmFlightController {
    @PostMapping("/confirmFlight")
    public String confirmFlight(@RequestParam(name = "scheduleId") Long scheduleId, Model model) {
        System.out.println("In confirm Flight" + scheduleId);
        return "confirm_flight";
    }
}
