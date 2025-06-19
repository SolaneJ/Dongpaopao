CREATE TABLE `t_order_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_type` tinyint DEFAULT 1 COMMENT '操作人类型：1-用户，2-跑腿员，3-系统',
  `old_status` tinyint DEFAULT NULL COMMENT '原状态',
  `new_status` tinyint NOT NULL COMMENT '新状态',
  `action` varchar(50) DEFAULT NULL COMMENT '操作描述',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';