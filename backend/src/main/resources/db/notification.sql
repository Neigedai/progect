-- ============================================
-- 系统消息通知模块 建表脚本（含优化版）
-- ============================================

USE park_service;
SET NAMES utf8mb4;

-- 消息表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID，0=全员公告',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: business/system',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT COMMENT '消息内容',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读(个人通知): 0-未读 1-已读',
    `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息通知表';

-- 已读记录表（用于系统公告的已读状态）
CREATE TABLE IF NOT EXISTS `notification_read` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `notification_id` BIGINT NOT NULL COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_notif_user` (`notification_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息已读记录表';

-- ============================================
-- 权限数据
-- ============================================

INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `path`, `sort_order`) VALUES
(100, '消息管理',   'admin:notification', 'menu', 1, '/admin/notifications', 10),
(101, '消息-查看',  'notification:view',   'button', 100, '', 1),
(102, '消息-发布',  'notification:add',    'button', 100, '', 2),
(103, '消息-删除',  'notification:delete', 'button', 100, '', 3);

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission` WHERE `id` IN (100, 101, 102, 103);

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `sys_permission` WHERE `id` IN (100, 101, 102, 103);
