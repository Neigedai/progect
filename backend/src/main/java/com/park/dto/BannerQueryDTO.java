package com.park.dto;

import lombok.Data;

@Data
public class BannerQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String title;
    private Integer status;
}
