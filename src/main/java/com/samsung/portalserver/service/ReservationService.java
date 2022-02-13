package com.samsung.portalserver.service;

import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.repository.SimBoardRepository;
import com.samsung.portalserver.repository.SimBoardStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Transactional
public class ReservationService {
    private final SimBoardRepository simBoardRepository;
    private final SimBoardService simBoardService;
    private final FileService fileService = new FileService();

    @Autowired
    public ReservationService(SimBoardRepository simBoardRepository,
                              SimBoardService simBoardService) {
        this.simBoardRepository = simBoardRepository;
        this.simBoardService = simBoardService;
    }

    public void reserveNewSimulation(NewReservationDto newReservationDto) {
        SimBoard simBoard = new SimBoard();
        simBoard.setScenario(newReservationDto.getScenario());
        simBoard.setSimulator(newReservationDto.getSimulator());
        simBoard.setUser(newReservationDto.getUser());
        simBoard.setCurrent_rep(1);
        simBoard.setRequest_rep(99); //scenario config 파일에서 파싱
        simBoard.setStatus(SimBoardStatus.RESERVED.name());
        simBoard.setExecution_server(99);
        simBoard.setReservation_date(LocalDateTime.now());

        simBoardRepository.save(simBoard);
    }

    public void cancelReservation(String user, String simulator, String scenario) {
        SimBoard simBoard = (SimBoard) simBoardService.readUniqueRecord(user, simulator, scenario)
                .orElseThrow(() -> new IllegalStateException("There is no scenario to cancel"));

        simBoardRepository.delete(simBoard);
    }

    public Optional<Map<String, ArrayList<String>>> getSimulatorVersionList(SimulatorCategory simulator) {
        ArrayList<String> amhsSimVersions = new ArrayList<>();
        ArrayList<String> seeflowVersions = new ArrayList<>();
        ArrayList<String> remoteSimVersions = new ArrayList<>();

        Map<String, ArrayList<String>> SimulationVersion = new ConcurrentHashMap<>();

        if(simulator.equals(SimulatorCategory.ALL)) {
            amhsSimVersions = getEachVersionList(SimulatorCategory.AMHS_SIM).get();
            seeflowVersions = getEachVersionList(SimulatorCategory.SeeFlow).get();
            remoteSimVersions = getEachVersionList(SimulatorCategory.REMOTE_SIM).get();
        } else if(simulator.equals(SimulatorCategory.AMHS_SIM)) {
            amhsSimVersions = getEachVersionList(SimulatorCategory.AMHS_SIM).get();
        } else if(simulator.equals(SimulatorCategory.SeeFlow)) {;
            seeflowVersions = getEachVersionList(SimulatorCategory.SeeFlow).get();
        } else if(simulator.equals(SimulatorCategory.REMOTE_SIM)) {
            remoteSimVersions = getEachVersionList(SimulatorCategory.REMOTE_SIM).get();
        } else {
            throw new IllegalArgumentException("UI Simulator 이름과 서버의 시뮬레이터 디렉토리 이름 불일치");
        }

        SimulationVersion.put(SimulatorCategory.AMHS_SIM.toString(), amhsSimVersions);
        SimulationVersion.put(SimulatorCategory.SeeFlow.toString(), seeflowVersions);
        SimulationVersion.put(SimulatorCategory.REMOTE_SIM.toString(), remoteSimVersions);

        return Optional.of(SimulationVersion);
    }

    public Optional<ArrayList<String>> getEachVersionList(SimulatorCategory simulatorName) {
        ArrayList<String> versions = fileService.getFileList(
                FileService.SIMULATOR_DIR_PATH
                        + FileService.DIR_DELIMETER
                        + simulatorName.toString()
        );
        return Optional.ofNullable(versions);
    }

    public boolean saveScenarioFile(String saveDirectoryPath, MultipartFile importFile) {
        return saveScenarioFile(saveDirectoryPath, importFile.getOriginalFilename(), importFile);
    }

    public boolean saveScenarioFile(String saveDirectoryPath, String newName, MultipartFile importFile) {
        try {
            Files.createDirectories(Paths.get(saveDirectoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileService.saveMultipartFileToLocal(saveDirectoryPath, newName, importFile);
        return fileService.aleadyExistFileOrDir(saveDirectoryPath + FileService.DIR_DELIMETER + newName);
    }
}
