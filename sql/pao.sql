-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) NOT NULL COMMENT '微信openid',
  `nickname` varchar(50) DEFAULT '新用户' COMMENT '昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `like_count` int DEFAULT '0' COMMENT '被点赞数',
  `dislike_count` int DEFAULT '0' COMMENT '被点踩数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 示例数据
INSERT INTO `user` (`id`, `openid`, `nickname`, `avatar_url`, `phone`, `like_count`) VALUES
(1, 'wx1234567890abcdef', '校园跑腿王', 'https://example.com/avatar1.jpg', '13800138001', 42),
(2, 'wx9876543210fedcba', '快递小能手', 'https://example.com/avatar2.jpg', '13900139002', 35),
(3, 'wxabcdef123456789', '懒人代表', NULL, NULL, 0);

-- ----------------------------
-- 2. 地址簿表
-- ----------------------------
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '所属用户',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `location` varchar(100) NOT NULL COMMENT '定位坐标(经度,纬度)',
  `location_text` varchar(100) NOT NULL COMMENT '位置文本(如XX大学南门)',
  `detail` varchar(200) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址簿';

-- 示例数据
INSERT INTO `address` (`id`, `user_id`, `contact_name`, `contact_phone`, `location`, `location_text`, `detail`, `is_default`) VALUES
(1, 1, '张三', '13800138001', '120.123456,30.654321', '浙江大学紫金港校区', '月牙楼201室', 1),
(2, 1, '张三妈妈', '13900139001', '120.111222,30.333444', '西湖区文三路', '嘉绿苑小区3栋502', 0),
(3, 2, '李四', '13900139002', '120.222333,30.444555', '杭州师范大学仓前校区', '慎园8号楼宿舍',0);

-- ----------------------------
-- 3. 订单表
-- ----------------------------
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `creator_id` bigint NOT NULL COMMENT '下单用户',
  `runner_id` bigint DEFAULT NULL COMMENT '接单用户',
  `pickup_address_id` bigint NOT NULL COMMENT '取件地址',
  `delivery_address_id` bigint NOT NULL COMMENT '送达地址',
  `title` varchar(100) NOT NULL COMMENT '订单标题',
  `description` text COMMENT '订单描述',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `status` enum('pending','accepted','completed','canceled') DEFAULT 'pending' COMMENT '状态',
  `images_urls` varchar(500) DEFAULT NULL COMMENT '图片URL(逗号分隔)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_runner` (`runner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 示例数据
INSERT INTO `order` (`order_no`, `creator_id`, `runner_id`, `pickup_address_id`, `delivery_address_id`, `title`, `description`, `amount`, `status`, `images_urls`) VALUES
('ORD20231101001', 3, 1, 3, 1, '代取快递', '顺丰快递 SF123456789，放在宿管阿姨处', 8.50, 'completed', 'https://example.com/img1.jpg'),
('ORD20231101002', 3, NULL, 3, 2, '代送文件', '急送合同到文三路，今天18点前', 15.00, 'pending', NULL),
('ORD20231101003', 2, NULL, 1, 3, '代买奶茶', '茶颜悦色幽兰拿铁，少冰半糖', 20.00, 'accepted', 'https://example.com/img2.jpg,https://example.com/img3.jpg');

-- ----------------------------
-- 4. 支付记录表
-- ----------------------------
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单',
  `user_id` bigint NOT NULL COMMENT '支付用户',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付单号',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` enum('pending','paid','refunded','failed') NOT NULL DEFAULT 'pending' COMMENT '支付状态',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order` (`order_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

-- 示例数据
INSERT INTO `payment` (`order_id`, `user_id`, `transaction_id`, `amount`, `status`, `pay_time`) VALUES
(1, 3, 'WX12345678901234567890', 8.50, 'paid', '2023-11-01 10:23:45'),
(2, 3, NULL, 15.00, 'pending', NULL),
(3, 2, 'WX98765432109876543210', 20.00, 'paid', '2023-11-01 14:12:33');

-- ----------------------------
-- 5. 评价表
-- ----------------------------
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单',
  `reviewer_id` bigint NOT NULL COMMENT '评价人',
  `rating` tinyint NOT NULL COMMENT '评分(1-5)',
  `comment` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order` (`order_id`),
  KEY `idx_reviewer` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价';

-- 示例数据
INSERT INTO `review` (`order_id`, `reviewer_id`, `rating`, `comment`) VALUES
(1, 3, 5, '小哥速度超快，快递包装完好！'),
(3, 2, 4, '奶茶味道不错，就是冰块有点多');

-- ----------------------------
-- 6. 黑名单表
-- ----------------------------
CREATE TABLE `blacklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blocker_id` bigint NOT NULL COMMENT '拉黑人',
  `blocked_user_id` bigint NOT NULL COMMENT '被拉黑人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_block_relation` (`blocker_id`,`blocked_user_id`),
  KEY `idx_blocked` (`blocked_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户黑名单';

-- 示例数据
INSERT INTO `blacklist` (`blocker_id`, `blocked_user_id`) VALUES
(1, 3),
(2, 3);

-- 7. 消息表（支持多种消息类型）
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '雪花ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `msg_type` enum('TEXT','IMAGE','ORDER','SYSTEM') NOT NULL DEFAULT 'TEXT',
  `content` text NOT NULL COMMENT '加密内容或资源URL',
  `extras` json DEFAULT NULL COMMENT '扩展数据',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '精确到毫秒',
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_order` (`order_id`),
  CONSTRAINT `fk_message_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息表';

-- 文本消息（AES加密示例）
INSERT INTO `message` (`sender_id`, `receiver_id`, `msg_type`, `content`, `created_at`) VALUES
(1, 2, 'TEXT', '7A3B8E1F4C9D2A5B6E0F1C3D4E5F6A7B', '2023-11-20 10:00:00.123');

-- 图片消息（OSS地址）
INSERT INTO `message` (`sender_id`, `receiver_id`, `msg_type`, `content`, `extras`) VALUES
(2, 1, 'IMAGE', 'https://oss.dnun.com/msg/123.jpg',
 '{"width": 800, "height": 600, "format": "JPEG"}');

-- 订单关联消息
INSERT INTO `message` (`sender_id`, `receiver_id`, `msg_type`, `content`, `order_id`, `extras`) VALUES
(3, 1, 'ORDER', '您的订单状态已更新', 1,
 '{"order_no": "ORD202311001", "new_status": "ACCEPTED"}');

-- 系统通知
INSERT INTO `message` (`sender_id`, `receiver_id`, `msg_type`, `content`, `extras`) VALUES
(0, 1, 'SYSTEM', 'SYSTEM_MSG_ACCOUNT_UPDATE',
 '{"tpl_id": 5, "vars": {"time": "2023-11-20 14:30"}}');