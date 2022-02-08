package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Repository
public class SimBoardRepositoryImpl implements SimBoardRepository {
    private final EntityManager em;

    @Autowired
    public SimBoardRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<List<SimBoard>> readByUser(String user) {
        List<SimBoard> result = em.createQuery("select sb from SimBoard as sb where sb.user = :user")
                .setParameter("user", user)
                .getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimBoard>> readByStatus(SimBoardStatus status) {
        List<SimBoard> result = em.createQuery("select sb from SimBoard as sb where sb.status = :status")
                .setParameter("status", status.toString())
                .getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimBoard>> readByUserAndStatus(String user, SimBoardStatus status) {
        List<SimBoard> result = em.createQuery("select sb from SimBoard as sb where sb.user = :user and sb.status = :status")
                .setParameter("user", user)
                .setParameter("status", status.toString())
                .getResultList();

        return Optional.ofNullable(result);
    }

    public Optional<SimBoard> readUniqueRecord(String user, String simulator, String scenario) {
        List<SimBoard> result = em.createQuery("select sb from SimBoard as sb where sb.user = :user and sb.simulator = :simulator and sb.scenario = :scenario")
                .setParameter("user", user)
                .setParameter("simulator", simulator.toString())
                .setParameter("scenario", scenario.toString())
                .getResultList();

        if (result.size() > 1) {
            throw new IllegalStateException(
                    String.format(
                            "There are duplicate scenarios. user: %s, simulator: %s, scenario: %s",
                            user, simulator, scenario
                    )
            );
        }

        return Optional.ofNullable(result.get(0));
    }

    @Override
    public void delete(SimBoard simBoard) {
        em.remove(simBoard);
    }

    @Override
    public void save(SimBoard simBoard) {
        em.persist(simBoard);
    }
}
