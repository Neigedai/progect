package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_provider")
public class ServiceProvider {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String providerName;
    private String contactPerson;
    private String contactPhone;
    private String qualifications;
    private String logo;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
