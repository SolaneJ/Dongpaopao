CREATE TABLE `t_cross_school_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '跨校区订单ID',
  `order_id` bigint NOT NULL COMMENT '关联主订单ID',
  `source_school_id` bigint NOT NULL COMMENT '源校区ID',
  `target_school_id` bigint NOT NULL COMMENT '目标校区ID',
  `source_dispatcher_id` bigint DEFAULT NULL COMMENT '源校区跑腿员ID',
  `target_dispatcher_id` bigint DEFAULT NULL COMMENT '目标校区跑腿员ID',
  `handover_time` datetime DEFAULT NULL COMMENT '交接时间',
  `handover_location` varchar(255) DEFAULT NULL COMMENT '交接地点',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-待接单，2-源校区已接单，3-已交接，4-目标校区已接单，5-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_id` (`order_id`),
  KEY `idx_source_school_id` (`source_school_id`),
  KEY `idx_target_school_id` (`target_school_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨校区订单表';