package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("park_info")
public class ParkInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String parkName;
    private String logo;
    private Integer isDefault;
    private String address;
    private String description;
    private String planImage;
    private String planDescription;
    private String transportInfo;
    private Integer status;
    private Integer sortOrder;
    private String videoUrl;
    private String createBy;
    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
