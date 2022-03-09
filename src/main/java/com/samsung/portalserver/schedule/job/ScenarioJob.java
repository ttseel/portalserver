package com.samsung.portalserver.schedule.job;

import com.samsung.portalserver.domain.SimBoard;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioJob {

    private String fssFilePath;
    private String trGenFilePath;
    private Long simBoardPKNo;
    private String fslName;
    private String scenario;
    private String simulator;
    private String version;
    private String user;
    private Integer current_rep;
    private Integer request_rep;
    private String status;
    private LocalDateTime end_date;
    private String termination_reason;

    public ScenarioJob(SimBoard simBoard) {
        this.fssFilePath = "";
        this.trGenFilePath = "";
        this.simBoardPKNo = simBoard.getNo();
        this.fslName = simBoard.getFsl_name();
        this.scenario = simBoard.getScenario();
        this.simulator = simBoard.getSimulator();
        this.version = simBoard.getVersion();
        this.user = simBoard.getUser();
        this.current_rep = simBoard.getCurrent_rep();
        this.request_rep = simBoard.getRequest_rep();
        this.status = simBoard.getStatus();
        this.end_date = LocalDateTime.MAX;
        termination_reason = "";
    }
}
