package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("enterprise_auth")
public class EnterpriseAuth {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String companyName;
    private String creditCode;
    private String licenseUrl;
    private String legalPersonIdUrl;
    private String legalPersonIdBackUrl;
    private Boolean legalFaceVerified;
    private String authStatus;
    private String reviewComment;
    private Long reviewedBy;
    private String tag;
    private LocalDateTime authSubmittedAt;
    private LocalDateTime authReviewedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private String userNickname;
    @TableField(exist = false)
    private String userPhone;
}
