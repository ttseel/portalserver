package com.samsung.portalserver.api;

import com.samsung.portalserver.api.dto.StatusAndMessageDto;
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
    public StatusAndMessageDto stopSim(UniqueSimulationRecordDto dto) {
        StatusAndMessageDto statusAndMessageDto = new StatusAndMessageDto();

        boolean isStopped = simulationService.stopSimulation(dto);

        if (isStopped) {
            statusAndMessageDto.getMessage().add("Scenario Group has been stopped successfully");
            statusAndMessageDto.setStatus(true);
        } else {
            statusAndMessageDto.getMessage().add("Stop request failed");
            statusAndMessageDto.setStatus(false);
        }

        return statusAndMessageDto;
    }
}
