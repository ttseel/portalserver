package com.samsung.portalserver.service;

import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.repository.SimBoardRepository;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.JobSchedulerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
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
        for(String fssName : newReservationDto.getFssNameList()) {
            SimBoard simBoard = new SimBoard();
            simBoard.setFsl_name(newReservationDto.getFslName());
            simBoard.setScenario(fssName);
            simBoard.setSimulator(newReservationDto.getSimulator());
            simBoard.setVersion(newReservationDto.getVersion());
            simBoard.setUser(newReservationDto.getUser());
            simBoard.setCurrent_rep(1);
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

    public Optional<Map<String, ArrayList<String>>> getSimulatorVersionList(SimulatorCategory simulator) {
        ArrayList<String> mcpsimVersions = new ArrayList<>();
        ArrayList<String> ocs3simVersions = new ArrayList<>();
        ArrayList<String> ocs4simVersions = new ArrayList<>();
        ArrayList<String> seeflowVersions = new ArrayList<>();
        ArrayList<String> remoteSimVersions = new ArrayList<>();

        Map<String, ArrayList<String>> SimulationVersion = new ConcurrentHashMap<>();

        if(simulator.equals(SimulatorCategory.ALL)) {
            mcpsimVersions = getEachVersionList(SimulatorCategory.MCPSIM).get();
            ocs3simVersions = getEachVersionList(SimulatorCategory.OCS3SIM).get();
            ocs4simVersions = getEachVersionList(SimulatorCategory.OCS4SIM).get();
            seeflowVersions = getEachVersionList(SimulatorCategory.SeeFlow).get();
            remoteSimVersions = getEachVersionList(SimulatorCategory.REMOTE_SIM).get();
        } else if(simulator.equals(SimulatorCategory.MCPSIM)) {
            mcpsimVersions = getEachVersionList(SimulatorCategory.MCPSIM).get();
        } else if(simulator.equals(SimulatorCategory.OCS3SIM)) {
            ocs3simVersions = getEachVersionList(SimulatorCategory.OCS3SIM).get();
        } else if(simulator.equals(SimulatorCategory.OCS4SIM)) {
            ocs4simVersions = getEachVersionList(SimulatorCategory.OCS4SIM).get();
        } else if(simulator.equals(SimulatorCategory.SeeFlow)) {;
            seeflowVersions = getEachVersionList(SimulatorCategory.SeeFlow).get();
        } else if(simulator.equals(SimulatorCategory.REMOTE_SIM)) {
            remoteSimVersions = getEachVersionList(SimulatorCategory.REMOTE_SIM).get();
        } else {
            throw new IllegalArgumentException("UI Simulator 이름과 서버의 시뮬레이터 디렉토리 이름 불일치");
        }

        SimulationVersion.put(SimulatorCategory.MCPSIM.toString(), mcpsimVersions);
        SimulationVersion.put(SimulatorCategory.OCS3SIM.toString(), ocs3simVersions);
        SimulationVersion.put(SimulatorCategory.OCS4SIM.toString(), ocs4simVersions);
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

        versions.sort(Comparator.reverseOrder());
        return Optional.ofNullable(versions);
    }

    public boolean saveScenarioFile(String saveDirectoryPath, MultipartFile fslFile, List<MultipartFile> fssFiles) {
        try {
            Files.createDirectories(Paths.get(saveDirectoryPath));
            fileService.saveMultipartFileToLocal(saveDirectoryPath, fslFile, fssFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileService.aleadyExistFileOrDir(saveDirectoryPath + FileService.DIR_DELIMETER + fslFile.getOriginalFilename());
    }
}
