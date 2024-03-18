package dev.example.aero.restcontroller;

import dev.example.aero.enumeration.SeatClassType;
import dev.example.aero.service.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seatInfo")
public class SeatInfoRestController {
    @GetMapping("/seatClasses")
    List<String> getSeatCLasses() {
        return SeatClassType.getAllSeatClassType();
    }

    @GetMapping("/highestSeats")
    int getHighestSeats() {
        return TicketService.HIGHEST_PERMISSIBLE_SEATS;
    }
}
