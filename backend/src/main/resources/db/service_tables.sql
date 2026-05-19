-- 企业服务大厅相关表 DDL + 初始数据
USE park_service;

-- 服务分类表
CREATE TABLE IF NOT EXISTS service_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL COMMENT '分类编码',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- 服务项目表
CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL COMMENT '所属分类ID',
    service_name VARCHAR(200) NOT NULL COMMENT '服务名称',
    summary VARCHAR(500) DEFAULT NULL COMMENT '服务简介（列表摘要）',
    applicable_enterprise VARCHAR(200) DEFAULT NULL COMMENT '适用企业类型',
    detail_desc TEXT COMMENT '服务详情描述',
    steps JSON DEFAULT NULL COMMENT '流程步骤 JSON: [{"title":"","desc":""}]',
    price_info JSON DEFAULT NULL COMMENT '定价信息 JSON: {"price":"","subsidy":"","note":""}',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序权重（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 服务商表（P1 占位，暂不接入业务逻辑）
CREATE TABLE IF NOT EXISTS service_provider (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_name VARCHAR(200) NOT NULL COMMENT '服务商名称',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    qualifications TEXT COMMENT '资质说明',
    logo VARCHAR(255) DEFAULT NULL COMMENT 'Logo URL',
    description VARCHAR(500) DEFAULT NULL COMMENT '服务商简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商表（P1 预留）';

-- ====== 初始数据 ======

INSERT INTO service_category (category_name, code, sort_order) VALUES
('政策申报', 'policy', 1),
('财税代办', 'finance', 2),
('法律咨询', 'legal', 3),
('人力资源', 'hr', 4),
('知识产权', 'ip', 5);

-- 政策申报类服务
INSERT INTO service_item (category_id, service_name, summary, applicable_enterprise, detail_desc, steps, price_info, status, sort_order) VALUES
(1, '高新技术企业认定', '协助企业完成国家高新技术企业认定，享受税收优惠政策', '科技型企业、研发型企业',
'高新技术企业认定是国家针对科技型企业的一项扶持政策，通过认定后可享受 15% 企业所得税优惠税率，以及各级政府的配套资金奖励。',
'[{"title":"初步评估","desc":"企业自评是否符合高新技术领域及基本条件"},{"title":"材料准备","desc":"整理知识产权、研发项目、财务审计报告等材料"},{"title":"系统申报","desc":"在火炬中心系统提交认定申请"},{"title":"专家评审","desc":"科技部门组织专家组进行材料审查与现场核查"},{"title":"公示认定","desc":"评审通过后公示 10 个工作日，无异议即颁发证书"}]',
'{"price":"8000-15000元（按企业规模）","subsidy":"认定后可申请省/市级奖励 10-50 万元","note":"不含第三方审计、知识产权代理费用"}', 1, 1),

(1, '科技型中小企业评价', '帮助企业完成科技型中小企业入库评价，享受研发费加计扣除', '中小科技企业',
'科技型中小企业评价是国家支持中小企业技术创新的重要举措，入库后可享受研发费用 100% 加计扣除等政策红利。',
'[{"title":"在线填报","desc":"在科技型中小企业服务系统注册并填报企业信息"},{"title":"形式审查","desc":"地方科技部门对企业提交材料的规范性进行审查"},{"title":"公示入库","desc":"审查通过后公示，无异议即纳入科技型中小企业库"}]',
'{"price":"免费（政府服务）","subsidy":"入库后享研发费 100% 加计扣除","note":"每年需在规定时间内完成评价"}', 1, 2);

-- 财税代办类服务
INSERT INTO service_item (category_id, service_name, summary, applicable_enterprise, detail_desc, steps, price_info, status, sort_order) VALUES
(2, '代理记账', '专业会计团队提供月度记账报税服务，满足税务合规要求', '初创企业、小微企业',
'由持证会计团队为企业提供全套代理记账服务，包含原始凭证整理、记账凭证编制、账簿登记、财务报表出具等。',
'[{"title":"签订协议","desc":"签订代理记账服务合同，明确服务范围与费用"},{"title":"票据收集","desc":"每月月初收集企业上月经营票据"},{"title":"记账报税","desc":"会计团队完成记账并按时进行纳税申报"},{"title":"报表反馈","desc":"每月出具财务报表及税务申报回执"}]',
'{"price":"3000-6000元/年","subsidy":"无","note":"含月度记账、季度/年度报税，不含审计"}', 1, 1);

-- 人力资源类服务
INSERT INTO service_item (category_id, service_name, summary, applicable_enterprise, detail_desc, steps, price_info, status, sort_order) VALUES
(4, '人才招聘服务', '依托园区人才库为企业提供精准招聘方案', '入驻园区企业、规模以上企业',
'园区运营中心联合专业招聘平台，为入驻企业提供线上+线下招聘服务，涵盖简历筛选、面试组织、背景调查等全流程。',
'[{"title":"需求沟通","desc":"与企业 HR 沟通岗位需求及人才画像"},{"title":"岗位发布","desc":"在园区招聘平台及合作渠道发布岗位信息"},{"title":"简历筛选","desc":"根据岗位要求进行简历初筛与推荐"},{"title":"面试组织","desc":"协调面试时间地点，提供面试场地支持"},{"title":"录用跟进","desc":"录用通知发送及入职引导服务"}]',
'{"price":"按岗位年薪的 10%-15% 收费","subsidy":"园区入驻企业首年享 50% 补贴","note":"保用期 3 个月，未通过免费重新推荐"}', 1, 1);

-- 知识产权类服务
INSERT INTO service_item (category_id, service_name, summary, applicable_enterprise, detail_desc, steps, price_info, status, sort_order) VALUES
(5, '商标注册代理', '全程代理商标注册申请，提高核准通过率', '所有企业',
'专业知识产权代理机构为企业提供商标查询、申请撰写、提交、驳回复审等全流程代理服务。',
'[{"title":"商标查询","desc":"查询拟注册商标是否已被占用或近似"},{"title":"材料准备","desc":"准备商标图样、申请主体资质等材料"},{"title":"提交申请","desc":"向国家知识产权局提交注册申请"},{"title":"实质审查","desc":"商标局进行形式审查及实质审查（约 4-6 个月）"},{"title":"公告注册","desc":"初审公告 3 个月，无异议即核准注册并颁发证书"}]',
'{"price":"800-1500元/类","subsidy":"无","note":"官费另计（电子申请 270元/类），非最终驳回可免费复审一次"}', 1, 2);
