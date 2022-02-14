package com.samsung.portalserver.api.dto;

import com.samsung.portalserver.domain.SimBoard;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservedScenarioRecord {
    private Long key;
    private Long no;
    private String scenario;
    private String simulator;
    private String version;
    private List<String> user = new ArrayList<>();
    private LocalDateTime reservationDate;
    private Integer reservationServer;

    public ReservedScenarioRecord(Long no, SimBoard simBoard) {
        this.key = no;
        this.no = no;
        this.scenario = simBoard.getScenario();
        this.simulator = simBoard.getSimulator();
        this.version = simBoard.getVersion();
        this.user.add(simBoard.getUser());
        this.reservationDate = simBoard.getReservation_date();
        this.reservationServer = simBoard.getReservation_server();
    }
}
