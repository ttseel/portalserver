package com.samsung.portalserver.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_board")
@Getter
@Setter
public class SimBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "fsl_name")
    private String fsl_name;

    @Column(name = "scenario")
    private String scenario;

    @Column(name = "simulator")
    private String simulator;

    @Column(name = "version")
    private String version;

    @Column(name = "user")
    private String user;

    @Column(name = "current_rep")
    private Integer current_rep;

    @Column(name = "request_rep")
    private Integer request_rep;

    @Column(name = "status")
    private String status;

    @Column(name = "reservation_server")
    private Integer reservation_server;

    @Column(name = "execution_server")
    private Integer execution_server;

    @Column(name = "reservation_date")
    private LocalDateTime reservation_date;

    @Column(name = "start_date")
    private LocalDateTime start_date;
}
