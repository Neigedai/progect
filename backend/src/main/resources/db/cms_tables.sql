-- 后台内容管理相关表 DDL (需求文档 3.9)
-- park_info 扩展字段 + cms_article + cms_banner

USE park_service;

-- park_info 扩展：增加后台管理所需字段
ALTER TABLE park_info
    ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    ADD COLUMN IF NOT EXISTS sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    ADD COLUMN IF NOT EXISTS video_url VARCHAR(255) DEFAULT NULL COMMENT '园区宣传视频链接',
    ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    ADD COLUMN IF NOT EXISTS update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人';

-- 资讯文章表
CREATE TABLE IF NOT EXISTS cms_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    type VARCHAR(20) NOT NULL DEFAULT 'news' COMMENT '文章类型: news-动态 policy-政策',
    summary VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    content TEXT COMMENT '文章正文（Markdown/HTML）',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    video_url VARCHAR(500) DEFAULT NULL COMMENT '视频地址',
    author VARCHAR(64) DEFAULT NULL COMMENT '作者',
    source VARCHAR(100) DEFAULT NULL COMMENT '来源',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_publish_time (publish_time),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯文章表';

-- 轮播图表
CREATE TABLE IF NOT EXISTS cms_banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    begin_time DATETIME DEFAULT NULL COMMENT '生效开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '生效结束时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ====== 初始数据 ======

-- 园区扩展字段更新
UPDATE park_info SET status = 1, sort_order = 1, video_url = '/static/videos/digital-tech-intro.mp4', create_by = 'admin', update_by = 'admin' WHERE id = 1;
UPDATE park_info SET status = 1, sort_order = 2, video_url = '/static/videos/biomed-intro.mp4', create_by = 'admin', update_by = 'admin' WHERE id = 2;

-- 资讯文章示例数据
INSERT INTO cms_article (title, type, summary, content, cover_image, author, source, status, publish_time, sort_order, create_by, update_by) VALUES
(
    '数字科技园获评国家级双创示范基地',
    'news',
    '近日，数字科技园成功获评国家级双创示范基地，标志着园区创新创业生态建设迈上新台阶。',
    '## 数字科技园获评国家级双创示范基地\n\n近日，经国务院办公厅批准，数字科技园正式获评**国家级双创示范基地**。\n\n### 建设成效\n\n- 累计孵化科技型企业 200+ 家\n- 高新技术企业占比超过 60%\n- 带动就业岗位 5000+ 个\n\n### 未来规划\n\n园区将进一步优化双创生态，计划三年内新增孵化面积 10 万平方米。',
    '/static/images/articles/shuangchuang.png',
    '园区运营中心',
    '园区快讯',
    1,
    '2026-05-15 09:00:00',
    1,
    'admin',
    'admin'
),
(
    '关于2026年度高新技术企业申报工作的通知',
    'policy',
    '2026年度高新技术企业申报工作已启动，请园区内符合条件的企业按要求提交材料。',
    '## 关于2026年度高新技术企业申报工作的通知\n\n各入驻企业：\n\n根据科技部《高新技术企业认定管理办法》，现启动2026年度高新技术企业申报工作。\n\n### 申报条件\n\n1. 企业注册成立一年以上\n2. 拥有核心自主知识产权\n3. 研发费用占比达标\n\n### 申报时间\n\n**材料提交截止：2026年6月30日**\n\n### 联系方式\n\n园区政策服务窗口：A栋一楼服务大厅',
    '/static/images/articles/gaoqirenzheng.png',
    '政策服务部',
    '科技部火炬中心',
    1,
    '2026-05-10 14:30:00',
    2,
    'admin',
    'admin'
),
(
    '园区首届企业技术交流会成功举办',
    'news',
    '5月8日，园区首届企业技术交流会在学术报告厅成功举办，30余家企业代表参会。',
    '## 园区首届企业技术交流会成功举办\n\n5月8日下午，数字科技园首届企业技术交流会在学术报告厅成功举办。\n\n### 参会企业\n\n来自人工智能、大数据、物联网等领域的 30 余家企业代表参加了本次交流会。\n\n### 活动亮点\n\n- 技术路演：5家企业展示最新技术成果\n- 自由对接：促成 12 项合作意向\n- 圆桌论坛：共话数字化转型挑战与机遇',
    '/static/images/articles/jiaoliuhui.png',
    '园区运营中心',
    '园区活动',
    0,
    NULL,
    3,
    'admin',
    'admin'
);

-- 轮播图示例数据
INSERT INTO cms_banner (title, image_url, link_url, sort_order, status, begin_time, end_time, create_by, update_by) VALUES
(
    '数字科技园欢迎您',
    '/static/images/banners/banner-1.png',
    '/park-overview',
    1,
    1,
    '2026-01-01 00:00:00',
    '2026-12-31 23:59:59',
    'admin',
    'admin'
),
(
    '企业服务大厅全新上线',
    '/static/images/banners/banner-2.png',
    '/services',
    2,
    1,
    '2026-05-01 00:00:00',
    '2026-07-31 23:59:59',
    'admin',
    'admin'
),
(
    '2026年度高新企业申报启动',
    '/static/images/banners/banner-3.png',
    '/services',
    3,
    1,
    '2026-05-10 00:00:00',
    '2026-06-30 23:59:59',
    'admin',
    'admin'
),
(
    '园区荣誉资质展示',
    '/static/images/banners/banner-4.png',
    '/park-overview',
    4,
    0,
    '2026-06-01 00:00:00',
    '2026-09-30 23:59:59',
    'admin',
    'admin'
);

-- 增量迁移：为 cms_article 添加 video_url 列
ALTER TABLE cms_article ADD COLUMN IF NOT EXISTS video_url VARCHAR(500) DEFAULT NULL COMMENT '视频地址' AFTER cover_image;
