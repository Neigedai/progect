package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("residency_application")
public class ResidencyApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String location;
    private String companyName;
    private String businessLicenseUrl;
    private String legalPersonName;
    private String legalPersonPhone;
    private String legalPersonIdFront;
    private String legalPersonIdBack;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Integer enterpriseType;
    private String enterpriseTrack;
    private String status;
    private Long reviewedBy;
    private String reviewComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
