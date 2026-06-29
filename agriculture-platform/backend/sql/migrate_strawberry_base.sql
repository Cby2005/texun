-- ============================================================
-- 草莓基地管理 - 数据库迁移
-- 将 farm_enterprise 表扩展为草莓基地管理字段
-- ============================================================

USE agriculture_platform;

-- 1. 重命名现有字段（适配新业务）
ALTER TABLE farm_enterprise
    CHANGE COLUMN `name`          `base_name`      VARCHAR(100) NOT NULL                 COMMENT '基地名称',
    CHANGE COLUMN `address`       `detail_address`  VARCHAR(200) DEFAULT ''               COMMENT '详细地址',
    CHANGE COLUMN `contact_name`  `manager_name`    VARCHAR(50)  DEFAULT ''               COMMENT '基地负责人',
    CHANGE COLUMN `contact_phone` `manager_phone`   VARCHAR(20)  DEFAULT ''               COMMENT '联系电话',
    CHANGE COLUMN `icon`          `base_code`       VARCHAR(50)  DEFAULT ''               COMMENT '基地编码',
    MODIFY COLUMN `status`        TINYINT           DEFAULT 0                             COMMENT '0正常 1停用 2建设中',
    MODIFY COLUMN `lng`           DECIMAL(10,6)     DEFAULT NULL                          COMMENT '经度（可空）',
    MODIFY COLUMN `lat`           DECIMAL(10,6)     DEFAULT NULL                          COMMENT '纬度（可空）';

-- 2. 新增草莓基地专有字段
ALTER TABLE farm_enterprise
    ADD COLUMN `enterprise_name`       VARCHAR(100) DEFAULT ''    COMMENT '所属企业/合作社'      AFTER `base_name`,
    ADD COLUMN `base_type`             VARCHAR(50)  DEFAULT ''    COMMENT '基地类型'              AFTER `enterprise_name`,
    ADD COLUMN `main_crop`             VARCHAR(50)  DEFAULT '草莓' COMMENT '主栽作物'            AFTER `base_type`,
    ADD COLUMN `strawberry_variety`    VARCHAR(100) DEFAULT ''    COMMENT '草莓品种'              AFTER `main_crop`,
    ADD COLUMN `region`                VARCHAR(100) DEFAULT ''    COMMENT '所在地区'              AFTER `strawberry_variety`,
    ADD COLUMN `total_area`            DECIMAL(10,2) DEFAULT 0    COMMENT '总面积（亩）'          AFTER `detail_address`,
    ADD COLUMN `greenhouse_count`      INT          DEFAULT 0    COMMENT '温室数量'              AFTER `total_area`,
    ADD COLUMN `planting_area`         DECIMAL(10,2) DEFAULT 0    COMMENT '草莓种植面积（亩）'    AFTER `greenhouse_count`,
    ADD COLUMN `planting_mode`         VARCHAR(50)  DEFAULT ''    COMMENT '种植模式'              AFTER `planting_area`,
    ADD COLUMN `irrigation_mode`       VARCHAR(50)  DEFAULT ''    COMMENT '灌溉方式'              AFTER `planting_mode`,
    ADD COLUMN `expected_yield`        DECIMAL(10,2) DEFAULT 0    COMMENT '年预计产量（kg）'      AFTER `irrigation_mode`,
    ADD COLUMN `enable_monitor`        TINYINT      DEFAULT 0    COMMENT '是否接入环境监测'      AFTER `expected_yield`,
    ADD COLUMN `enable_disease_detect` TINYINT      DEFAULT 0    COMMENT '是否启用病虫害识别'    AFTER `enable_monitor`,
    ADD COLUMN `enable_trace`          TINYINT      DEFAULT 0    COMMENT '是否启用溯源'          AFTER `enable_disease_detect`;
