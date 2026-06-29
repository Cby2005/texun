-- Upgrade an existing database for the greenhouse strawberry diagnosis flow.
ALTER TABLE `knowledge_question`
    ADD COLUMN `crop_type` VARCHAR(50) DEFAULT NULL COMMENT '作物类型' AFTER `crop_id`,
    ADD COLUMN `land_id` BIGINT DEFAULT NULL COMMENT '关联地块ID' AFTER `crop_type`,
    ADD COLUMN `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '生产批次号' AFTER `land_id`,
    ADD COLUMN `growth_stage` VARCHAR(50) DEFAULT NULL COMMENT '生长阶段' AFTER `batch_no`,
    ADD COLUMN `region` VARCHAR(100) DEFAULT NULL COMMENT '所在地区' AFTER `growth_stage`,
    ADD INDEX `idx_land_id` (`land_id`),
    ADD INDEX `idx_batch_no` (`batch_no`);
