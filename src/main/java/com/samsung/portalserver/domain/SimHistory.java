package com.samsung.portalserver.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_history")
@Getter @Setter
public class SimHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "scenario")
    private String scenario;

    @Column(name = "simulator")
    private String simulator;

    @Column(name = "user")
    private String user;

    @Column(name = "replication")
    private Integer replication;

    @Column(name = "termination_reason")
    private String termination_reason;

    @Column(name = "server_no")
    private Integer server_no;

    @Column(name = "reservation_date")
    private LocalDateTime reservation_date;

    @Column(name = "start_date")
    private LocalDateTime start_date;

    @Column(name = "end_date")
    private LocalDateTime end_date;
}
