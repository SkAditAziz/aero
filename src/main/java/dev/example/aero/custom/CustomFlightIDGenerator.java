package dev.example.aero.custom;

import dev.example.aero.model.Flight;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.Arrays;
import java.util.Optional;

public class CustomFlightIDGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        Flight currentFlight = (Flight) o;
        String fromAirportCode = currentFlight.getFromAirport().getCode();
        String toAirportCode = currentFlight.getToAirport().getCode();
        String customIDPrefix = fromAirportCode + "-" + toAirportCode + "-";

        TypedQuery<Integer> getMaxIDQuery = sharedSessionContractImplementor.createQuery(
                "SELECT MAX(SUBSTRING(f.id, :startIdx)) FROM Flight f WHERE f.id LIKE :customIDPrefix", Integer.class);
        getMaxIDQuery.setParameter("startIdx",customIDPrefix.length()+1);
        getMaxIDQuery.setParameter("customIDPrefix", customIDPrefix + "%");

        String maxIDString = (String) getMaxIDQuery.getResultList().toArray()[0];
        int sequence = (maxIDString != null) ? (Integer.parseInt(maxIDString) + 1): 1;

        return customIDPrefix + String.format("%03d", sequence);
    }
}
