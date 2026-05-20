-- 悬浮菜单项表
CREATE TABLE IF NOT EXISTS `floating_menu_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(50) NOT NULL COMMENT '按钮标题',
    `icon` VARCHAR(100) DEFAULT '' COMMENT '图标(Element Plus图标名)',
    `image_url` VARCHAR(500) NOT NULL COMMENT '弹出图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='悬浮菜单项';

-- 示例数据
INSERT INTO `floating_menu_item` (`title`, `icon`, `image_url`, `sort_order`) VALUES
('服务指南', 'Guide', '/uploads/floating/guide.png', 1),
('办事流程', 'List', '/uploads/floating/flow.png', 2),
('联系我们', 'Phone', '/uploads/floating/contact.png', 3),
('常见问题', 'QuestionFilled', '/uploads/floating/faq.png', 4);
