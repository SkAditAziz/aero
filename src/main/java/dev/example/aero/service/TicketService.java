package dev.example.aero.service;

import dev.example.aero.model.Ticket;
import dev.example.aero.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    public void issueTicket(Ticket t) {
        ticketRepository.save(t);
    }
}
