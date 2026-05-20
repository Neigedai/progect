CREATE TABLE IF NOT EXISTS `enterprise_auth` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `company_name` VARCHAR(255) NOT NULL,
  `credit_code` VARCHAR(32) NOT NULL,
  `license_url` VARCHAR(255),
  `legal_person_id_url` VARCHAR(255),
  `legal_face_verified` TINYINT(1) NOT NULL DEFAULT 0,
  `auth_status` VARCHAR(20) NOT NULL DEFAULT 'pending',
  `review_comment` VARCHAR(500),
  `reviewed_by` BIGINT,
  `auth_submitted_at` DATETIME,
  `auth_reviewed_at` DATETIME,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_enterprise_auth_user` (`user_id`),
  UNIQUE KEY `uk_enterprise_auth_credit_code` (`credit_code`),
  INDEX `idx_enterprise_auth_status` (`auth_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `enterprise_auth` ADD COLUMN `tag` VARCHAR(50) DEFAULT NULL COMMENT '企业标签，如重点企业、初创企业';
