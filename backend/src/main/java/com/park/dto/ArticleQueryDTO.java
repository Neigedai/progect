package com.park.dto;

import lombok.Data;

@Data
public class ArticleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String title;
    private String type;
    private Integer status;
}
