package com.park.dto;

import lombok.Data;

@Data
public class ServiceItemVO {
    private Long id;
    private Long categoryId;
    private String serviceName;
    private String summary;
    private String applicableEnterprise;
    private Integer status;
    private Integer sortOrder;
}
