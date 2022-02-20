package com.samsung.portalserver.api.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StatusAndMessageDto {
    Boolean Status = true;
    List<String> Message = new ArrayList<>();

    public StatusAndMessageDto() {}

    public StatusAndMessageDto(Boolean status) {
        Status = status;
    }

    public StatusAndMessageDto(Boolean status, List<String> message) {
        Status = status;
        Message = message;
    }
}


