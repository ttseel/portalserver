package com.samsung.portalserver.api;

import com.samsung.portalserver.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimulationApiController {

    private final SimulationService simulationService;

    @DeleteMapping("/api/simulation/simboard/stop-sim")
    public Boolean stopSim(@RequestParam("user") String user,
                           @RequestParam("simulator") String simulator,
                           @RequestParam("scenario") String scenario) {

        simulationService.stopSimulation(user, simulator, scenario);

        String res = String.format("user: %s, scenario: %s stopped successfully", user.toUpperCase(), scenario);

        return true;
    }
}
