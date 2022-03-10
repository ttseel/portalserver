package com.samsung.portalserver.repository;

import static com.samsung.portalserver.repository.DBConstants.usp_find_new_sim;

import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.schedule.job.NewSimulationJobDto;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public NewSimulationJobDto findNewSim(int executionServer) {
        StoredProcedureQuery query = em.createStoredProcedureQuery(usp_find_new_sim);
        query.registerStoredProcedureParameter("execution_server", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("o_fsl_name", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("o_user", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("o_simulator", String.class, ParameterMode.OUT);
        query.setParameter("execution_server", executionServer);

        boolean queryResult = query.execute();

        NewSimulationJobDto jobDto = new NewSimulationJobDto();
        jobDto.setFslName((String) query.getOutputParameterValue("o_fsl_name"));
        jobDto.setUser((String) query.getOutputParameterValue("o_user"));
        jobDto.setSimulator((String) query.getOutputParameterValue("o_simulator"));

        return jobDto;
    }

    @Override
    public Optional<List<SimBoard>> readUniqueFsl(String fslName, String user, String simulator) {
        List<SimBoard> queryResult = em.createQuery(
                "select sb from SimBoard as sb where sb.fsl_name = :fslName and sb.user = :user and sb.simulator = :simulator")
            .setParameter("fslName", fslName).setParameter("user", user)
            .setParameter("simulator", simulator).getResultList();

        return Optional.ofNullable(queryResult);
    }

    @Override
    public void commit() {
        em.flush();
    }
}
