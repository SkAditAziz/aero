package dev.example.aero.dao.daoImpl;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.model.Airport;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class AirportDAOImpl implements AirportDao {

    private EntityManager entityManager;

    public AirportDAOImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Airport airport) {
        entityManager.persist(airport);
    }
}
