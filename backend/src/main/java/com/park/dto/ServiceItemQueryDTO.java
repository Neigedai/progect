package com.park.dto;

import lombok.Data;

@Data
public class ServiceItemQueryDTO {
    private Long categoryId;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
