package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("park_honor")
public class ParkHonor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parkId;
    private String honorName;
    private String image;
    private String awardYear;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
