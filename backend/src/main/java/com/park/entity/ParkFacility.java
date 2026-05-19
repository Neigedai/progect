package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("park_facility")
public class ParkFacility {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parkId;
    private String facilityName;
    private String icon;
    private String description;
    private String image;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
