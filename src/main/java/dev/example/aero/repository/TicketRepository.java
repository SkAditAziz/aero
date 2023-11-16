package dev.example.aero.repository;

import dev.example.aero.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    @Query("select max(t.id) from Ticket t")
    String findMaxId();
}
