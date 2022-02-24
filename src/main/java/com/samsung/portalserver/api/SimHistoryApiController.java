package com.samsung.portalserver.api;

import com.samsung.portalserver.service.SimHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

//@CrossOrigin
@RestController
@RequiredArgsConstructor
public class SimHistoryApiController {

    private final SimHistoryService simHistoryService;

    @GetMapping("/api/simulation/my-simulation/my-history")
    private Optional<List<SimHistoryService.MyHistoryDto>> readMyHistory(
        @RequestParam("user") String user) {
        return simHistoryService.readMyHistory(user.toUpperCase());
    }

    @DeleteMapping("/api/simulation/my-simulation/delete-my-history")
    private Boolean deleteMyHistory(@RequestParam("user") String user,
        @RequestParam("simulator") String simulator, @RequestParam("scenario") String scenario) {
        return true;
    }

    @GetMapping("/api/simulation/my-simulation/download-my-history")
    private void downloadMyHistory(@RequestParam("user") String user,
        @RequestParam("simulator") String simulator, @RequestParam("scenario") String scenario,
        HttpServletResponse response) {

        simHistoryService.downloadMyHistory(user, simulator, scenario, response);
    }
}
