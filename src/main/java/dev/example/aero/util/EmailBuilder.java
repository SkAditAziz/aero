package dev.example.aero.util;

import dev.example.aero.model.Ticket;

import java.util.Map;

public class EmailBuilder {
    public static Map<String, String> getConfirmationEmailDetails(Ticket ticket) {
        return Map.of(
                "to", ticket.getPassenger().getEmail(),
                "sub", "Your Aero Ticket",
                "body", "Dear " + ticket.getPassenger().getFirstName() + " " +
                        ticket.getPassenger().getLastName() + ",\n\n" +
                        "Your ticket has been confirmed.\n\n" +
                        "Wishing a great journey!\n" +
                        "-Team Aero"
        );
    }
}
