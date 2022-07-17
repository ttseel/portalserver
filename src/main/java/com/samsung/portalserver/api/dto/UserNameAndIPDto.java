package com.samsung.portalserver.api.dto;

import lombok.Data;

@Data
public class UserNameAndIPDto {

    String userName;
    String ip;

    public UserNameAndIPDto(String ip) {
        this.ip = ip;
    }
}
