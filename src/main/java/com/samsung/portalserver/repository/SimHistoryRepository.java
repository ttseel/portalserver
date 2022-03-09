package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimHistory;
import com.samsung.portalserver.schedule.job.ScenarioJob;
import java.util.List;
import java.util.Optional;

public interface SimHistoryRepository {

    Optional<List<SimHistory>> readByUser(String user);

    Optional<List<SimHistory>> readByUserAndSimulatorAndScenario(String user, String simulator,
        String scenario);

    Long moveFromBoardToHistory(ScenarioJob job);
}
