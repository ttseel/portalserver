package com.samsung.portalserver.api;

import static com.samsung.portalserver.service.FileConstants.CONFIG_DIR_NAME;
import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;
import static com.samsung.portalserver.service.FileConstants.HISTORY_DIR_PATH;

import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.api.dto.StatusAndMessageDto;
import com.samsung.portalserver.service.ReservationService;
import com.samsung.portalserver.service.SimBoardService;
import com.samsung.portalserver.simulation.SimulatorCategory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private SimBoardService simBoardService;

    @PostMapping(value = "/api/simulation/reservation/reserve-new-scenario", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StatusAndMessageDto reserveNewSceanrio(NewReservationDto newReservationDto) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto();
        statusAndMessageDto.setStatus(false);
        statusAndMessageDto.getMessage().add("Reservation request has failed");

        String groupPath = makeGroupPath(newReservationDto);

        boolean isSuccessSaveTheFile = reservationService.saveScenarioFile(groupPath,
            newReservationDto.getFslFile(), newReservationDto.getFssFiles());
        if (isSuccessSaveTheFile) {
            try {
                reservationService.reserveNewSimulation(newReservationDto);
                statusAndMessageDto.setStatus(true);
                statusAndMessageDto.getMessage().clear();
                statusAndMessageDto.getMessage().add("New scenario reservation successful");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return statusAndMessageDto;
    }

    public String makeGroupPath(NewReservationDto newReservationDto) {
        return HISTORY_DIR_PATH + DIR_DELIMETER + newReservationDto.getUser() + DIR_DELIMETER
            + newReservationDto.getSimulator() + DIR_DELIMETER + newReservationDto.getFslName()
            + DIR_DELIMETER + CONFIG_DIR_NAME;
    }

    @DeleteMapping("/api/simulation/simboard/cancel-reservation")
    public Boolean cancelReservation(@RequestParam("user") String user,
        @RequestParam("simulator") String simulator, @RequestParam("scenario") String scenario) {

        reservationService.cancelReservation(user, simulator, scenario);
        String res = String.format("user: %s, scenario: %s cancel successfully", user.toUpperCase(),
            scenario);

        return true;
    }

    @GetMapping("/api/simulation/reservation/version-list")
    public Map<String, List<String>> getVersionList(@RequestParam("simulator") String simulator) {

        Map<String, List<String>> simVersionsList;

        if (checkEachVersion(simulator)) {
            simVersionsList = reservationService.getSimulatorVersionList(
                SimulatorCategory.getByString(simulator));
        } else {
            simVersionsList = reservationService.getSimulatorVersionList(SimulatorCategory.ALL);
        }
        return simVersionsList;
    }

    private boolean checkEachVersion(String simulator) {
        if (SimulatorCategory.getByString(simulator).equals(SimulatorCategory.ALL)
            || SimulatorCategory.getByString(simulator).equals(SimulatorCategory.NOT_FOUND)) {
            return false;
        }

        return true;
    }

    @GetMapping("/api/simulation/reservation/validate-reserve")
    public Optional<StatusAndMessageDto> validatePossibleToNewReservation(
        @RequestParam("user") String user, @RequestParam("simulator") String simulator,
        @RequestParam("scenarioList") List<String> scenarioList) {

        Optional<StatusAndMessageDto> statusAndMessageDto = simBoardService.validatePossibleToReserveScenario(
            user.toUpperCase(), simulator, scenarioList);

        return statusAndMessageDto;
    }

}
