-- 入驻申请表
CREATE TABLE IF NOT EXISTS `residency_application` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `contact_name` VARCHAR(50) DEFAULT '' COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT '' COMMENT '联系电话',
    `area` VARCHAR(100) NOT NULL COMMENT '面积需求',
    `industry_type` VARCHAR(100) NOT NULL COMMENT '行业类型',
    `expected_entry_date` DATE DEFAULT NULL COMMENT '预计入驻时间',
    `additional_info` TEXT COMMENT '其他信息',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/approved/rejected',
    `reviewed_by` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `review_comment` VARCHAR(500) DEFAULT '' COMMENT '审批意见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入驻申请表';
