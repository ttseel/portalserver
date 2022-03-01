package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimHistory;
import com.samsung.portalserver.schedule.job.SimulationJob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SimHistoryRepositoryImpl implements SimHistoryRepository {

    private final EntityManager em;

    @Autowired
    public SimHistoryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<List<SimHistory>> readByUser(String user) {
        List<SimHistory> result = em.createQuery(
                "select hist from SimHistory as hist where hist.user = :user")
            .setParameter("user", user).getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<List<SimHistory>> readByUserAndSimulatorAndScenario(String user,
        String simulator, String scenario) {
        List<SimHistory> result = em.createQuery(
                "select hist from SimHistory as hist where hist.user = :user and hist.simulator = :simulator and hist.scenario = :scenario")
            .setParameter("user", user).setParameter("simulator", simulator.toString())
            .setParameter("scenario", scenario.toString()).getResultList();

        return Optional.ofNullable(result);
    }

    @Override
    public Long moveFromBoardToHistory(SimulationJob job) {
        StoredProcedureQuery query = em.createStoredProcedureQuery(
            "HYPPEOPLE.usp_move_from_board_to_history");
        query.registerStoredProcedureParameter("sim_board_no", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("h_completed_no", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("h_termination_reason", String.class,
            ParameterMode.IN);
        query.registerStoredProcedureParameter("h_end_date", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("hist_no", Long.class, ParameterMode.OUT);
        query.setParameter("sim_board_no", job.getSimBoardPKNo());
        query.setParameter("h_completed_no", job.getCurrent_rep());
        query.setParameter("h_termination_reason", job.getTermination_reason());
        query.setParameter("h_end_date", job.getEnd_date());
        boolean queryResult = query.execute();
        Long result = (Long) query.getOutputParameterValue("hist_no");
        return result;
    }
}
