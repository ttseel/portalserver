package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class SimHistoryRepositoryImpl implements SimHistoryRepository {
    private final EntityManager em;

    @Autowired
    public SimHistoryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<List<SimHistory>> readByUser(String user) {
        List<SimHistory> result = em.createQuery("select hist from SimHistory as hist where hist.user = :user")
                .setParameter("user", user)
                .getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimHistory>> readByUserAndSimulatorAndScenario(String user, String simulator, String scenario) {
        List<SimHistory> result = em.createQuery("select hist from SimHistory as hist where hist.user = :user and hist.simulator = :simulator and hist.scenario = :scenario")
                .setParameter("user", user)
                .setParameter("simulator", simulator.toString())
                .setParameter("scenario", scenario.toString())
                .getResultList();

        return Optional.ofNullable(result);
    }
}
