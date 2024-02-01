package dev.example.aero.messageQ;

import dev.example.aero.model.Ticket;
import dev.example.aero.model.TicketWrapper;
import dev.example.aero.service.TicketService;
import dev.example.aero.service.utilService.EmailService;
import dev.example.aero.util.EmailBuilder;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageReceiver {
    private final EmailService emailService;
    private final TicketService ticketService;

    public MessageReceiver(EmailService emailService, TicketService ticketService) {
        this.emailService = emailService;
        this.ticketService = ticketService;
    }

    @JmsListener(destination = "messagequeue.q")
    public void processMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Object object = objectMessage.getObject();
            if (object instanceof TicketWrapper) {
                saveTicketAndSendMail((TicketWrapper) object);
            }
            else if (object instanceof Ticket) {
                sendCancelMail((Ticket) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTicketAndSendMail(TicketWrapper ticketWrapper) {
        String filePath = ticketService.saveTicketPdf(ticketWrapper.getTicket(), ticketWrapper.getPdfTicket());

        Map<String, String> emailDetails = EmailBuilder.getConfirmationEmailDetails(ticketWrapper.getTicket());
        emailService.sendEmail(emailDetails.get("to"), emailDetails.get("sub"), emailDetails.get("body"), filePath);
    }

    public void sendCancelMail(Ticket ticket) {
        Map<String, String> emailDetails = EmailBuilder.getCancelEmailDetails(ticket);
        emailService.sendEmail(emailDetails.get("to"), emailDetails.get("sub"), emailDetails.get("body"));
    }
}