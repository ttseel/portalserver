package com.samsung.portalserver.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_board")
@Getter @Setter
public class SimBoard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "scenario")
    private String scenario;

    @Column(name = "simulator")
    private String simulator;

    @Column(name = "user")
    private String user;

    @Column(name = "current_rep")
    private Integer current_rep;

    @Column(name = "request_rep")
    private Integer request_rep;

    @Column(name = "status")
    private String status;

    @Column(name = "server_no")
    private Integer server_no;

    @Column(name = "reservation_date")
    private LocalDateTime reservation_date;

    @Column(name = "start_date")
    private LocalDateTime start_date;
}
