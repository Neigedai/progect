-- 园区概况相关表 DDL
-- TODO: 待与 CONVENTIONS.md 规范对齐（is_deleted vs deleted 字段名统一）

USE park_service;

-- 园区基础信息表
CREATE TABLE IF NOT EXISTS park_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    park_name VARCHAR(100) NOT NULL COMMENT '园区名称',
    logo VARCHAR(255) DEFAULT NULL COMMENT '园区Logo URL（相对路径或外部URL占位）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认园区: 0-否 1-是',
    address VARCHAR(255) DEFAULT NULL COMMENT '园区地址',
    description TEXT COMMENT '园区简介',
    plan_image VARCHAR(255) DEFAULT NULL COMMENT '园区规划图URL',
    plan_description TEXT COMMENT '规划文字说明（P1）',
    transport_info TEXT COMMENT '交通信息说明（P1）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区基础信息表';

-- 配套设施表
CREATE TABLE IF NOT EXISTS park_facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    park_id BIGINT NOT NULL COMMENT '所属园区ID',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    icon VARCHAR(255) DEFAULT NULL COMMENT '设施图标URL',
    description VARCHAR(500) DEFAULT NULL COMMENT '设施描述',
    image VARCHAR(255) DEFAULT NULL COMMENT '设施图片URL',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_park_id (park_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区配套设施表';

-- 荣誉资质表
CREATE TABLE IF NOT EXISTS park_honor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    park_id BIGINT NOT NULL COMMENT '所属园区ID',
    honor_name VARCHAR(200) NOT NULL COMMENT '资质/荣誉名称',
    image VARCHAR(255) DEFAULT NULL COMMENT '证书/牌匾图片URL',
    award_year VARCHAR(10) DEFAULT NULL COMMENT '颁发年份',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_park_id (park_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区荣誉资质表';

-- ====== 初始数据 ======

INSERT INTO park_info (park_name, logo, is_default, address, description, plan_image, plan_description, transport_info) VALUES
(
    '数字科技园',
    '/static/images/parks/logo-digital-tech.png',
    1,
    '某市高新区创新大道 168 号',
    '数字科技园是国家级高新技术产业开发区核心载体，聚焦人工智能、大数据、云计算等前沿数字产业，已入驻企业 300+ 家。',
    '/static/images/parks/plan-digital-tech.png',
    '园区总规划面积 500 亩，分三期建设：一期研发办公区（已建成）、二期产业孵化中心（在建）、三期人才配套社区（规划中）。',
    '地铁 2 号线直达（创新大道站 A 口），距高铁站 15 分钟车程，公交 101/102/203 路经停。园区配备内部通勤班车（工作日早晚高峰循环）。'
),
(
    '生物医药产业园',
    '/static/images/parks/logo-biomed.png',
    0,
    '某市经开区健康大道 66 号',
    '生物医药产业园聚焦生物制药、医疗器械、健康科技三大领域，是省级生物医药产业示范基地。',
    '/static/images/parks/plan-biomed.png',
    '园区规划面积 350 亩，包含研发实验楼群、GMP 标准厂房、共享检测中心、冷链物流仓库。',
    '地铁 5 号线（健康大道站），距高速出口 3 公里，公交 330/401 路经停。'
);

-- 数字科技园配套设施
INSERT INTO park_facility (park_id, facility_name, icon, description, image, sort_order) VALUES
(1, '智慧食堂', '/static/images/icons/canteen.png', '支持扫码点餐、人脸支付，提供早中晚餐及深夜简餐', '/static/images/facilities/canteen-1.png', 1),
(1, '共享会议室', '/static/images/icons/meeting.png', '20间大小会议室在线预约，配备智能投屏与视频会议系统', '/static/images/facilities/meeting-1.png', 2),
(1, '人才公寓', '/static/images/icons/apartment.png', '精装单间/套间拎包入住，配套洗衣房、快递驿站', '/static/images/facilities/apartment-1.png', 3),
(1, '健身中心', '/static/images/icons/gym.png', '24小时开放，含力量器械区、瑜伽室、淋浴间', '/static/images/facilities/gym-1.png', 4),
(1, '便利店', '/static/images/icons/store.png', '24小时便利店 + 现磨咖啡吧', '/static/images/facilities/store-1.png', 5);

-- 生物医药产业园配套设施
INSERT INTO park_facility (park_id, facility_name, icon, description, image, sort_order) VALUES
(2, '实验食堂', '/static/images/icons/canteen.png', '提供营养套餐、轻食沙拉，可预订实验加班简餐', '/static/images/facilities/canteen-2.png', 1),
(2, '学术报告厅', '/static/images/icons/auditorium.png', '200人阶梯报告厅，支持学术会议与项目路演', '/static/images/facilities/auditorium-2.png', 2),
(2, '冷链物流站', '/static/images/icons/coldchain.png', '园区专属冷链物流配货点，支持生物样本运输', '/static/images/facilities/coldchain-2.png', 3),
(2, '共享检测中心', '/static/images/icons/lab.png', '配备 HPLC、PCR、质谱等常用检测设备，预约使用', '/static/images/facilities/lab-2.png', 4);

-- 数字科技园荣誉资质
INSERT INTO park_honor (park_id, honor_name, image, award_year, sort_order) VALUES
(1, '国家级高新技术产业开发区', '/static/images/honors/national-tech.png', '2023', 1),
(1, '国家双创示范基地', '/static/images/honors/double-creativity.png', '2022', 2),
(1, '省级数字经济示范园区', '/static/images/honors/province-digital.png', '2024', 3),
(1, 'ISO 14001 环境管理体系认证', '/static/images/honors/iso14001.png', '2023', 4);

-- 生物医药产业园荣誉资质
INSERT INTO park_honor (park_id, honor_name, image, award_year, sort_order) VALUES
(2, '省级生物医药产业示范基地', '/static/images/honors/province-biomed.png', '2023', 1),
(2, 'GMP 标准厂房认证', '/static/images/honors/gmp.png', '2024', 2),
(2, '生物安全二级实验室备案', '/static/images/honors/bsl2.png', '2023', 3);
