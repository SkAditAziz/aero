package dev.example.aero.util;

import dev.example.aero.model.Ticket;
import dev.example.aero.service.EmailService;
import dev.example.aero.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import java.util.Map;

public class MessageReceiver {
    @Autowired
    private EmailService emailService;
    @Autowired
    private TicketService ticketService;

    @JmsListener(destination = "messagequeue.q")
    public void sendMail(Ticket ticket) {
        Map<String, String> emailDetails = ticketService.getEmailDetails(ticket);
        emailService.sendEmail(emailDetails.get("to"), emailDetails.get("sub"), emailDetails.get("body"));
    }
}