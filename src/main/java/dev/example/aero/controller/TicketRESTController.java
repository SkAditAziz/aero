package dev.example.aero.controller;


import dev.example.aero.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/ticket")
public class TicketRESTController {
    @Autowired
    private TicketService ticketService;
    @PostMapping("/confirm")
    public String confirmTicket (@RequestBody Map<String, Object> req) { // Is it okay? or a class TicketConfirmReq ?
        try {
            ticketService.issueTicket(req);
            return "Ticket Confirmed";
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to issue ticket");
        }
    }
}
