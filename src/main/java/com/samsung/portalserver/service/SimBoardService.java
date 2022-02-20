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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    public Optional<StatusAndMessageDto> validatePossibleToReserveScenario(String user, String simulator, List<String> scenarioList) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto(true);

        Optional<List<SimBoard>> records = simBoardRepository.readByUserAndSimulator(user, simulator);

        if (records.isPresent() && records.get().size() > 0) {
            Map<String, Boolean> scenarioMap = new ConcurrentHashMap<>();
            for (SimBoard simBoard : records.get()) {
                scenarioMap.put(simBoard.getScenario(), true);
            }

            StringBuilder validationMessage = new StringBuilder("Some scenarios already exist in the Reserved or History: ");
            for (int i = 0; i < scenarioList.size(); i++) {
                if (scenarioMap.containsKey(scenarioList.get(i))) {
                    statusAndMessageDto.setStatus(false);
                    validationMessage.append(scenarioList.get(i));
                    if (!isLastScenario(i, scenarioList.size()-1)) {
                        validationMessage.append(", ");
                    }
                }
            }

            if (statusAndMessageDto.getStatus()) {
                statusAndMessageDto.getMessage().add("You can reserve scenario now");
            } else {
                statusAndMessageDto.getMessage().add(validationMessage.toString());
            }
        }
        return Optional.of(statusAndMessageDto);
    }

    private boolean isLastScenario(int i, int lastIdx) {
        return i == lastIdx;
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

    public void updateCurrentRep(Long simBoardPKNo, int currentRep) {
        Optional<SimBoard> simBoard = simBoardRepository.readUniqueRecord(simBoardPKNo);
        simBoard.ifPresent(sb -> sb.setCurrent_rep(currentRep));
        // 이후 Commit 되는 시점에서 JPA가 Entity의 변화를 확인하고 update 쿼리를 DB에 날려준 뒤 Transaction이 종료됨
    }

    public void updateStatus(Long simBoardPKNo, String status) {
        Optional<SimBoard> simBoard = simBoardRepository.readUniqueRecord(simBoardPKNo);
        simBoard.ifPresent(sb -> sb.setStatus(status));
    }

    public void commitSimBoard() {
        simBoardRepository.commit();
    }
}
