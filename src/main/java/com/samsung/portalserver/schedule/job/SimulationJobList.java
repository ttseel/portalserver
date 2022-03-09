package com.samsung.portalserver.schedule.job;

import com.samsung.portalserver.domain.SimBoard;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationJobList implements Job {

    private Process process;
    private String configDirPath;
    private String fslFilePath;
    private String groupDirPath;
    private String fslName;
    private String simulator;
    private String version;
    private String user;
    private Integer reservation_server;
    private Integer execution_server;
    private LocalDateTime reservation_date;
    private LocalDateTime start_date;
    private Map<String, SimulationJob> simulationMap;

    public SimulationJobList() {
    }

    public SimulationJobList(SimBoard simBoard) {
        this.fslName = simBoard.getFsl_name();
        this.simulator = simBoard.getSimulator();
        this.version = simBoard.getVersion();
        this.user = simBoard.getUser();
        this.reservation_server = simBoard.getReservation_server();
        this.execution_server = simBoard.getExecution_server();
        this.reservation_date = simBoard.getReservation_date();
        this.start_date = simBoard.getStart_date();
        simulationMap = new ConcurrentHashMap<>();
    }
}
