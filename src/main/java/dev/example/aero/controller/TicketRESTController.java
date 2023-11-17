package dev.example.aero.controller;

import dev.example.aero.model.*;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.service.FlightScheduleService;
import dev.example.aero.service.FlightService;
import dev.example.aero.service.PassengerService;
import dev.example.aero.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/ticket")
public class TicketRESTController {
    @Autowired
    private FlightService flightService;
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private FlightScheduleService flightScheduleService;
    @Autowired
    private TicketService ticketService;
    @PostMapping("/confirm")
    public String confirmTicket (@RequestBody Map<String, Object> req) { // Is it okay? or a class TicketConfirmReq ?
        Flight f = flightService.getFlightById((String) req.get("flightId"));
        // TODO use jwt authToken to retrieve passenger
        Passenger p = passengerService.getPassengerById(((Integer) req.get("userId")).longValue());
        FlightSchedule fs = flightScheduleService.getScheduleByDate(LocalDate.parse((String) req.get("date")));
        SeatClassType sct = (SeatClassType) SeatClassType.fromCode((String) req.get("seatClassType"));
        int totalSeats = (int) req.get("noPassengers");
        double totalFare = (double) req.get("totalFare");
        TicketStatus ts = TicketStatus.UPCOMING;
        Ticket t = new Ticket("",f,p,fs,sct,totalSeats,totalFare,ts);
        try {
            ticketService.issueTicket(t);
            return "Ticket Confirmed";
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to isuue ticket");
        }
    }
}
