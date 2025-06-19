CREATE TABLE `t_school_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `source_school_id` bigint NOT NULL COMMENT '源校区ID',
  `target_school_id` bigint NOT NULL COMMENT '目标校区ID',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '距离（公里）',
  `estimated_time` int DEFAULT NULL COMMENT '预计送达时间（分钟）',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-可用，0-不可用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_source_target` (`source_school_id`,`target_school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校区关系表';