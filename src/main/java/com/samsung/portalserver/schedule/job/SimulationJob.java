package com.samsung.portalserver.schedule.job;

import com.samsung.portalserver.domain.SimBoard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class SimulationJob implements Job {

    private Process process;
    private String configDirPath;
    private String scenario;
    private String simulator;
    private String user;
    private Integer current_rep;
    private Integer request_rep;
    private String status;
    private Integer reservation_server;
    private Integer execution_server;
    private LocalDateTime reservation_date;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private String termination_reason;

    public SimulationJob() {}

    public SimulationJob(SimBoard simBoard) {
        this.scenario = simBoard.getScenario();
        this.simulator = simBoard.getSimulator();
        this.user = simBoard.getUser();
        this.current_rep = simBoard.getCurrent_rep();
        this.request_rep = simBoard.getRequest_rep();
        this.status = simBoard.getStatus();
        this.reservation_server = simBoard.getReservation_server();
        this.execution_server = simBoard.getExecution_server();
        this.reservation_date = simBoard.getReservation_date();
        this.start_date = simBoard.getStart_date();
    }
}