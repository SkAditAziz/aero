package dev.example.aero.messageQ;

import dev.example.aero.model.TicketWrapper;
import dev.example.aero.service.TicketService;
import dev.example.aero.service.utilService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageReceiver {
    @Autowired
    private EmailService emailService;
    @Autowired
    private TicketService ticketService;

    @JmsListener(destination = "messagequeue.q")
    public void saveTicketAndSendMail(TicketWrapper ticketWrapper) {
        String filePath = ticketService.saveTicketPdf(ticketWrapper.getTicket(), ticketWrapper.getPdfTicket());

        Map<String, String> emailDetails = ticketService.getEmailDetails(ticketWrapper.getTicket());
        emailService.sendEmailWithAttachment(emailDetails.get("to"), emailDetails.get("sub"), emailDetails.get("body"), filePath);
    }
}