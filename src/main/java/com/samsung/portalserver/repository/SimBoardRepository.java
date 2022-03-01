package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimBoard;

import com.samsung.portalserver.schedule.job.NewSimulationJobDto;
import java.util.List;
import java.util.Optional;

public interface SimBoardRepository {

    Optional<List<SimBoard>> readByUser(String user);

    Optional<List<SimBoard>> readByStatus(SimBoardStatus status);

    Optional<List<SimBoard>> readByUserAndSimulator(String user, String simulator);

    Optional<List<SimBoard>> readByUserAndStatus(String user, SimBoardStatus status);

    Optional<SimBoard> readUniqueRecord(long no);

    Optional<SimBoard> readUniqueRecord(String user, String simulator, String scenario);

    void delete(SimBoard simBoard);

    void save(SimBoard simBoard);

    NewSimulationJobDto findNewSim(int executionServer);

    void commit();

    Optional<List<SimBoard>> readUniqueFsl(String fslName, String user, String simulator);
}
