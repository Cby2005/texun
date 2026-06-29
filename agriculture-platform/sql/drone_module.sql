-- ============================================
-- 温室无人机巡检与路径规划模块 - 数据库建表
-- 适配版本: longitude/latitude + Haversine距离
-- ============================================

-- 1. 无人机设备表
DROP TABLE IF EXISTS `drone_device`;
CREATE TABLE `drone_device` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `drone_code`     VARCHAR(50)  DEFAULT ''             COMMENT '无人机编号',
  `drone_name`     VARCHAR(100) DEFAULT ''             COMMENT '无人机名称',
  `model`          VARCHAR(50)  DEFAULT ''             COMMENT '型号',
  `battery_level`  DECIMAL(5,2) DEFAULT 100.00          COMMENT '电量百分比',
  `status`         VARCHAR(30)  DEFAULT 'IDLE'          COMMENT '状态:IDLE/RUNNING/CHARGING/ERROR',
  `camera_status`  VARCHAR(30)  DEFAULT 'NORMAL'        COMMENT '摄像头状态:NORMAL/ERROR',
  `longitude`      DECIMAL(12,6) DEFAULT NULL          COMMENT '经度',
  `latitude`       DECIMAL(12,6) DEFAULT NULL          COMMENT '纬度',
  `altitude`       DECIMAL(8,2)  DEFAULT 0.00          COMMENT '飞行高度(米)',
  `greenhouse_id`  BIGINT       DEFAULT NULL           COMMENT '所属温室ID',
  `remark`         VARCHAR(255) DEFAULT ''             COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_drone_code` (`drone_code`),
  KEY `idx_status` (`status`),
  KEY `idx_greenhouse_id` (`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='无人机设备表';

-- 2. 温室巡检点表 (使用真实经纬度)
DROP TABLE IF EXISTS `drone_inspection_point`;
CREATE TABLE `drone_inspection_point` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `point_name`     VARCHAR(100) DEFAULT ''             COMMENT '巡检点名称',
  `greenhouse_id`  BIGINT       DEFAULT NULL           COMMENT '所属温室ID',
  `area_name`      VARCHAR(100) DEFAULT ''             COMMENT '所属区域',
  `longitude`      DECIMAL(12,6) NOT NULL              COMMENT '经度',
  `latitude`       DECIMAL(12,6) NOT NULL              COMMENT '纬度',
  `altitude`       DECIMAL(8,2)  DEFAULT 1.50          COMMENT '飞行高度(米)',
  `point_type`     VARCHAR(30)  DEFAULT 'NORMAL'        COMMENT '点位类型:START/NORMAL/ABNORMAL/END',
  `remark`         VARCHAR(255) DEFAULT ''             COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_point_name` (`point_name`),
  KEY `idx_greenhouse_id` (`greenhouse_id`),
  KEY `idx_point_type` (`point_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='温室巡检点表';

-- 3. 巡检路径规划表
DROP TABLE IF EXISTS `drone_route_plan`;
CREATE TABLE `drone_route_plan` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_code`      VARCHAR(50)  DEFAULT ''             COMMENT '路径编号',
  `route_name`      VARCHAR(100) DEFAULT ''             COMMENT '路径名称',
  `greenhouse_id`   BIGINT       DEFAULT NULL           COMMENT '温室ID',
  `route_type`      VARCHAR(30)  DEFAULT 'DAILY_INSPECTION' COMMENT '路径类型:DAILY_INSPECTION/DISEASE_INSPECTION/ABNORMAL_RECHECK',
  `algorithm_type`  VARCHAR(30)  DEFAULT 'ORDER'         COMMENT '算法类型:ORDER/NEAREST',
  `start_point`     VARCHAR(255) DEFAULT NULL           COMMENT '起点坐标JSON',
  `end_point`       VARCHAR(255) DEFAULT NULL           COMMENT '终点坐标JSON',
  `waypoints`       TEXT                                 COMMENT '航点JSON(含longitude/latitude/altitude)',
  `total_distance`  DECIMAL(10,2) DEFAULT 0.00          COMMENT '总距离(米)',
  `estimated_time`  DECIMAL(10,0) DEFAULT 0             COMMENT '预计耗时(秒)',
  `status`          VARCHAR(30)  DEFAULT 'ACTIVE'        COMMENT '状态',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_code` (`route_code`),
  KEY `idx_greenhouse_id` (`greenhouse_id`),
  KEY `idx_route_type` (`route_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检路径规划表';

-- 4. 巡检任务表
DROP TABLE IF EXISTS `drone_inspection_task`;
CREATE TABLE `drone_inspection_task` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_code`      VARCHAR(50)  DEFAULT ''             COMMENT '任务编号',
  `task_name`      VARCHAR(100) DEFAULT ''             COMMENT '任务名称',
  `drone_id`       BIGINT       DEFAULT NULL           COMMENT '无人机ID',
  `route_id`       BIGINT       DEFAULT NULL           COMMENT '路径ID',
  `greenhouse_id`  BIGINT       DEFAULT NULL           COMMENT '温室ID',
  `task_type`      VARCHAR(30)  DEFAULT 'DAILY_INSPECTION' COMMENT '任务类型',
  `task_status`    VARCHAR(30)  DEFAULT 'PENDING'       COMMENT '任务状态:PENDING/RUNNING/FINISHED/CANCELED/ERROR',
  `start_time`     DATETIME     DEFAULT NULL           COMMENT '开始时间',
  `end_time`       DATETIME     DEFAULT NULL           COMMENT '结束时间',
  `result`         TEXT                                 COMMENT '巡检结果',
  `remark`         VARCHAR(255) DEFAULT ''             COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_code` (`task_code`),
  KEY `idx_drone_id` (`drone_id`),
  KEY `idx_route_id` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务表';

-- 5. 巡检图像表
DROP TABLE IF EXISTS `drone_inspection_image`;
CREATE TABLE `drone_inspection_image` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id`        BIGINT       DEFAULT NULL           COMMENT '巡检任务ID',
  `image_url`      VARCHAR(500) DEFAULT ''             COMMENT '图片地址',
  `capture_point`  VARCHAR(100) DEFAULT ''             COMMENT '拍摄点(经纬度)',
  `detect_result`  VARCHAR(30)  DEFAULT NULL           COMMENT '识别结果:HEALTHY/DISEASE',
  `disease_type`   VARCHAR(100) DEFAULT ''             COMMENT '病害类型',
  `confidence`     DECIMAL(5,4) DEFAULT NULL           COMMENT '置信度',
  `suggestion`     VARCHAR(500) DEFAULT ''             COMMENT '处理建议',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检图像表';

-- 6. 巡检报告表
DROP TABLE IF EXISTS `drone_inspection_report`;
CREATE TABLE `drone_inspection_report` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id`         BIGINT       DEFAULT NULL           COMMENT '关联任务ID',
  `task_name`       VARCHAR(100) DEFAULT ''             COMMENT '任务名称',
  `drone_name`      VARCHAR(100) DEFAULT ''             COMMENT '无人机名称',
  `route_name`      VARCHAR(100) DEFAULT ''             COMMENT '路径名称',
  `area_name`       VARCHAR(100) DEFAULT ''             COMMENT '巡检区域',
  `start_time`      DATETIME     DEFAULT NULL           COMMENT '巡检开始时间',
  `end_time`        DATETIME     DEFAULT NULL           COMMENT '巡检结束时间',
  `total_images`    INT          DEFAULT 0              COMMENT '总图片数量',
  `abnormal_images` INT          DEFAULT 0              COMMENT '异常图片数量',
  `disease_types`   VARCHAR(500) DEFAULT ''             COMMENT '病害类型列表',
  `suggestions`     TEXT                                 COMMENT '处理建议',
  `report_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '报告生成时间',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      DEFAULT 0              COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检报告表';

-- 菜单数据 (如果已存在则忽略 - 使用实际sys_menu表结构)
INSERT IGNORE INTO `sys_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `icon`, `order_num`, `menu_type`, `status`) VALUES
(60, 0, '无人机巡检管理', '', 'Promotion', 5, 'M', 0);
INSERT IGNORE INTO `sys_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `icon`, `order_num`, `menu_type`, `status`) VALUES
(61, 60, '无人机设备管理', '/drone/device', 'Platform', 1, 'C', 0),
(62, 60, '温室巡检点管理', '/drone/point', 'Location', 2, 'C', 0),
(63, 60, '巡检路径规划', '/drone/route', 'Guide', 3, 'C', 0),
(64, 60, '巡检任务管理', '/drone/task', 'List', 4, 'C', 0),
(65, 60, '巡检图像记录', '/drone/image', 'Picture', 5, 'C', 0),
(66, 60, '巡检报告管理', '/drone/report', 'Report', 6, 'C', 0);
