package dev.example.aero.util;

import dev.example.aero.model.Ticket;
import org.springframework.jms.annotation.JmsListener;

public class Receiver {
    @JmsListener(destination = "messagequeue.q")
    public void sendMail(Ticket ticket) {
        System.out.println("In receiver\n" + ticket.getId());
    }
}