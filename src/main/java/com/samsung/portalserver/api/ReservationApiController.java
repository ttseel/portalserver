package com.samsung.portalserver.api;

import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.api.dto.StatusAndMessageDto;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.service.FileService;
import com.samsung.portalserver.service.ReservationService;
import com.samsung.portalserver.service.SimBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {
    private final ReservationService reservationService;
    private final SimBoardService simBoardService;

    @PostMapping(
            value = "/api/simulation/reservation/reserve-new-scenario",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public StatusAndMessageDto reserveNewSceanrio(NewReservationDto newReservationDto) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto();
        statusAndMessageDto.setStatus(false);
        statusAndMessageDto.getMessage().add("Reservation request has failed");

        String saveDirectoryPath = FileService.HISTORY_DIR_PATH +
                FileService.DIR_DELIMETER + newReservationDto.getUser()
                + FileService.DIR_DELIMETER + newReservationDto.getSimulator()
                + FileService.DIR_DELIMETER + newReservationDto.getFslName()
                + FileService.DIR_DELIMETER + FileService.CONFIG_DIR_NAME;

        boolean isSuccessSaveTheFile = reservationService.saveScenarioFile(saveDirectoryPath, newReservationDto.getFslFile(), newReservationDto.getFssFiles());
        if (isSuccessSaveTheFile) {
            try {
                reservationService.reserveNewSimulation(newReservationDto);
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

    @DeleteMapping("/api/simulation/simboard/cancel-reservation")
    public Boolean cancelReservation(@RequestParam("user") String user,
                                     @RequestParam("simulator") String simulator,
                                     @RequestParam("scenario") String scenario) {

        reservationService.cancelReservation(user, simulator, scenario);
        String res = String.format("user: %s, scenario: %s cancel successfully", user.toUpperCase(), scenario);

        return true;
    }

    @GetMapping("/api/simulation/reservation/version-list")
    public Optional<Map<String, ArrayList<String>>> versionList(@RequestParam("simulator") String simulator) {
        Optional<Map<String, ArrayList<String>>> simVersionsList = Optional.empty();
        if (simulator.equals(SimulatorCategory.ALL.name())){
            simVersionsList = reservationService.getSimulatorVersionList(SimulatorCategory.ALL);
        } else {
            simVersionsList = reservationService.getSimulatorVersionList(SimulatorCategory.getCategoryByString(simulator));
        }
        return simVersionsList;
    }

    @GetMapping("/api/simulation/reservation/validate-reserve")
    public Optional<StatusAndMessageDto> validatePossibleToNewReservation(@RequestParam("user") String user,
                                                                          @RequestParam("simulator") String simulator,
                                                                          @RequestParam("scenarioList") List<String> scenarioList) {

        Optional<StatusAndMessageDto> statusAndMessageDto
                = simBoardService.validatePossibleToReserveScenario(user.toUpperCase(), simulator, scenarioList);

        return statusAndMessageDto;
    }
}
