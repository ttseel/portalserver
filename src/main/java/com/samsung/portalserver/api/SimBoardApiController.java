package com.samsung.portalserver.api;


import com.samsung.portalserver.api.dto.CurrentRunningRecord;
import com.samsung.portalserver.api.dto.ReservedScenarioRecord;
import com.samsung.portalserver.api.dto.StatusAndMessageDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.service.FileService;
import com.samsung.portalserver.service.SimBoardService;

import com.samsung.portalserver.service.SimReservationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@CrossOrigin // CORS 정책 허용
@RestController
@RequiredArgsConstructor
public class SimBoardApiController {

    private final SimBoardService simBoardService;
    private final SimReservationService simReservationService;

    @GetMapping("/api/simulation/simboard/current-running")
    private Optional<List<CurrentRunningRecord>> readCurrentRunning(@RequestParam("user") String user) {
        Optional<List<CurrentRunningRecord>> currentRunningDto = simBoardService.readCurrentRunning(user.toUpperCase());

        return currentRunningDto;
    }

    @GetMapping("/api/simulation/simboard/reserved-scenario")
    private Optional<List<ReservedScenarioRecord>> readReservedScenario(@RequestParam("user") String user) {
        Optional<List<SimBoard>> simBoardList = simBoardService.readByUserAndStatus(user.toUpperCase());

        List<ReservedScenarioRecord> reservedScenarioDto = new ArrayList<>();
        long idx = 1;
        if (simBoardList.isPresent()){
            for (SimBoard simBoard : simBoardList.get()) {
                reservedScenarioDto.add(new ReservedScenarioRecord(idx, simBoard));
                idx++;
            }
        }

        return Optional.ofNullable(reservedScenarioDto);
    }

    @DeleteMapping("/api/simulation/simboard/stop-sim")
    public Boolean stopSim(@RequestParam("user") String user,
                           @RequestParam("simulator") String simulator,
                           @RequestParam("scenario") String scenario) {

//        simBoardService.stopSimulation(user, simulator, scenario);

        String res = String.format("user: %s, scenario: %s stopped successfully", user.toUpperCase(), scenario);

        return true;
    }

    @DeleteMapping("/api/simulation/simboard/cancel-reservation")
    public Boolean cancelReservation(@RequestParam("user") String user,
                                     @RequestParam("simulator") String simulator,
                                     @RequestParam("scenario") String scenario) {

        simReservationService.cancelReservation(user, simulator, scenario);
        String res = String.format("user: %s, scenario: %s cancel successfully", user.toUpperCase(), scenario);

        return true;
    }

    @GetMapping("/api/simulation/reservation/version-list")
    public Optional<Map<String, ArrayList<String>>> versionList(@RequestParam("simulator") String simulator) {
        Optional<Map<String, ArrayList<String>>> simVersionsList = Optional.empty();
        if (simulator.equals(SimulatorCategory.ALL.name())){
            simVersionsList = simReservationService.getSimulatorVersionList(SimulatorCategory.ALL);
        } else {
            simVersionsList = simReservationService.getSimulatorVersionList(SimulatorCategory.getCategoryByString(simulator));
        }
        return simVersionsList;
    }

    @PostMapping(
            value = "/api/simulation/reservation/reserve-new-scenario",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public StatusAndMessageDto reserveNewSceanrio(NewReservationDto newReservationDto) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto();
        statusAndMessageDto.setStatus(false);
        statusAndMessageDto.getMessage().add("Reservation request has failed");

        String saveDirectoryPath = FileService.RESERVED_SCENARIO_DIR_PATH;
        String newName = FileService.DIR_DELIMETER + newReservationDto.getUser()
                + FileService.NAME_DELIMETER + newReservationDto.getSimulator()
                + FileService.NAME_DELIMETER + newReservationDto.getScenario()
                + FileService.NAME_DELIMETER + newReservationDto.getImportFile().getOriginalFilename();

        boolean isSuccessSaveTheFile = simReservationService.saveScenarioFile(saveDirectoryPath, newName, newReservationDto.getImportFile());
        if (isSuccessSaveTheFile) {
            try {
                simBoardService.reserveNewSimulation(newReservationDto);
                statusAndMessageDto.setStatus(true);
                statusAndMessageDto.getMessage().clear();
                statusAndMessageDto.getMessage().add("New scenario reservation successful");
            } catch (Exception e) {
                System.out.println(e);
            }

            return statusAndMessageDto;
        }
        return statusAndMessageDto;
    }

    @Data
    public static class NewReservationDto {
        private String user;
        private String simulator;
        private String scenario;
        private MultipartFile importFile;
    }

    static class NewReservationResponseDto {
        private String user;
        private String simulator;
    }

    @GetMapping("/api/simulation/reservation/validate-reserve")
    public Optional<StatusAndMessageDto> validatePossibleToNewReservation(@RequestParam("user") String user,
                                                                                          @RequestParam("simulator") String simulator,
                                                                                          @RequestParam("scenario") String scenario) {

        Optional<StatusAndMessageDto> statusAndMessageDto
                = simBoardService.validatePossibleToReserveScenario(user.toUpperCase(), simulator, scenario);

        return statusAndMessageDto;
    }
}
