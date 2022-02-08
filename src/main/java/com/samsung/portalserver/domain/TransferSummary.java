package com.samsung.portalserver.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "transfer_summary")
@Getter @Setter
public class TransferSummary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "site")
    private String site;

    @Column(name = "line")
    private String line;

    @Column(name = "from_type")
    private String from_type;

    @Column(name = "to_type")
    private String to_type;

    @Column(name = "category")
    private String category;

    @Column(name = "count")
    private Integer count;

    @Column(name = "yyyymmdd")
    private String yyyymmdd;

}
