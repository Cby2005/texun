-- ============================================================
-- 轻量级农技内容推荐系统 - 数据库变更
-- ============================================================

-- 1. 为 agri_content 表增加 tags 和 collect_count 字段（如果已存在请忽略）
ALTER TABLE agri_content
    ADD COLUMN `tags` VARCHAR(500) DEFAULT '' COMMENT 'tags',
    ADD COLUMN `collect_count` INT DEFAULT 0 COMMENT 'collect_count';

-- 2. 用户行为日志表
CREATE TABLE IF NOT EXISTS `user_behavior_log` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL COMMENT '用户ID',
    `content_id`   BIGINT       DEFAULT NULL COMMENT '内容ID',
    `behavior_type` VARCHAR(50) NOT NULL COMMENT '行为类型: view,click,like,collect,comment,search,play_finish',
    `keyword`      VARCHAR(255) DEFAULT NULL COMMENT '搜索关键词',
    `duration`     INT          DEFAULT 0 COMMENT '停留时长或播放时长，单位秒',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_content_id` (`content_id`),
    INDEX `idx_behavior_type` (`behavior_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

-- 3. 用户兴趣标签表
CREATE TABLE IF NOT EXISTS `user_interest_tag` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `tag_name`    VARCHAR(100) NOT NULL COMMENT '标签名称',
    `tag_type`    VARCHAR(20)  DEFAULT 'TAG' COMMENT '标签类型: TAG(内容标签), CATEGORY(分类), KEYWORD(搜索关键词)',
    `weight`      DOUBLE       DEFAULT 0 COMMENT '兴趣权重',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag_type` (`user_id`, `tag_name`, `tag_type`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_weight` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表';

-- 4. 用户画像汇总表
CREATE TABLE IF NOT EXISTS `user_profile` (
    `id`                BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT   NOT NULL UNIQUE COMMENT '用户ID',
    `crop_tags`         TEXT     DEFAULT NULL COMMENT '作物兴趣，如草莓,番茄',
    `interest_tags`     TEXT     DEFAULT NULL COMMENT '兴趣标签JSON，如{"草莓":36,"白粉病":20}',
    `category_weights`  TEXT     DEFAULT NULL COMMENT '分类权重JSON，如{"病虫害防治":28,"种植管理":15}',
    `last_active_time`  DATETIME DEFAULT NULL COMMENT '最近活跃时间',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像汇总表';
