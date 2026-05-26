package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_application")
public class ServiceApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long serviceId;
    private String serviceName;
    private String contactName;
    private String contactPhone;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
