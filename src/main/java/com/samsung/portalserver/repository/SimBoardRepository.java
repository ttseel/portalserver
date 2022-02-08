package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.SimBoard;

import java.util.List;
import java.util.Optional;

public interface SimBoardRepository {

    Optional<List<SimBoard>> readByUser(String user);

    Optional<List<SimBoard>> readByStatus(SimBoardStatus status);

    Optional<List<SimBoard>> readByUserAndStatus(String user, SimBoardStatus status);

    Optional<SimBoard> readUniqueRecord(String user, String simulator, String scenario);

    void delete(SimBoard simBoard);

    void save(SimBoard simBoard);
}
