package com.park.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_article")
public class CmsArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String type;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private String source;
    private Integer status;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private Integer sortOrder;
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String updateBy;

    @TableLogic
    private Integer isDeleted;
}
