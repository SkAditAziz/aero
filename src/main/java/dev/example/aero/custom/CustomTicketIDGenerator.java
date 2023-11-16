package dev.example.aero.custom;

import jakarta.persistence.TypedQuery;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class CustomTicketIDGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        TypedQuery<String> getMaxIDQuery = sharedSessionContractImplementor.createQuery(
                "select max(t.id) from Ticket t", String.class);

        String lastMaxID = (String) getMaxIDQuery.getResultList().toArray()[0];

        if (lastMaxID == null)
            return "A00001";

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
