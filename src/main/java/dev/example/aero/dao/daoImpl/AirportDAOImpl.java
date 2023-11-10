package dev.example.aero.dao.daoImpl;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.dao.FlightDAO;
import dev.example.aero.model.Airport;
import dev.example.aero.model.Flight;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class AirportDAOImpl implements AirportDao {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private FlightDAO flightDAO;

    @Override
    @Transactional
    public void save(Airport airport) {
        entityManager.persist(airport);
    }

    @Override
    public List<Airport> findAll() {
        TypedQuery<Airport> sql = entityManager.createQuery("select a from Airport a", Airport.class);
        return sql.getResultList();
    }

    @Override
    public Airport findByID(String code) {
        return entityManager.find(Airport.class, code);
    }

    @Override
    @Transactional
    public boolean deleteByID(String code) {
        Airport a = findByID(code);
        if(a == null)
            return false;

        List<Flight> flightsToDelete = flightDAO.findOnAirport(code);

       if (!flightsToDelete.isEmpty()) {
            for (Flight deleteFlight : flightsToDelete)
                flightDAO.delete(deleteFlight);
        }
        entityManager.remove(a);
        return true;
    }
}
