package dev.example.aero.util;

import dev.example.aero.model.Ticket;
import dev.example.aero.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

public class Receiver {
    @Autowired
    private EmailService emailService;

    @JmsListener(destination = "messagequeue.q")
    public void sendMail(Ticket ticket) {
        String to = ticket.getPassenger().getEmail();
        String sub = "Your Aero Ticket";
        String body = "Dear " + ticket.getPassenger().getFirstName() + " " +
                ticket.getPassenger().getLastName() + ",\n\n" +
                "Your ticket has been confirmed\n\n" +
                "Thanks,\n" +
                "Team Aero";
        emailService.sendEmail(to,sub,body);
    }
}