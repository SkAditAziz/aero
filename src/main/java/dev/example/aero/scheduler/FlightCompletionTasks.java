package dev.example.aero.scheduler;

import dev.example.aero.enumeration.TicketStatus;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightCompletionTasks {
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;

    public FlightCompletionTasks(TicketRepository ticketRepository, PassengerRepository passengerRepository) {
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
    }

    @Scheduled(cron = "0 1 0 * * *") // run it on every day 00:01 AM
    @Transactional
    public void updateTicketStatusAndDistance() {
        List<Ticket> ticketsToUpdate = ticketRepository.completedFlightTickets();
        for (Ticket t: ticketsToUpdate) {
            t.setTicketStatus(TicketStatus.COMPLETED);
            ticketRepository.save(t);
            passengerRepository.addDistanceFlied(t.getPassenger(), t.getFlight());
        }
    }
}
