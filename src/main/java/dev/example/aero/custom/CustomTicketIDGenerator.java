package dev.example.aero.custom;

import dev.example.aero.repository.TicketRepository;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomTicketIDGenerator implements IdentifierGenerator {
    @Autowired
    private TicketRepository ticketRepository;
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        String lastMaxID = ticketRepository.findMaxId();
        char prefix = lastMaxID.charAt(0);
        int number = Integer.parseInt(lastMaxID.substring(1));

        if (number == 99999) {
            prefix = (char) (prefix + 1);
            number = 1;
        } else
            number++;

        return prefix + String.format("%05d", number);
    }
}
