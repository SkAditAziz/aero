package dev.example.aero.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmFlightController {
    @PostMapping("/selectFlight")
    public String confirmFlight(@RequestParam(name = "scheduleId") Long scheduleId) {
        System.out.println("In confirm Flight");
        return "confirm_flight";
    }
}
