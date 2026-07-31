-- ============================================================
-- 云途智旅 数据库初始化脚本
-- 数据库: smart_travel (MySQL 8.0+)
-- ============================================================

USE smart_travel;

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户自增主键',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名，不可重复',
  `password` VARCHAR(100) NOT NULL COMMENT '加密存储用户密码（BCrypt）',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '用户绑定邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '用户手机号',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '用户真实姓名/昵称',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '用户头像图片访问地址',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：user普通游客 / admin管理员',
  `preferences` JSON DEFAULT NULL COMMENT '旅游偏好标签数组',
  `max_budget` INT DEFAULT 0 COMMENT '用户人均预算上限（元）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1正常，0禁用',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0未删除，1已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号注册创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号信息更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 景点信息表
CREATE TABLE IF NOT EXISTS `tb_attraction` (
  `attraction_id` VARCHAR(32) NOT NULL COMMENT '景点ID（如 A001）',
  `name` VARCHAR(100) NOT NULL COMMENT '景点名称',
  `city` VARCHAR(50) NOT NULL COMMENT '景点所属城市',
  `type` VARCHAR(50) NOT NULL COMMENT '景点分类：历史古迹/自然风光/美食街区',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '门票价格',
  `open_time` VARCHAR(50) NOT NULL COMMENT '开放时间段',
  `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `description` TEXT DEFAULT NULL COMMENT '景点文字介绍',
  `img_url` VARCHAR(512) DEFAULT NULL COMMENT '景点封面图片地址',
  `score` DECIMAL(2,1) DEFAULT 5.0 COMMENT '景点综合评分1.0~5.0',
  `visit_count` INT NOT NULL DEFAULT 0 COMMENT '景点访问次数，用于热门排行',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度坐标',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度坐标',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`attraction_id`),
  INDEX `idx_city` (`city`),
  INDEX `idx_type` (`type`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点信息表';

-- 3. 酒店信息表
CREATE TABLE IF NOT EXISTS `tb_hotel` (
  `hotel_id` VARCHAR(32) NOT NULL COMMENT '酒店唯一ID',
  `name` VARCHAR(100) NOT NULL COMMENT '酒店名称',
  `destination` VARCHAR(50) NOT NULL COMMENT '所属城市',
  `star` VARCHAR(20) NOT NULL COMMENT '酒店星级：二星/三星/四星/五星',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单晚房价',
  `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `description` TEXT DEFAULT NULL COMMENT '酒店介绍',
  `img_url` VARCHAR(512) DEFAULT NULL COMMENT '酒店封面图地址',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '剩余可预订房间数',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '酒店联系电话',
  `facilities` VARCHAR(512) DEFAULT NULL COMMENT '设施JSON数组',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`hotel_id`),
  INDEX `idx_destination` (`destination`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店信息表';

-- 4. 城际交通资源表
CREATE TABLE IF NOT EXISTS `tb_traffic` (
  `traffic_id` VARCHAR(32) NOT NULL COMMENT '交通记录唯一ID',
  `start_city` VARCHAR(50) NOT NULL COMMENT '出发城市',
  `end_city` VARCHAR(50) NOT NULL COMMENT '目的地城市',
  `way` VARCHAR(20) NOT NULL COMMENT '交通方式：高铁/飞机/大巴/自驾',
  `code` VARCHAR(20) DEFAULT NULL COMMENT '班次编号 G123/MU567',
  `cost` DECIMAL(10,2) NOT NULL COMMENT '单程票价',
  `duration` VARCHAR(20) NOT NULL COMMENT '全程耗时',
  `depart_time` VARCHAR(20) DEFAULT NULL COMMENT '出发时段',
  `carrier` VARCHAR(50) DEFAULT NULL COMMENT '承运方',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`traffic_id`),
  INDEX `idx_route` (`start_city`,`end_city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城际交通资源表';

-- 5. 行程主表
CREATE TABLE IF NOT EXISTS `tb_itinerary` (
  `itinerary_id` VARCHAR(32) NOT NULL COMMENT '行程唯一ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '归属用户ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '行程标题',
  `start_city` VARCHAR(50) NOT NULL COMMENT '出发城市',
  `destination` VARCHAR(50) NOT NULL COMMENT '目的地城市',
  `days` INT NOT NULL COMMENT '总游玩天数',
  `start_date` DATE DEFAULT NULL COMMENT '行程出发日期',
  `end_date` DATE DEFAULT NULL COMMENT '行程结束日期',
  `total_budget` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '预估总预算',
  `total_cost` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际总花费',
  `interests` VARCHAR(255) DEFAULT NULL COMMENT '兴趣标签逗号分隔',
  `travel_tips` TEXT DEFAULT NULL COMMENT 'AI出行贴士',
  `day_plans_json` LONGTEXT DEFAULT NULL COMMENT '每日行程JSON（替代三张明细表）',
  `people` INT DEFAULT 1 COMMENT '出行人数',
  `status` VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT 'planned计划中/active进行中/completed已完成',
  `is_temp` TINYINT NOT NULL DEFAULT 1 COMMENT '1临时行程，0永久保存',
  `is_archived` TINYINT NOT NULL DEFAULT 0 COMMENT '0未归档，1已归档（过期不可编辑）',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`itinerary_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程主表';

-- 6. 行程景点明细表
CREATE TABLE IF NOT EXISTS `tb_itinerary_attraction` (
  `detail_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细自增主键',
  `itinerary_id` VARCHAR(32) NOT NULL COMMENT '关联行程ID',
  `day_num` INT NOT NULL COMMENT '游玩第几天',
  `order_num` INT DEFAULT 0 COMMENT '当日内排序序号',
  `attraction_id` VARCHAR(32) NOT NULL COMMENT '关联景点ID',
  `item_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '当日门票花费',
  `start_time` VARCHAR(20) DEFAULT NULL COMMENT '游玩开始时间',
  `end_time` VARCHAR(20) DEFAULT NULL COMMENT '游玩结束时间',
  `item_desc` VARCHAR(512) DEFAULT NULL COMMENT '游玩备注',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  INDEX `idx_itinerary_id` (`itinerary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程景点明细表';

-- 7. 行程酒店明细表
CREATE TABLE IF NOT EXISTS `tb_itinerary_hotel` (
  `detail_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细自增主键',
  `itinerary_id` VARCHAR(32) NOT NULL COMMENT '关联行程ID',
  `day_num` INT NOT NULL COMMENT '入住对应第几天',
  `order_num` INT DEFAULT 0 COMMENT '当日住宿排序序号',
  `hotel_id` VARCHAR(32) NOT NULL COMMENT '关联酒店ID',
  `item_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '单晚房费',
  `check_in_time` VARCHAR(20) DEFAULT NULL COMMENT '入住时段',
  `check_out_time` VARCHAR(20) DEFAULT NULL COMMENT '退房时段',
  `item_desc` VARCHAR(512) DEFAULT NULL COMMENT '住宿备注',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  INDEX `idx_itinerary_id` (`itinerary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程酒店明细表';

-- 8. 行程交通明细表
CREATE TABLE IF NOT EXISTS `tb_itinerary_traffic` (
  `detail_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细自增主键',
  `itinerary_id` VARCHAR(32) NOT NULL COMMENT '关联行程ID',
  `day_num` INT NOT NULL COMMENT '交通安排对应第几天',
  `order_num` INT DEFAULT 0 COMMENT '当日交通排序序号',
  `traffic_id` VARCHAR(32) NOT NULL COMMENT '关联交通资源ID',
  `item_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '交通花费',
  `start_time` VARCHAR(20) DEFAULT NULL COMMENT '出发时间',
  `end_time` VARCHAR(20) DEFAULT NULL COMMENT '到达时间',
  `item_desc` VARCHAR(512) DEFAULT NULL COMMENT '交通备注',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  INDEX `idx_itinerary_id` (`itinerary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程交通明细表';

-- 9. 模拟预订订单表
CREATE TABLE IF NOT EXISTS `tb_booking_order` (
  `order_id` VARCHAR(32) NOT NULL COMMENT '订单号 ORD202607200001',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID',
  `itinerary_id` VARCHAR(32) NOT NULL COMMENT '关联行程ID',
  `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型 attraction景点/hotel酒店',
  `resource_id` VARCHAR(32) NOT NULL COMMENT '预订资源ID',
  `quantity` INT DEFAULT 1 COMMENT '预订数量（门票/房间）',
  `total_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `check_in` VARCHAR(20) DEFAULT NULL COMMENT '酒店入住日期',
  `check_out` VARCHAR(20) DEFAULT NULL COMMENT '酒店离店日期',
  `order_status` VARCHAR(20) NOT NULL DEFAULT '待支付' COMMENT '待支付/已完成/已取消',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  PRIMARY KEY (`order_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模拟预订订单表';

-- 10. 系统操作日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
  `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志自增主键',
  `operator_id` BIGINT UNSIGNED NOT NULL COMMENT '操作人用户ID',
  `operator_type` VARCHAR(50) NOT NULL COMMENT '操作类型 add/update/delete/login/generate',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块 attraction/hotel/user/itinerary',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '客户端操作IP',
  `content` TEXT DEFAULT NULL COMMENT '操作详细描述',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数JSON',
  `response_result` TEXT DEFAULT NULL COMMENT '响应结果摘要',
  `status` VARCHAR(20) DEFAULT 'success' COMMENT 'success成功/fail失败',
  `error_message` TEXT DEFAULT NULL COMMENT '失败异常信息',
  `execution_time` INT DEFAULT 0 COMMENT '接口执行耗时（毫秒）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志记录时间',
  PRIMARY KEY (`log_id`),
  INDEX `idx_operator_type` (`operator_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 11. 用户足迹表
CREATE TABLE IF NOT EXISTS `tb_footprint` (
  `footprint_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '足迹自增主键',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `attraction_id` VARCHAR(32) NOT NULL COMMENT '打卡景点ID',
  `visit_date` DATE NOT NULL COMMENT '实际到访日期',
  `rating` TINYINT DEFAULT NULL COMMENT '用户评分1~5',
  `comment` VARCHAR(512) DEFAULT NULL COMMENT '游玩评语',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡创建时间',
  PRIMARY KEY (`footprint_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户足迹表';

-- 12. 意见反馈表
CREATE TABLE IF NOT EXISTS `tb_feedback` (
  `feedback_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '反馈自增主键',
  `user_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '提交用户ID，0=未登录',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '填写用户名',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理，1已处理',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
  PRIMARY KEY (`feedback_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈表';
