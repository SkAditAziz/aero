package dev.example.aero.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class TicketWrapper implements Serializable {
    private Ticket ticket;
    private byte[] pdfTicket;
}
