package com.samsung.portalserver.api.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class NewReservationDto {

    private String user;
    private String simulator;
    private String version;
    private String fslName;
    private List<String> fssNameList;
    private MultipartFile fslFile;
    private List<MultipartFile> fssFiles;
}
