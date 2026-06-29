-- 草莓数字农田孪生巡检 - 数据库迁移
-- 执行前确认在 agriculture_platform 库

-- 1. strawberry_planting_record 增加 plot_code 字段
ALTER TABLE strawberry_planting_record ADD COLUMN plot_code VARCHAR(20) DEFAULT NULL COMMENT '地块编号 A-1/B-2 等' AFTER area_name;

-- 2. 草莓地块表
CREATE TABLE IF NOT EXISTS strawberry_plot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plot_code VARCHAR(20) NOT NULL COMMENT '地块编号',
    greenhouse_id BIGINT DEFAULT 1 COMMENT '温室ID',
    area_code VARCHAR(10) NOT NULL COMMENT '区域编码',
    area_name VARCHAR(50) DEFAULT '' COMMENT '区域名称',
    row_no INT DEFAULT 0 COMMENT '行号',
    col_no INT DEFAULT 0 COMMENT '列号',
    plot_status VARCHAR(20) DEFAULT '' COMMENT '地块状态',
    current_planting_record_id BIGINT DEFAULT NULL COMMENT '当前种植记录ID',
    coordinate_x DECIMAL(10,4) DEFAULT 0,
    coordinate_y DECIMAL(10,4) DEFAULT 0,
    coordinate_z DECIMAL(10,4) DEFAULT 0,
    last_inspection_time DATETIME DEFAULT NULL COMMENT '最近巡检时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '0-正常 1-删除',
    UNIQUE KEY uk_plot_code (plot_code)
) COMMENT '草莓地块表';

-- 3. 采摘机器人表
CREATE TABLE IF NOT EXISTS picking_robot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    robot_code VARCHAR(30) NOT NULL COMMENT '机器人编号',
    robot_name VARCHAR(50) DEFAULT '' COMMENT '机器人名称',
    greenhouse_id BIGINT DEFAULT 1,
    battery INT DEFAULT 100 COMMENT '电量%',
    status VARCHAR(20) DEFAULT '空闲' COMMENT '状态：空闲/采摘中/维护/离线',
    max_load DECIMAL(10,2) DEFAULT 50 COMMENT '最大载重kg',
    current_task_id BIGINT DEFAULT NULL,
    remark VARCHAR(200) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_robot_code (robot_code)
) COMMENT '采摘机器人表';

-- 4. 采收记录表
CREATE TABLE IF NOT EXISTS strawberry_harvest_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    harvest_code VARCHAR(30) NOT NULL COMMENT '采收批次号',
    planting_record_id BIGINT NOT NULL COMMENT '种植记录ID',
    plot_id BIGINT DEFAULT NULL COMMENT '地块ID',
    greenhouse_id BIGINT DEFAULT 1,
    robot_id BIGINT DEFAULT NULL COMMENT '采摘机器人ID',
    harvest_weight DECIMAL(10,2) DEFAULT 0 COMMENT '采收重量kg',
    quality_grade VARCHAR(10) DEFAULT '一级' COMMENT '特级/一级/二级/次果',
    harvest_date DATE DEFAULT NULL,
    operator_name VARCHAR(30) DEFAULT '' COMMENT '操作人员',
    harvest_status VARCHAR(20) DEFAULT '已采收',
    remark VARCHAR(200) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_harvest_code (harvest_code),
    INDEX idx_planting_id (planting_record_id)
) COMMENT '草莓采收记录表';

-- 5. 销售批次表
CREATE TABLE IF NOT EXISTS strawberry_sale_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_code VARCHAR(30) NOT NULL COMMENT '销售批次号',
    harvest_record_id BIGINT NOT NULL COMMENT '采收记录ID',
    planting_record_id BIGINT NOT NULL COMMENT '种植记录ID',
    plot_id BIGINT DEFAULT NULL COMMENT '地块ID',
    greenhouse_id BIGINT DEFAULT 1,
    variety VARCHAR(30) DEFAULT '' COMMENT '草莓品种',
    sale_weight DECIMAL(10,2) DEFAULT 0 COMMENT '销售重量kg',
    unit_price DECIMAL(10,2) DEFAULT 0 COMMENT '单价元/kg',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '销售金额',
    customer_name VARCHAR(50) DEFAULT '' COMMENT '客户名称',
    sale_channel VARCHAR(30) DEFAULT '基地直销' COMMENT '销售渠道',
    sale_date DATE DEFAULT NULL,
    trace_code VARCHAR(50) DEFAULT NULL COMMENT '溯源码',
    sale_status VARCHAR(20) DEFAULT '已售出',
    remark VARCHAR(200) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_batch_code (batch_code)
) COMMENT '草莓销售批次表';

-- 插入默认采摘机器人
INSERT IGNORE INTO picking_robot (robot_code, robot_name, battery, status, max_load) VALUES
('ROBOT-001', '采摘机器人1号', 100, '空闲', 50),
('ROBOT-002', '采摘机器人2号', 95, '空闲', 40);
