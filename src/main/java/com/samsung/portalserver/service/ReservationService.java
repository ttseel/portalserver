package com.samsung.portalserver.service;

import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;
import static com.samsung.portalserver.service.FileConstants.SIMULATOR_DIR_PATH;

import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.repository.SimBoardRepository;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.JobSchedulerImpl;
import com.samsung.portalserver.simulation.SimulatorCategory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


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
        for (String fssName : newReservationDto.getFssNameList()) {
            SimBoard simBoard = new SimBoard();
            simBoard.setFsl_name(newReservationDto.getFslName());
            simBoard.setScenario(fssName);
            simBoard.setSimulator(newReservationDto.getSimulator());
            simBoard.setVersion(newReservationDto.getVersion());
            simBoard.setUser(newReservationDto.getUser());
            simBoard.setCurrent_rep(0);
            simBoard.setRequest_rep(99); //scenario config 파일에서 파싱
            simBoard.setStatus(SimBoardStatus.RESERVED.name());
            simBoard.setReservation_server(JobSchedulerImpl.CURRENT_SERVER_TEMP);
            simBoard.setExecution_server(0);
            simBoard.setReservation_date(LocalDateTime.now());

            simBoardRepository.save(simBoard);
        }
    }

    public void cancelReservation(String user, String simulator, String scenario) {
        SimBoard simBoard = (SimBoard) simBoardService.readUniqueRecord(user, simulator, scenario)
            .orElseThrow(() -> new IllegalStateException("There is no scenario to cancel"));

        simBoardRepository.delete(simBoard);
    }

    public Map<String, List<String>> getSimulatorVersionList(SimulatorCategory simulator) {

        Map<String, List<String>> SimulationVersion = new HashMap<>();

        if (simulator.equals(SimulatorCategory.NOT_FOUND)) {
            throw new IllegalArgumentException("UI Simulator 이름과 서버의 시뮬레이터 디렉토리 이름 불일치");
        }

        if (simulator.equals(SimulatorCategory.ALL)) {
            SimulationVersion = getAllVersionList();
        } else {
            SimulationVersion.put(simulator.name(), getEachVersionList(simulator));
        }

        return SimulationVersion;
    }

    public ArrayList<String> getEachVersionList(SimulatorCategory simulatorName) {
        ArrayList<String> versions = fileService.getFileList(
            SIMULATOR_DIR_PATH + DIR_DELIMETER + simulatorName.toString());

        versions.sort(Comparator.reverseOrder());
        return versions;
    }

    public Map<String, List<String>> getAllVersionList() {
        Map<String, List<String>> allVersionList = new HashMap<>();

        for (SimulatorCategory value : SimulatorCategory.values()) {
            if (value.equals(SimulatorCategory.NOT_FOUND)) {
                continue;
            }
            allVersionList.put(value.name(), getEachVersionList(value));
        }
        return allVersionList;
    }

    public boolean saveScenarioFile(String saveDirectoryPath, MultipartFile fslFile,
        List<MultipartFile> fssFiles) {
        try {
            Files.createDirectories(Paths.get(saveDirectoryPath));
            fileService.saveMultipartFileToLocal(saveDirectoryPath, fslFile, fssFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileService.aleadyExistFileOrDir(
            saveDirectoryPath + DIR_DELIMETER + fslFile.getOriginalFilename());
    }
}
