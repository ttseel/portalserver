package com.samsung.portalserver.api;


import com.samsung.portalserver.api.dto.CurrentRunningRecord;
import com.samsung.portalserver.api.dto.ReservedScenarioRecord;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.service.SimBoardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@CrossOrigin // CORS 정책 허용
@RestController
@RequiredArgsConstructor
public class SimBoardApiController {

    private final SimBoardService simBoardService;

    @GetMapping("/api/simulation/simboard/current-running")
    public Optional<List<CurrentRunningRecord>> readCurrentRunning(
        @RequestParam("user") String user) {

        Optional<List<CurrentRunningRecord>> currentRunningDto = simBoardService.readCurrentRunning(
            user.toUpperCase());

        return currentRunningDto;
    }

    @GetMapping("/api/simulation/simboard/reserved-scenario")
    public Optional<List<ReservedScenarioRecord>> readReserved(@RequestParam("user") String user) {

        Optional<List<SimBoard>> simBoardList = simBoardService.readByUserAndStatus(
            user.toUpperCase());

        List<ReservedScenarioRecord> reservedScenarioDto = new ArrayList<>();
        long idx = 1;
        if (simBoardList.isPresent()) {
            for (SimBoard simBoard : simBoardList.get()) {
                reservedScenarioDto.add(new ReservedScenarioRecord(idx, simBoard));
                idx++;
            }
        }

        return Optional.ofNullable(reservedScenarioDto);
    }


}
