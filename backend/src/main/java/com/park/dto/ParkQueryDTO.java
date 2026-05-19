package com.park.dto;

import lombok.Data;

@Data
public class ParkQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String parkName;
    private Integer status;
}
