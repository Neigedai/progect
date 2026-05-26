CREATE TABLE IF NOT EXISTS `service_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `service_id` BIGINT NOT NULL,
  `service_name` VARCHAR(200) DEFAULT '',
  `contact_name` VARCHAR(50) DEFAULT '',
  `contact_phone` VARCHAR(20) DEFAULT '',
  `status` VARCHAR(20) DEFAULT 'pending',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
