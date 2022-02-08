package com.samsung.portalserver.api.dto;

import com.samsung.portalserver.domain.SimBoard;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CurrentRunningRecord {
    private Long key;
    private Long no;
    private String scenario;
    private String simulator;
    private List<String> user = new ArrayList<>();
    private LocalDateTime startDate;
    private Integer runningTime;
    private Integer currentRep;
    private Integer requestRep;
    private String status;
    private Integer serverNo;

    public CurrentRunningRecord(Long no, SimBoard simBoard) {
        this.key = no;
        this.no = no;
        this.scenario = simBoard.getScenario();
        this.simulator = simBoard.getSimulator();
        this.user.add(simBoard.getUser());
        this.startDate = simBoard.getStart_date();
        this.runningTime = 10;
        this.currentRep = simBoard.getCurrent_rep();
        this.requestRep = simBoard.getRequest_rep();
        this.status = simBoard.getStatus();
        this.serverNo = simBoard.getServer_no();
    }
}