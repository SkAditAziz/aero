package dev.example.aero.repository;

import dev.example.aero.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> getTicketsByPassengerId(long passengerId);
}
