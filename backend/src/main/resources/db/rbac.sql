-- ============================================
-- RBAC 权限系统建表脚本
-- ============================================

-- 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `permission_type` VARCHAR(20) NOT NULL DEFAULT 'button' COMMENT '类型: menu/button/api',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID(菜单层级)',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由路径',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 新增角色
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, '超级管理员', 'super_admin', '拥有所有权限'),
(2, '审批人员', 'reviewer', '拥有审批相关权限');

-- 更新已有角色编码
UPDATE `sys_role` SET `role_code` = 'super_admin', `role_name` = '超级管理员' WHERE `id` = 1;
UPDATE `sys_role` SET `role_code` = 'reviewer', `role_name` = '审批人员' WHERE `id` = 2;
UPDATE `sys_role` SET `role_code` = 'admin', `role_name` = '管理员' WHERE `id` = 3;
UPDATE `sys_role` SET `role_code` = 'user', `role_name` = '普通用户' WHERE `id` = 4;

-- ============================================
-- 权限数据
-- ============================================

-- 菜单权限 (parent_id=0)
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `path`, `sort_order`) VALUES
(1,  '内容管理',   'admin',          'menu', 0, '',             1),
(2,  '园区管理',   'admin:park',     'menu', 1, '/admin/parks',    1),
(3,  '资讯管理',   'admin:article',  'menu', 1, '/admin/articles',  2),
(4,  '轮播图管理', 'admin:banner',   'menu', 1, '/admin/banners',   3),
(5,  '服务管理',   'admin:service',  'menu', 1, '/admin/services',  4),
(6,  '用户管理',   'admin:user',     'menu', 1, '/admin/users',     5),
(7,  '入驻审批',   'admin:residency','menu', 1, '/admin/residency', 6),
(8,  '悬浮菜单',   'admin:floating', 'menu', 1, '/admin/floating-menu', 7),
(9,  '权限管理',   'admin:perm',     'menu', 1, '',               8),
(10, '角色管理',   'admin:role',     'menu', 9, '/admin/roles',     1),
(11, '企业认证',   'admin:enterprise','menu', 1, '/admin/enterprise', 9);

-- 操作权限 - 园区
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(20, '园区-查看',   'park:view',   'button', 2, 1),
(21, '园区-新增',   'park:add',    'button', 2, 2),
(22, '园区-编辑',   'park:edit',   'button', 2, 3),
(23, '园区-删除',   'park:delete', 'button', 2, 4),
(24, '园区-启停',   'park:status', 'button', 2, 5);

-- 操作权限 - 资讯
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(30, '资讯-查看',   'article:view',   'button', 3, 1),
(31, '资讯-新增',   'article:add',    'button', 3, 2),
(32, '资讯-编辑',   'article:edit',   'button', 3, 3),
(33, '资讯-删除',   'article:delete', 'button', 3, 4),
(34, '资讯-发布',   'article:status', 'button', 3, 5);

-- 操作权限 - 轮播图
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(40, '轮播图-查看',   'banner:view',   'button', 4, 1),
(41, '轮播图-新增',   'banner:add',    'button', 4, 2),
(42, '轮播图-编辑',   'banner:edit',   'button', 4, 3),
(43, '轮播图-删除',   'banner:delete', 'button', 4, 4),
(44, '轮播图-启停',   'banner:status', 'button', 4, 5);

-- 操作权限 - 服务
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(50, '服务-查看',   'service:view',   'button', 5, 1),
(51, '服务-新增',   'service:add',    'button', 5, 2),
(52, '服务-编辑',   'service:edit',   'button', 5, 3),
(53, '服务-删除',   'service:delete', 'button', 5, 4),
(54, '服务-上下架', 'service:status', 'button', 5, 5);

-- 操作权限 - 用户
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(60, '用户-查看',   'user:view',   'button', 6, 1),
(61, '用户-新增',   'user:add',    'button', 6, 2),
(62, '用户-编辑',   'user:edit',   'button', 6, 3),
(63, '用户-删除',   'user:delete', 'button', 6, 4),
(64, '用户-封禁',   'user:status', 'button', 6, 5);

-- 操作权限 - 入驻审批
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(70, '入驻-查看',   'residency:view',   'button', 7, 1),
(71, '入驻-审批',   'residency:approve','button', 7, 2),
(72, '入驻-编辑',   'residency:edit',   'button', 7, 3);

-- 操作权限 - 悬浮菜单
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(80, '悬浮菜单-查看', 'floating:view',   'button', 8, 1),
(81, '悬浮菜单-新增', 'floating:add',    'button', 8, 2),
(82, '悬浮菜单-编辑', 'floating:edit',   'button', 8, 3),
(83, '悬浮菜单-删除', 'floating:delete', 'button', 8, 4);

-- 操作权限 - 角色
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(90, '角色-查看',   'role:view',       'button', 10, 1),
(91, '角色-新增',   'role:add',        'button', 10, 2),
(92, '角色-编辑',   'role:edit',       'button', 10, 3),
(93, '角色-删除',   'role:delete',     'button', 10, 4),
(94, '角色-分配权限','role:permission', 'button', 10, 5);

-- 操作权限 - 企业认证
INSERT IGNORE INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `sort_order`) VALUES
(95, '企业认证-查看',   'enterprise:view',    'button', 11, 1),
(96, '企业认证-审批',   'enterprise:approve', 'button', 11, 2);

-- ============================================
-- 分配角色权限
-- ============================================

-- 超级管理员(role_id=1): 所有权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- 管理员(role_id=3): 除权限管理外的所有权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `sys_permission` WHERE `permission_code` NOT IN ('admin:perm', 'admin:role', 'role:view', 'role:add', 'role:edit', 'role:delete', 'role:permission');

-- 审批人员(role_id=2): 仅有入驻审批和查看权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `sys_permission` WHERE `permission_code` IN ('admin', 'admin:residency', 'residency:view', 'residency:approve', 'residency:edit');

-- 普通用户(role_id=4): 无后台权限

-- 将 admin 用户设为超级管理员
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);
