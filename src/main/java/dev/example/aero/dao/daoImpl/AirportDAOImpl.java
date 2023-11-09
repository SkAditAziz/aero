package dev.example.aero.dao.daoImpl;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.model.Airport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AirportDAOImpl implements AirportDao {

    @Autowired
    private EntityManager entityManager;

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
}
