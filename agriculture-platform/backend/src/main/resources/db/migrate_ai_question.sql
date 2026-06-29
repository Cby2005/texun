-- 智能问答核心表
CREATE TABLE IF NOT EXISTS `agri_ai_question` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `base_id` BIGINT DEFAULT NULL COMMENT '所属基地ID',
  `greenhouse_id` BIGINT DEFAULT NULL COMMENT '所属温室ID',
  `question_type` VARCHAR(50) DEFAULT NULL COMMENT '问题类型',
  `growth_stage` VARCHAR(50) DEFAULT NULL COMMENT '草莓生长阶段',
  `question_content` TEXT NOT NULL COMMENT '问题内容',
  `related_image` VARCHAR(500) DEFAULT NULL COMMENT '相关图片URL',
  `ai_answer` MEDIUMTEXT DEFAULT NULL COMMENT 'AI回答内容',
  `model_name` VARCHAR(100) DEFAULT NULL COMMENT '模型名称',
  `prompt_tokens` INT DEFAULT 0 COMMENT '提示词token数',
  `completion_tokens` INT DEFAULT 0 COMMENT '回答token数',
  `total_tokens` INT DEFAULT 0 COMMENT '总token数',
  `answer_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '回答状态: pending/answered/failed/expert_review',
  `need_expert_review` TINYINT DEFAULT 0 COMMENT '是否需要专家复核',
  `user_feedback` VARCHAR(20) DEFAULT 'none' COMMENT '用户反馈: helpful/unhelpful/none',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_question_type` (`question_type`),
  INDEX `idx_answer_status` (`answer_status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能问答表';

-- 专家复核任务表
CREATE TABLE IF NOT EXISTS `agri_expert_review_task` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `question_id` BIGINT NOT NULL COMMENT '关联问答ID',
  `expert_id` BIGINT DEFAULT NULL COMMENT '专家用户ID',
  `review_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '复核状态: pending/reviewed/closed',
  `expert_answer` MEDIUMTEXT DEFAULT NULL COMMENT '专家回答',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  INDEX `idx_question_id` (`question_id`),
  INDEX `idx_expert_id` (`expert_id`),
  INDEX `idx_review_status` (`review_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专家复核任务表';
