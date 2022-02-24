package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
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
        List<SimBoard> result = em.createQuery(
                "select sb from SimBoard as sb where sb.user = :user").setParameter("user", user)
            .getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimBoard>> readByStatus(SimBoardStatus status) {
        List<SimBoard> result = em.createQuery(
                "select sb from SimBoard as sb where sb.status = :status")
            .setParameter("status", status.toString()).getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimBoard>> readByUserAndSimulator(String user, String simulator) {
        List<SimBoard> result = em.createQuery(
                "select sb from SimBoard as sb where sb.user = :user and sb.simulator = :simulator")
            .setParameter("user", user).setParameter("simulator", simulator).getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimBoard>> readByUserAndStatus(String user, SimBoardStatus status) {
        List<SimBoard> result = em.createQuery(
                "select sb from SimBoard as sb where sb.user = :user and sb.status = :status")
            .setParameter("user", user).setParameter("status", status.toString()).getResultList();

        return Optional.ofNullable(result);
    }

    public Optional<SimBoard> readUniqueRecord(long no) {
        List<SimBoard> result = em.createQuery("select sb from SimBoard as sb where sb.no = :no")
            .setParameter("no", no).getResultList();

        if (result.size() == 0) {
            return Optional.empty();
        }
        if (result.size() > 1) {
            throw new IllegalStateException(
                String.format("There are duplicate scenarios. no: %s, simulator: %s, scenario: %s",
                    String.valueOf(no)));
        }

        return Optional.ofNullable(result.get(0));
    }

    public Optional<SimBoard> readUniqueRecord(String user, String simulator, String scenario) {
        Optional<SimBoard> uniqueRecord = Optional.empty();
        List<SimBoard> queryResult = em.createQuery(
                "select sb from SimBoard as sb where sb.user = :user and sb.simulator = :simulator and sb.scenario = :scenario")
            .setParameter("user", user).setParameter("simulator", simulator)
            .setParameter("scenario", scenario).getResultList();

        if (queryResult.size() == 1) {
            uniqueRecord = Optional.ofNullable(queryResult.get(0));
        } else if (queryResult.size() > 1) {
            throw new IllegalStateException(String.format(
                "There are duplicate scenarios. user: %s, simulator: %s, scenario: %s", user,
                simulator, scenario));
        }

        return uniqueRecord;
    }

    @Override
    public void delete(SimBoard simBoard) {
        em.remove(simBoard);
    }

    @Override
    public void save(SimBoard simBoard) {
        em.persist(simBoard);
    }

    @Override
    public long findNewSim(int executionServer) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("HYPPEOPLE.usp_find_new_sim");
        query.registerStoredProcedureParameter("execution_server", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("sim_no", Long.class, ParameterMode.OUT);
        query.setParameter("execution_server", executionServer);
        boolean queryResult = query.execute();
        Long result = (Long) query.getOutputParameterValue("sim_no");

        return result;
    }

    @Override
    public void commit() {
        em.flush();
    }
}
