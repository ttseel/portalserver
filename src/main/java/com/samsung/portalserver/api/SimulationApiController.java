package com.samsung.portalserver.api;

import com.samsung.portalserver.api.dto.UniqueSimulationRecordDto;
import com.samsung.portalserver.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimulationApiController {

    private final SimulationService simulationService;

    @DeleteMapping("/api/simulation/simboard/stop-sim")
    public Boolean stopSim(UniqueSimulationRecordDto dto) {

        simulationService.stopSimulation(dto);

        String res = String.format("user: %s, scenario: %s stopped successfully",
            dto.getUser().toUpperCase(), dto.getScenario());

        return true;
    }
}
