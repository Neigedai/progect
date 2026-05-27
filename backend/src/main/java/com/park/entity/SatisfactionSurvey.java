package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("satisfaction_survey")
public class SatisfactionSurvey {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String surveyCode;
    private Long userId;
    private String status;

    private String companyName;
    private String industry;
    private Integer employeeCount;
    private String contactName;
    private String contactPhone;

    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;
    private Integer q6;
    private Integer q7;
    private Integer q8;
    private Integer q9;
    private Integer q10;
    private String q11;

    private LocalDateTime assignedAt;
    private LocalDateTime submittedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
