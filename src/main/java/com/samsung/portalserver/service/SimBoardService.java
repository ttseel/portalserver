package com.samsung.portalserver.service;

import com.samsung.portalserver.api.dto.CurrentRunningRecord;
import com.samsung.portalserver.api.dto.StatusAndMessageDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimHistory;
import com.samsung.portalserver.repository.SimBoardRepository;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.repository.SimHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SimBoardService {
    private final SimBoardRepository simBoardRepository;
    private final SimHistoryRepository simHistoryRepository;

    @Autowired
    public SimBoardService(SimBoardRepository simBoardRepository,
                           SimHistoryRepository simHistoryRepository) {
        this.simBoardRepository = simBoardRepository;
        this.simHistoryRepository = simHistoryRepository;
    }

    public Optional<List<CurrentRunningRecord>> readCurrentRunning(String user) {
        Optional<List<SimBoard>> simBoardList;
        if (user.equals("ALL")) {
            simBoardList = simBoardRepository.readByStatus(SimBoardStatus.RUNNING);
        } else {
            simBoardList = simBoardRepository.readByUserAndStatus(user, SimBoardStatus.RUNNING);
        }

        List<CurrentRunningRecord> currentRunningDto = new ArrayList<>();
        long idx = 1;
        if (simBoardList.isPresent()){
            for (SimBoard simBoard : simBoardList.get()) {
                currentRunningDto.add(new CurrentRunningRecord(idx, simBoard));
                idx++;
            }
        }

        return Optional.of(currentRunningDto);
    }

    public Optional<List<SimBoard>> readByUserAndStatus(String user) {
        Optional<List<SimBoard>> simBoardList;
        if (user.equals("ALL")) {
            simBoardList = simBoardRepository.readByStatus(SimBoardStatus.RESERVED);
        } else {
            simBoardList = simBoardRepository.readByUserAndStatus(user, SimBoardStatus.RESERVED);
        }
        return simBoardList;
    }

    public Optional<SimBoard> readUniqueRecord(long no) {
        return simBoardRepository.readUniqueRecord(no);
    }

    public Optional<SimBoard> readUniqueRecord(String user, String simulator, String scenario) {
        return simBoardRepository.readUniqueRecord(user, simulator, scenario);
    }

    public Optional<StatusAndMessageDto> validatePossibleToReserveScenario(String user, String simulator, String scenario) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto();

        if (alreadyExistInSimBoard(user, simulator, scenario)) {
            statusAndMessageDto.setStatus(false);
            statusAndMessageDto.getMessage().add("This scenario is already on the Reserved table");
            return Optional.of(statusAndMessageDto); // 검증 실패하면 바로 return: 이미 실패했으므로 후속 조건 검증으로 인한 부하를 피한다.
        }
        if (alreadyExistInSimHistory(user, simulator, scenario)) {
            statusAndMessageDto.setStatus(false);
            statusAndMessageDto.getMessage().add("This scenario is already on the My History table");

            return Optional.of(statusAndMessageDto);
        }

        statusAndMessageDto.setStatus(true);
        statusAndMessageDto.getMessage().add("You can reserve scenario now");
        return Optional.of(statusAndMessageDto);
    }

    private boolean alreadyExistInSimBoard(String user, String simulator, String scenario) {
        return readUniqueRecord(user, simulator, scenario).isPresent();
    }

    private boolean alreadyExistInSimHistory(String user, String simulator, String scenario) {
        boolean exist = false;
        Optional<List<SimHistory>> simHistories = simHistoryRepository.readByUserAndSimulatorAndScenario(user, simulator, scenario);
        if (simHistories.isPresent() && simHistories.get().size() > 0) {
            exist = true;
        }
        return exist;
    }

    public long findNewSimulation(int executionServer) {
        return simBoardRepository.findNewSim(executionServer);
    }
}
