-- =================================================================
-- 智慧农业综合服务平台 - 统一数据库初始化脚本
-- 数据库: agriculture_platform
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- 密码加密: BCrypt (默认密码均为 123456)
-- 生成日期: 2026-06-18
-- =================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 创建数据库 ====================
DROP DATABASE IF EXISTS `agriculture_platform`;
CREATE DATABASE `agriculture_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agriculture_platform`;

-- =================================================================
-- 第一部分：通用基础表（系统管理）
-- =================================================================

-- ----------------------------
-- 1. 部门表（从 ruanjiansheji 保留）
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `dept_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id`   BIGINT       DEFAULT 0                COMMENT '父部门ID',
    `ancestors`   VARCHAR(50)  DEFAULT ''               COMMENT '祖级列表',
    `dept_name`   VARCHAR(30)  DEFAULT ''               COMMENT '部门名称',
    `order_num`   INT          DEFAULT 0                COMMENT '显示顺序',
    `leader`      VARCHAR(20)  DEFAULT NULL             COMMENT '负责人',
    `phone`       VARCHAR(11)  DEFAULT NULL             COMMENT '联系电话',
    `email`       VARCHAR(50)  DEFAULT NULL             COMMENT '邮箱',
    `status`      TINYINT      DEFAULT 0                COMMENT '部门状态（0正常 1停用）',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 2. 统一用户表（合并 kuangjia + texunsuyuan + ruanjiansheji）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`       BIGINT       DEFAULT NULL             COMMENT '部门ID',
    `username`      VARCHAR(50)  NOT NULL                 COMMENT '登录用户名',
    `password`      VARCHAR(200) NOT NULL                 COMMENT 'BCrypt加密密码',
    `nickname`      VARCHAR(50)  DEFAULT ''               COMMENT '昵称/姓名',
    `user_type`     TINYINT      DEFAULT 4                COMMENT '用户类型: 0管理员 1农场管理员 2溯源企业 3专家 4农户 5消费者',
    `email`         VARCHAR(100) DEFAULT ''               COMMENT '邮箱',
    `phone`         VARCHAR(20)  DEFAULT ''               COMMENT '手机号',
    `sex`           TINYINT      DEFAULT 2                COMMENT '性别: 0男 1女 2未知',
    `avatar`        VARCHAR(500) DEFAULT ''               COMMENT '头像地址',
    `status`        TINYINT      DEFAULT 0                COMMENT '账号状态: 0正常 1禁用',
    `login_ip`      VARCHAR(128) DEFAULT ''               COMMENT '最后登录IP',
    `login_date`    DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
    `last_login_at` DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
    `create_by`     VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`        VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 3. 统一角色表（合并三套）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `code`        VARCHAR(30)  NOT NULL                 COMMENT '角色编码',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '角色名称',
    `role_key`    VARCHAR(100) NOT NULL DEFAULT ''      COMMENT '角色权限字符串',
    `role_sort`   INT          DEFAULT 0                COMMENT '显示顺序',
    `data_scope`  TINYINT      DEFAULT 1                COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下）',
    `status`      TINYINT      DEFAULT 0                COMMENT '角色状态（0正常 1停用）',
    `description` VARCHAR(200) DEFAULT ''               COMMENT '角色描述',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 4. 菜单权限表（从 ruanjiansheji 迁移，最完整）
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `menu_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`  VARCHAR(50)   NOT NULL                 COMMENT '菜单名称',
    `parent_id`  BIGINT        DEFAULT 0                COMMENT '父菜单ID',
    `order_num`  INT           DEFAULT 0                COMMENT '显示顺序',
    `path`       VARCHAR(200)  DEFAULT ''               COMMENT '路由地址',
    `component`  VARCHAR(255)  DEFAULT NULL             COMMENT '组件路径',
    `query`      VARCHAR(255)  DEFAULT NULL             COMMENT '路由参数',
    `is_frame`   TINYINT       DEFAULT 1                COMMENT '是否为外链（0是 1否）',
    `is_cache`   TINYINT       DEFAULT 0                COMMENT '是否缓存（0缓存 1不缓存）',
    `menu_type`  CHAR(1)       DEFAULT ''               COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`    TINYINT       DEFAULT 0                COMMENT '菜单状态（0显示 1隐藏）',
    `status`     TINYINT       DEFAULT 0                COMMENT '菜单状态（0正常 1停用）',
    `perms`      VARCHAR(100)  DEFAULT NULL             COMMENT '权限标识',
    `icon`       VARCHAR(100)  DEFAULT '#'              COMMENT '菜单图标',
    `create_by`  VARCHAR(64)   DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`  VARCHAR(64)   DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`     VARCHAR(500)  DEFAULT ''               COMMENT '备注',
    `deleted`    TINYINT       DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 5. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '用户ID',
    `role_id`    BIGINT   NOT NULL                 COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT   DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`  BIGINT NOT NULL                 COMMENT '角色ID',
    `menu_id`  BIGINT NOT NULL                 COMMENT '菜单ID',
    `deleted`  TINYINT DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 7. 岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
    `post_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`  VARCHAR(64)  NOT NULL                 COMMENT '岗位编码',
    `post_name`  VARCHAR(50)  NOT NULL                 COMMENT '岗位名称',
    `post_sort`  INT          NOT NULL                 COMMENT '显示顺序',
    `status`     TINYINT      NOT NULL DEFAULT 0       COMMENT '状态（0正常 1停用）',
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- ----------------------------
-- 8. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `dict_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name`  VARCHAR(100) DEFAULT ''               COMMENT '字典名称',
    `dict_type`  VARCHAR(100) DEFAULT ''               COMMENT '字典类型',
    `status`     TINYINT      DEFAULT 0                COMMENT '状态（0正常 1停用）',
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`dict_id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 9. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `dict_code`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort`  INT          DEFAULT 0                COMMENT '字典排序',
    `dict_label` VARCHAR(100) DEFAULT ''               COMMENT '字典标签',
    `dict_value` VARCHAR(100) DEFAULT ''               COMMENT '字典键值',
    `dict_type`  VARCHAR(100) DEFAULT ''               COMMENT '字典类型',
    `css_class`  VARCHAR(100) DEFAULT NULL             COMMENT '样式属性',
    `list_class` VARCHAR(100) DEFAULT NULL             COMMENT '表格回显样式',
    `is_default` TINYINT      DEFAULT 0                COMMENT '是否默认（0否 1是）',
    `status`     TINYINT      DEFAULT 0                COMMENT '状态（0正常 1停用）',
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`dict_code`),
    INDEX `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 10. 系统配置/参数表
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `config_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name` VARCHAR(100) DEFAULT ''               COMMENT '参数名称',
    `config_key`  VARCHAR(100) DEFAULT ''               COMMENT '参数键名',
    `config_value` VARCHAR(500) DEFAULT ''              COMMENT '参数键值',
    `config_type` TINYINT      DEFAULT 1                COMMENT '系统内置（0是 1否）',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ----------------------------
-- 11. 文件资源表
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `user_id`       BIGINT       DEFAULT NULL             COMMENT '上传者ID',
    `original_name` VARCHAR(200) NOT NULL                 COMMENT '原始文件名',
    `file_name`     VARCHAR(200) NOT NULL                 COMMENT '存储文件名（UUID）',
    `file_path`     VARCHAR(500) NOT NULL                 COMMENT '文件存储路径',
    `file_url`      VARCHAR(500) NOT NULL                 COMMENT '文件访问URL',
    `file_size`     BIGINT       DEFAULT 0                COMMENT '文件大小（字节）',
    `content_type`  VARCHAR(100) DEFAULT ''               COMMENT 'MIME类型',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';

-- ----------------------------
-- 12. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `user_id`     BIGINT       DEFAULT NULL             COMMENT '操作者ID',
    `username`    VARCHAR(50)  DEFAULT ''               COMMENT '操作者用户名',
    `module`      VARCHAR(50)  DEFAULT ''               COMMENT '模块名称',
    `type`        VARCHAR(20)  DEFAULT ''               COMMENT '操作类型',
    `description` VARCHAR(200) DEFAULT ''               COMMENT '操作描述',
    `method`      VARCHAR(200) DEFAULT ''               COMMENT '请求方法',
    `url`         VARCHAR(500) DEFAULT ''               COMMENT '请求URL',
    `params`      TEXT                                  COMMENT '请求参数',
    `result`      TINYINT      DEFAULT 1                COMMENT '结果：1成功 0失败',
    `error_msg`   TEXT                                  COMMENT '错误信息',
    `cost_time`   BIGINT       DEFAULT 0                COMMENT '耗时（毫秒）',
    `ip`          VARCHAR(50)  DEFAULT ''               COMMENT '操作IP',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_module` (`module`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 13. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `username`    VARCHAR(50)  DEFAULT ''               COMMENT '用户账号',
    `ipaddr`      VARCHAR(128) DEFAULT ''               COMMENT '登录IP地址',
    `status`      TINYINT      DEFAULT 0                COMMENT '登录状态（0成功 1失败）',
    `msg`         VARCHAR(255) DEFAULT ''               COMMENT '提示信息',
    `access_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录表';

-- ----------------------------
-- 14. 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
    `notice_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title` VARCHAR(50) NOT NULL                 COMMENT '公告标题',
    `notice_type` TINYINT      NOT NULL DEFAULT 1       COMMENT '公告类型（1通知 2公告）',
    `notice_content` LONGBLOB  DEFAULT NULL             COMMENT '公告内容',
    `status`      TINYINT      DEFAULT 0                COMMENT '公告状态（0正常 1关闭）',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(255) DEFAULT NULL             COMMENT '备注',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ----------------------------
-- 15. 敏感词表
-- ----------------------------
DROP TABLE IF EXISTS `sys_sensitive_word`;
CREATE TABLE `sys_sensitive_word` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `word`       VARCHAR(50) NOT NULL                 COMMENT '敏感词',
    `level`      TINYINT     DEFAULT 1                COMMENT '敏感等级：1一般 2严重',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT     DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- ----------------------------
-- 16. 用户反馈表
-- ----------------------------
DROP TABLE IF EXISTS `sys_feedback`;
CREATE TABLE `sys_feedback` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT        DEFAULT NULL             COMMENT '反馈用户ID',
    `content`     VARCHAR(3000) DEFAULT NULL             COMMENT '反馈内容',
    `img`         VARCHAR(1000) DEFAULT NULL             COMMENT '图片',
    `contacts`    VARCHAR(500)  DEFAULT NULL             COMMENT '联系方式',
    `status`      TINYINT       DEFAULT 0                COMMENT '0未处理 1已处理',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT       DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- =================================================================
-- 第二部分：农场生产管理业务表（farm_ 前缀）
-- =================================================================

-- ----------------------------
-- 17. 农场/企业表
-- ----------------------------
DROP TABLE IF EXISTS `farm_enterprise`;
CREATE TABLE `farm_enterprise` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '企业ID',
    `name`        VARCHAR(100) NOT NULL                 COMMENT '企业名称',
    `address`     VARCHAR(200) DEFAULT ''               COMMENT '企业地址',
    `lng`         DECIMAL(10,6) DEFAULT NULL            COMMENT '经度',
    `lat`         DECIMAL(10,6) DEFAULT NULL            COMMENT '纬度',
    `icon`        VARCHAR(200) DEFAULT ''               COMMENT '企业图标',
    `contact_name` VARCHAR(50) DEFAULT ''               COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT ''              COMMENT '联系电话',
    `admin_id`    BIGINT       DEFAULT NULL             COMMENT '管理员用户ID（关联sys_user）',
    `status`      TINYINT      DEFAULT 0                COMMENT '0正常 1停用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农场/企业表';

-- ----------------------------
-- 18. 地块表（大棚/鱼塘/大田/仓库）
-- ----------------------------
DROP TABLE IF EXISTS `farm_land`;
CREATE TABLE `farm_land` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地块ID',
    `enterprise_id` BIGINT      NOT NULL                 COMMENT '所属企业ID',
    `number`       INT          NOT NULL                 COMMENT '大棚/鱼塘编号',
    `type`         TINYINT      NOT NULL DEFAULT 0       COMMENT '类型: 0大棚 1鱼塘 2大田 3仓库',
    `display_name` VARCHAR(100) DEFAULT ''               COMMENT '显示名称',
    `camera_pass_num` VARCHAR(50) DEFAULT '1'            COMMENT '摄像头通道号',
    `area`         DECIMAL(10,2) DEFAULT 0               COMMENT '面积（平方米）',
    `location`     VARCHAR(200) DEFAULT ''               COMMENT '位置描述',
    `status`       TINYINT      DEFAULT 0                COMMENT '0正常 1停用',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地块表（大棚/鱼塘/大田/仓库）';

-- ----------------------------
-- 19. 作物字典表（从 kuangjia 扩展）
-- ----------------------------
DROP TABLE IF EXISTS `farm_crop`;
CREATE TABLE `farm_crop` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '作物ID',
    `name`       VARCHAR(50)  NOT NULL                 COMMENT '作物名称',
    `code`       VARCHAR(30)  NOT NULL                 COMMENT '作物编码',
    `icon`       VARCHAR(500) DEFAULT ''               COMMENT '作物图标',
    `sort_order` INT          DEFAULT 0                COMMENT '排序序号',
    `type`       TINYINT      DEFAULT 0                COMMENT '类型: 0农作物 1渔业 2畜牧业',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作物字典表';

-- ----------------------------
-- 20. 种植计划表
-- ----------------------------
DROP TABLE IF EXISTS `farm_planting_plan`;
CREATE TABLE `farm_planting_plan` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    `enterprise_id`  BIGINT       NOT NULL               COMMENT '所属企业ID',
    `land_id`        BIGINT       NOT NULL               COMMENT '地块ID',
    `crop_id`        BIGINT       NOT NULL               COMMENT '作物ID',
    `plan_name`      VARCHAR(100) NOT NULL               COMMENT '计划名称',
    `season`         INT          DEFAULT 1              COMMENT '季次: 1春 2夏 3秋 4冬',
    `start_date`     DATE         DEFAULT NULL           COMMENT '开始日期',
    `end_date`       DATE         DEFAULT NULL           COMMENT '预计结束日期',
    `expected_yield` DECIMAL(10,2) DEFAULT 0             COMMENT '预期产量',
    `yield_unit`     VARCHAR(20)  DEFAULT 'kg'           COMMENT '产量单位',
    `responsible_id` BIGINT       DEFAULT NULL           COMMENT '负责人ID（关联sys_user）',
    `status`         TINYINT      DEFAULT 0              COMMENT '0计划中 1进行中 2已完成 3已取消',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`         VARCHAR(500) DEFAULT NULL           COMMENT '备注',
    `deleted`        TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='种植计划表';

-- ----------------------------
-- 21. 农事任务表
-- ----------------------------
DROP TABLE IF EXISTS `farm_task`;
CREATE TABLE `farm_task` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '所属企业ID',
    `land_id`         BIGINT       NOT NULL               COMMENT '地块ID',
    `planting_plan_id` BIGINT      DEFAULT NULL            COMMENT '关联种植计划ID',
    `task_name`       VARCHAR(100) NOT NULL               COMMENT '任务名称',
    `task_type`       VARCHAR(30)  DEFAULT ''             COMMENT '任务类型: 播种/施肥/灌溉/打药/采收/其他',
    `description`     TEXT                                COMMENT '任务描述',
    `start_time`      DATETIME     DEFAULT NULL           COMMENT '开始时间',
    `end_time`        DATETIME     DEFAULT NULL           COMMENT '结束时间',
    `assignee_id`     BIGINT       DEFAULT NULL           COMMENT '执行人ID（关联sys_user）',
    `status`          TINYINT      DEFAULT 0              COMMENT '0待执行 1进行中 2已完成 3已取消',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`          VARCHAR(500) DEFAULT NULL           COMMENT '备注',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_assignee_id` (`assignee_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农事任务表';

-- ----------------------------
-- 22. 设备定义表
-- ----------------------------
DROP TABLE IF EXISTS `farm_device`;
CREATE TABLE `farm_device` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    `type`         TINYINT      NOT NULL DEFAULT 0       COMMENT '适用类型: 0农业 1渔业 2大田 3仓库',
    `device_num`   VARCHAR(10)  NOT NULL                 COMMENT '设备编号',
    `name`         VARCHAR(50)  NOT NULL                 COMMENT '设备名称',
    `device_type`  TINYINT      NOT NULL DEFAULT 0       COMMENT '0控制设备(动态) 1采集设备(静态)',
    `need_statistics` TINYINT   DEFAULT 0                COMMENT '是否需要统计',
    `park_device`  TINYINT      DEFAULT 0                COMMENT '是否为园区设备',
    `img`          VARCHAR(100) DEFAULT ''               COMMENT '设备图标',
    `can_modify`   TINYINT      DEFAULT 0                COMMENT '是否支持手动修改数值',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`      TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_num` (`device_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备定义表';

-- ----------------------------
-- 23. 设备关联表
-- ----------------------------
DROP TABLE IF EXISTS `farm_device_rel`;
CREATE TABLE `farm_device_rel` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `device_id`       BIGINT       NOT NULL               COMMENT '设备ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '企业ID',
    `land_id`         BIGINT       NOT NULL               COMMENT '地块ID',
    `device_switch_num` TINYINT    DEFAULT 1              COMMENT '设备开关个数',
    `device_type`     TINYINT      DEFAULT 0              COMMENT '设备类型: 0农业 1渔业 2大田 3仓库',
    `device_state`    TINYINT      DEFAULT 0              COMMENT '设备状态: 0运行中 1预警 2断线',
    `unique_flag`     VARCHAR(50)  DEFAULT ''             COMMENT '企业ID+大棚编号+设备编号唯一键',
    `is_modify`       TINYINT      DEFAULT 0              COMMENT '是否手动修改: 0否 1是',
    `modify_val`      VARCHAR(50)  DEFAULT ''             COMMENT '修改值',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_flag` (`unique_flag`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备关联表';

-- ----------------------------
-- 24. 控制器设置表
-- ----------------------------
DROP TABLE IF EXISTS `farm_device_control`;
CREATE TABLE `farm_device_control` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '设置ID',
    `enterprise_id`    BIGINT       NOT NULL               COMMENT '企业ID',
    `land_id`          BIGINT       NOT NULL               COMMENT '地块ID',
    `device_id`        BIGINT       NOT NULL               COMMENT '控制器设备ID',
    `device_switch_num` INT         DEFAULT 1              COMMENT '控制器开关编号',
    `control_type`     TINYINT      NOT NULL DEFAULT 0     COMMENT '控制类型: 0定时 1循环 2智能',
    `open_device`      VARCHAR(100) DEFAULT NULL           COMMENT '智能控制-打开设备',
    `close_device`     VARCHAR(100) DEFAULT NULL           COMMENT '智能控制-关闭设备',
    `open_type`        INT          DEFAULT NULL           COMMENT '打开设备条件: 0大于 1小于',
    `close_type`       INT          DEFAULT NULL           COMMENT '关闭设备条件: 0大于 1小于',
    `open_val`         DECIMAL(4,1) DEFAULT NULL           COMMENT '打开设备数值',
    `close_val`        DECIMAL(4,1) DEFAULT NULL           COMMENT '关闭设备数值',
    `start_time`       TIME         NOT NULL               COMMENT '开始时间',
    `end_time`         TIME         DEFAULT NULL           COMMENT '结束时间',
    `loop_type`        INT          DEFAULT NULL           COMMENT '循环结束类型: 0时间 1次数',
    `loop_cnt`         INT          DEFAULT NULL           COMMENT '循环次数',
    `duration_time`    INT          DEFAULT NULL           COMMENT '持续时长(分钟)',
    `interval_time`    INT          DEFAULT NULL           COMMENT '间隔时长(分钟)',
    `loop_week`        VARCHAR(100) DEFAULT NULL           COMMENT '循环星期(逗号分隔 1-7)',
    `use_state`        TINYINT      DEFAULT 1              COMMENT '0禁用 1启用',
    `create_time`      DATETIME     NOT NULL               COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='控制器设置表';

-- ----------------------------
-- 25. 环境数据采集表（合并大棚+鱼塘数据）
-- ----------------------------
DROP TABLE IF EXISTS `farm_env_data`;
CREATE TABLE `farm_env_data` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '数据ID',
    `enterprise_id` BIGINT        NOT NULL                COMMENT '企业ID',
    `land_id`       BIGINT        NOT NULL                COMMENT '地块ID',
    `batch_no`      VARCHAR(50)   DEFAULT NULL            COMMENT '生产批次号',
    `device_id`    BIGINT         NOT NULL                COMMENT '设备ID',
    `land_type`    TINYINT        DEFAULT 0               COMMENT '地块类型: 0大棚 1鱼塘 2大田 3仓库',
    `data_value`   DECIMAL(11,2)  DEFAULT NULL            COMMENT '采集数据值',
    `year`         INT            NOT NULL                COMMENT '年份',
    `year_date`    INT            NOT NULL                COMMENT '日期(如 118 表示第118天)',
    `year_date_hour` INT          NOT NULL                COMMENT '日期小时(YYYYMMDDHH)',
    `create_time`  DATETIME       NOT NULL                COMMENT '采集时间',
    `deleted`      TINYINT        DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_land` (`enterprise_id`, `land_id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_year_date_hour` (`year_date_hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境数据采集表';

-- ----------------------------
-- 26. 年度数据汇总表
-- ----------------------------
DROP TABLE IF EXISTS `farm_data_sum`;
CREATE TABLE `farm_data_sum` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
    `enterprise_id` BIGINT        NOT NULL DEFAULT 0      COMMENT '企业ID',
    `land_id`       BIGINT        NOT NULL DEFAULT 0      COMMENT '地块ID',
    `device_id`    BIGINT         NOT NULL DEFAULT 0      COMMENT '设备ID',
    `year`         YEAR           NOT NULL DEFAULT '2000' COMMENT '年度',
    `sum_data`     DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '累计数值',
    `update_time`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT        DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_land` (`enterprise_id`, `land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年度数据汇总表';

-- ----------------------------
-- 27. 农场员工表
-- ----------------------------
DROP TABLE IF EXISTS `farm_employee`;
CREATE TABLE `farm_employee` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '企业ID',
    `user_id`         BIGINT       DEFAULT NULL           COMMENT '关联系统用户ID',
    `name`            VARCHAR(50)  NOT NULL               COMMENT '员工姓名',
    `sex`             TINYINT      DEFAULT 1              COMMENT '0女 1男',
    `icon`            VARCHAR(200) DEFAULT NULL           COMMENT '头像',
    `mobile`          VARCHAR(50)  DEFAULT ''             COMMENT '手机号',
    `position`        VARCHAR(50)  DEFAULT ''             COMMENT '职位',
    `age`             INT          DEFAULT 0              COMMENT '年龄',
    `certificates`    VARCHAR(200) DEFAULT NULL           COMMENT '资格证附件',
    `certificates_valid` DATE     DEFAULT NULL            COMMENT '资格证有效期',
    `healthy_state`   TINYINT      DEFAULT 1              COMMENT '健康状态: 0异常 1正常',
    `address`         VARCHAR(200) DEFAULT NULL           COMMENT '户籍地址',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农场员工表';

-- =================================================================
-- 第三部分：农产品溯源业务表（trace_ 前缀）
-- =================================================================

-- ----------------------------
-- 28. 溯源产品表
-- ----------------------------
DROP TABLE IF EXISTS `trace_product`;
CREATE TABLE `trace_product` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '产品ID',
    `product_code`  VARCHAR(50)  NOT NULL                COMMENT '产品编码',
    `product_name`  VARCHAR(100) NOT NULL                COMMENT '产品名称',
    `category`      VARCHAR(50)  DEFAULT NULL            COMMENT '产品类别',
    `specification` VARCHAR(200) DEFAULT NULL            COMMENT '规格',
    `unit`          VARCHAR(20)  DEFAULT NULL            COMMENT '单位',
    `origin`        VARCHAR(100) DEFAULT NULL            COMMENT '产地',
    `enterprise_id` BIGINT       DEFAULT NULL            COMMENT '所属企业ID',
    `producer_name` VARCHAR(100) DEFAULT NULL            COMMENT '生产者名称',
    `farm_id`       BIGINT       DEFAULT NULL            COMMENT '关联农场ID',
    `crop_id`       BIGINT       DEFAULT NULL            COMMENT '关联作物ID',
    `status`        TINYINT      DEFAULT 0               COMMENT '0下架 1上架',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_code` (`product_code`),
    INDEX `idx_category` (`category`),
    INDEX `idx_farm_id` (`farm_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源产品表';

-- ----------------------------
-- 29. 溯源批次表（增加农场关联）
-- ----------------------------
DROP TABLE IF EXISTS `trace_batch`;
CREATE TABLE `trace_batch` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '批次ID',
    `batch_no`         VARCHAR(50)   NOT NULL                COMMENT '批次号',
    `product_code`     VARCHAR(50)   NOT NULL                COMMENT '产品编码',
    `product_name`     VARCHAR(100)  DEFAULT NULL            COMMENT '产品名称',
    `quantity`         DECIMAL(18,4) NOT NULL                COMMENT '数量',
    `unit`             VARCHAR(20)   DEFAULT NULL            COMMENT '单位',
    `production_date`  DATE          DEFAULT NULL            COMMENT '生产日期',
    `expiry_date`      DATE          DEFAULT NULL            COMMENT '保质期至',
    `farm_id`          BIGINT        DEFAULT NULL            COMMENT '关联农场ID',
    `land_id`          BIGINT        DEFAULT NULL            COMMENT '关联地块ID',
    `crop_id`          BIGINT        DEFAULT NULL            COMMENT '关联作物ID',
    `planting_plan_id` BIGINT        DEFAULT NULL            COMMENT '关联种植计划ID',
    `producer_id`      BIGINT        DEFAULT NULL            COMMENT '生产者ID',
    `producer_name`    VARCHAR(100)  DEFAULT NULL            COMMENT '生产者名称',
    `status`           TINYINT       DEFAULT 0               COMMENT '状态: 0生产中 1已完成 2已失效',
    `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`           VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `deleted`          TINYINT       DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_farm_id` (`farm_id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源批次表';

-- ----------------------------
-- 30. 生产/种植记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_production_record`;
CREATE TABLE `trace_production_record` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`         VARCHAR(50)  NOT NULL                COMMENT '批次编号',
    `sow_time`         DATETIME     DEFAULT NULL            COMMENT '播种时间',
    `fertilizer_record` VARCHAR(500) DEFAULT NULL           COMMENT '施肥记录',
    `pesticide_record`  VARCHAR(500) DEFAULT NULL           COMMENT '农药使用记录',
    `irrigation_record` VARCHAR(500) DEFAULT NULL           COMMENT '灌溉记录',
    `soil_temperature`  VARCHAR(50) DEFAULT NULL            COMMENT '土壤温度',
    `soil_humidity`     VARCHAR(50) DEFAULT NULL            COMMENT '土壤湿度',
    `harvest_time`      DATETIME    DEFAULT NULL            COMMENT '采收时间',
    `responsible_person` VARCHAR(100) DEFAULT NULL          COMMENT '种植负责人',
    `image_url`         VARCHAR(500) DEFAULT NULL           COMMENT '图片地址',
    `operator_id`       BIGINT      DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`     VARCHAR(100) DEFAULT NULL           COMMENT '操作者姓名',
    `chain_hash`        VARCHAR(64) DEFAULT NULL            COMMENT '链上哈希',
    `create_time`       DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`            VARCHAR(500) DEFAULT NULL           COMMENT '备注',
    `deleted`           TINYINT     DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产/种植记录表';

-- ----------------------------
-- 31. 加工记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_processing_record`;
CREATE TABLE `trace_processing_record` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`              VARCHAR(50)  NOT NULL                COMMENT '批次编号',
    `processing_enterprise` VARCHAR(200) DEFAULT NULL            COMMENT '加工企业',
    `processing_time`       DATETIME     DEFAULT NULL            COMMENT '加工时间',
    `processing_method`     VARCHAR(200) DEFAULT NULL            COMMENT '加工方式',
    `processing_temperature` VARCHAR(50) DEFAULT NULL            COMMENT '加工温度',
    `quality_result`        VARCHAR(50)  DEFAULT NULL            COMMENT '质检结果',
    `inspector`             VARCHAR(100) DEFAULT NULL            COMMENT '检测人员',
    `report_url`            VARCHAR(500) DEFAULT NULL            COMMENT '检测报告地址',
    `operator_id`           BIGINT       DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`         VARCHAR(100) DEFAULT NULL            COMMENT '操作者姓名',
    `chain_hash`            VARCHAR(64)  DEFAULT NULL            COMMENT '链上哈希',
    `create_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`                VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `deleted`               TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='加工记录表';

-- ----------------------------
-- 32. 质检记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_quality_record`;
CREATE TABLE `trace_quality_record` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`            VARCHAR(50)  NOT NULL                COMMENT '批次编号',
    `processing_record_id` BIGINT      DEFAULT NULL            COMMENT '关联加工记录ID',
    `quality_date`        DATETIME     DEFAULT NULL            COMMENT '质检日期',
    `quality_result`      VARCHAR(50)  DEFAULT NULL            COMMENT '质检结果',
    `inspector`           VARCHAR(100) DEFAULT NULL            COMMENT '检测人员',
    `report_url`          VARCHAR(500) DEFAULT NULL            COMMENT '报告文件地址',
    `inspection_items`    VARCHAR(500) DEFAULT NULL            COMMENT '检测项目',
    `inspection_standard` VARCHAR(200) DEFAULT NULL            COMMENT '检测标准',
    `qualified`           TINYINT      DEFAULT 1               COMMENT '是否合格: 0不合格 1合格',
    `operator_id`         BIGINT       DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`       VARCHAR(100) DEFAULT NULL            COMMENT '操作者姓名',
    `chain_hash`          VARCHAR(64)  DEFAULT NULL            COMMENT '链上哈希',
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`              VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `deleted`             TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_processing_record_id` (`processing_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

-- ----------------------------
-- 33. 仓储/库存记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_storage_record`;
CREATE TABLE `trace_storage_record` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`         VARCHAR(50)  NOT NULL                COMMENT '批次编号',
    `quantity`         INT          DEFAULT 0               COMMENT '库存数量',
    `change_type`      VARCHAR(20)  DEFAULT NULL            COMMENT '库存变动类型: IN入库 OUT出库',
    `change_quantity`  INT          DEFAULT 0               COMMENT '变动数量',
    `after_quantity`   INT          DEFAULT 0               COMMENT '变动后库存',
    `change_time`      DATETIME     DEFAULT NULL            COMMENT '变动时间',
    `operator_name`    VARCHAR(100) DEFAULT NULL            COMMENT '操作人',
    `operator_id`      BIGINT       DEFAULT NULL            COMMENT '操作者ID',
    `related_record_id` BIGINT      DEFAULT NULL            COMMENT '关联业务ID',
    `chain_hash`       VARCHAR(64)  DEFAULT NULL            COMMENT '链上哈希',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`           VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `deleted`          TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓储/库存记录表';

-- ----------------------------
-- 34. 物流记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_logistics_record`;
CREATE TABLE `trace_logistics_record` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`              VARCHAR(50)  NOT NULL                COMMENT '批次编号',
    `logistics_company`     VARCHAR(200) DEFAULT NULL            COMMENT '物流公司',
    `transport_vehicle`     VARCHAR(100) DEFAULT NULL            COMMENT '运输车辆',
    `driver_name`           VARCHAR(50)  DEFAULT NULL            COMMENT '司机姓名',
    `driver_phone`          VARCHAR(20)  DEFAULT NULL            COMMENT '司机电话',
    `from_address`          VARCHAR(500) DEFAULT NULL            COMMENT '发货地址',
    `to_address`            VARCHAR(500) DEFAULT NULL            COMMENT '收货地址',
    `ship_time`             DATETIME     DEFAULT NULL            COMMENT '发货时间',
    `arrival_time`          DATETIME     DEFAULT NULL            COMMENT '到货时间',
    `transport_temperature` VARCHAR(50)  DEFAULT NULL            COMMENT '运输温度',
    `transport_humidity`    VARCHAR(50)  DEFAULT NULL            COMMENT '运输湿度',
    `logistics_status`      VARCHAR(50)  DEFAULT NULL            COMMENT '物流状态',
    `operator_id`           BIGINT       DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`         VARCHAR(100) DEFAULT NULL            COMMENT '操作者姓名',
    `chain_hash`            VARCHAR(64)  DEFAULT NULL            COMMENT '链上哈希',
    `create_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`                VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `deleted`               TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流记录表';

-- ----------------------------
-- 35. 销售记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_sales_record`;
CREATE TABLE `trace_sales_record` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `batch_no`      VARCHAR(50)   NOT NULL                COMMENT '批次编号',
    `seller`        VARCHAR(200)  DEFAULT NULL            COMMENT '销售商',
    `sales_region`  VARCHAR(200)  DEFAULT NULL            COMMENT '销售地区',
    `listing_time`  DATETIME      DEFAULT NULL            COMMENT '上架时间',
    `sales_price`   DECIMAL(18,2) DEFAULT NULL            COMMENT '销售价格',
    `stock_quantity` INT          DEFAULT 0               COMMENT '库存数量',
    `sales_status`  VARCHAR(50)   DEFAULT NULL            COMMENT '销售状态',
    `operator_id`   BIGINT        DEFAULT NULL            COMMENT '操作者ID',
    `operator_name` VARCHAR(100)  DEFAULT NULL            COMMENT '操作者姓名',
    `chain_hash`    VARCHAR(64)   DEFAULT NULL            COMMENT '链上哈希',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`        VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `deleted`       TINYINT       DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售记录表';

-- ----------------------------
-- 36. 溯源码表
-- ----------------------------
DROP TABLE IF EXISTS `trace_qrcode`;
CREATE TABLE `trace_qrcode` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '二维码ID',
    `qr_code`      VARCHAR(255) NOT NULL                COMMENT '二维码内容',
    `product_code` VARCHAR(50)  NOT NULL                COMMENT '产品编码',
    `batch_no`     VARCHAR(50)  NOT NULL                COMMENT '批次号',
    `trace_id`     VARCHAR(64)  DEFAULT NULL            COMMENT '溯源ID',
    `status`       TINYINT      DEFAULT 0               COMMENT '状态: 0未激活 1已激活 2已失效',
    `scan_count`   INT          DEFAULT 0               COMMENT '扫描次数',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_qr_code` (`qr_code`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源码表';

-- ----------------------------
-- 37. 公开查询日志表
-- ----------------------------
DROP TABLE IF EXISTS `trace_query_log`;
CREATE TABLE `trace_query_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `qr_code`     VARCHAR(255) DEFAULT NULL            COMMENT '溯源码',
    `batch_no`    VARCHAR(50)  DEFAULT NULL            COMMENT '批次号',
    `query_ip`    VARCHAR(50)  DEFAULT NULL            COMMENT '查询IP',
    `query_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '查询时间',
    `user_agent`  VARCHAR(500) DEFAULT NULL            COMMENT '用户代理',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_query_time` (`query_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公开查询日志表';

-- ----------------------------
-- 38. 哈希链头表
-- ----------------------------
DROP TABLE IF EXISTS `trace_chain_header`;
CREATE TABLE `trace_chain_header` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '链头ID',
    `chain_id`      VARCHAR(64)  NOT NULL                COMMENT '链ID',
    `chain_name`    VARCHAR(100) NOT NULL                COMMENT '链名称',
    `description`   VARCHAR(500) DEFAULT NULL            COMMENT '描述',
    `current_height` BIGINT      DEFAULT 0               COMMENT '当前高度',
    `latest_hash`   VARCHAR(64)  DEFAULT NULL            COMMENT '最新区块哈希',
    `status`        TINYINT      DEFAULT 0               COMMENT '状态: 0正常 1失效',
    `creator_id`    VARCHAR(64)  DEFAULT NULL            COMMENT '创建人ID',
    `creator_name`  VARCHAR(100) DEFAULT NULL            COMMENT '创建人姓名',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_chain_id` (`chain_id`),
    INDEX `idx_chain_name` (`chain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='哈希链头表';

-- ----------------------------
-- 39. 区块数据表
-- ----------------------------
DROP TABLE IF EXISTS `trace_chain_block`;
CREATE TABLE `trace_chain_block` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '区块ID',
    `chain_id`       VARCHAR(64)  NOT NULL                COMMENT '链ID',
    `block_hash`     VARCHAR(64)  NOT NULL                COMMENT '区块哈希',
    `previous_hash`  VARCHAR(64)  NOT NULL                COMMENT '前一区块哈希',
    `data_hash`      VARCHAR(64)  NOT NULL                COMMENT '数据哈希',
    `data_content`   TEXT                                  COMMENT '数据内容(JSON)',
    `nonce`          BIGINT       DEFAULT 0               COMMENT '随机数',
    `timestamp`      BIGINT       DEFAULT NULL            COMMENT '时间戳',
    `operator_id`    VARCHAR(64)  DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`  VARCHAR(100) DEFAULT NULL            COMMENT '操作者姓名',
    `operation_type` VARCHAR(50)  DEFAULT NULL            COMMENT '操作类型',
    `product_code`   VARCHAR(50)  DEFAULT NULL            COMMENT '产品编码',
    `batch_no`       VARCHAR(50)  DEFAULT NULL            COMMENT '批次号',
    `block_height`   INT          DEFAULT 0               COMMENT '区块高度',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_block_hash` (`block_hash`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块数据表';

-- ----------------------------
-- 40. 链验证记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_chain_verification`;
CREATE TABLE `trace_chain_verification` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '验证ID',
    `chain_id`            VARCHAR(64)  NOT NULL                COMMENT '链ID',
    `block_hash`          VARCHAR(64)  NOT NULL                COMMENT '区块哈希',
    `data_hash`           VARCHAR(64)  DEFAULT NULL            COMMENT '数据哈希',
    `original_data`       TEXT                                  COMMENT '原始数据',
    `verified`            TINYINT      DEFAULT 0               COMMENT '验证结果: 0未通过 1通过',
    `verification_result` VARCHAR(500) DEFAULT NULL            COMMENT '验证结果描述',
    `operator_id`         VARCHAR(64)  DEFAULT NULL            COMMENT '操作者ID',
    `operator_name`       VARCHAR(100) DEFAULT NULL            COMMENT '操作者姓名',
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_block_hash` (`block_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链验证记录表';

-- ----------------------------
-- 41. 溯源企业信息表（通过 user_id 关联 sys_user）
-- ----------------------------
DROP TABLE IF EXISTS `trace_enterprise_profile`;
CREATE TABLE `trace_enterprise_profile` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    `user_id`         BIGINT       NOT NULL                COMMENT '关联用户ID',
    `enterprise_name` VARCHAR(200) DEFAULT ''              COMMENT '企业名称',
    `license_no`      VARCHAR(100) DEFAULT ''              COMMENT '营业执照号',
    `address`         VARCHAR(300) DEFAULT ''              COMMENT '企业地址',
    `contact_person`  VARCHAR(50)  DEFAULT ''              COMMENT '联系人',
    `contact_phone`   VARCHAR(20)  DEFAULT ''              COMMENT '联系电话',
    `business_scope`  VARCHAR(500) DEFAULT ''             COMMENT '经营范围',
    `status`          TINYINT      DEFAULT 0              COMMENT '0待审核 1已认证 2已驳回',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源企业信息表';

-- =================================================================
-- 第四部分：农业技术推广业务表（knowledge_ 前缀）
-- =================================================================

-- ----------------------------
-- 42. 技术分类表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_category`;
CREATE TABLE `knowledge_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
    `code`        VARCHAR(30)  NOT NULL                 COMMENT '分类编码',
    `sort_order`  INT          DEFAULT 0                COMMENT '排序序号',
    `description` VARCHAR(200) DEFAULT ''               COMMENT '分类描述',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术分类表';

-- ----------------------------
-- 43. 文章标签表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_tag`;
CREATE TABLE `knowledge_tag` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`       VARCHAR(50) NOT NULL                 COMMENT '标签名称',
    `tag_type`   VARCHAR(50) DEFAULT ''               COMMENT '标签类型：作物/病虫害/技术/地区/季节/政策',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT     DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

-- ----------------------------
-- 44. 技术文章表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article`;
CREATE TABLE `knowledge_article` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '作者ID',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '分类ID',
    `crop_id`        BIGINT       DEFAULT NULL            COMMENT '关联作物ID',
    `crop_type`      VARCHAR(50)  DEFAULT NULL            COMMENT '作物类型',
    `region`         VARCHAR(100) DEFAULT NULL            COMMENT '地区',
    `title`          VARCHAR(200) NOT NULL                COMMENT '文章标题',
    `content`        LONGTEXT                              COMMENT '文章正文（富文本）',
    `summary`       VARCHAR(500)  DEFAULT ''              COMMENT '摘要',
    `cover_image`    VARCHAR(500) DEFAULT ''              COMMENT '封面图URL',
    `source`         VARCHAR(100) DEFAULT ''              COMMENT '文章来源名称',
    `source_url`     VARCHAR(500) DEFAULT ''              COMMENT '文章来源链接',
    `trusted_level`  VARCHAR(50)  DEFAULT 'normal'       COMMENT '可信等级：official/expert/normal',
    `status`         TINYINT      DEFAULT 0              COMMENT '0草稿 1待审核 2已发布 3已驳回 4已归档',
    `auditor_id`     BIGINT       DEFAULT NULL            COMMENT '审核人ID',
    `reject_reason`  VARCHAR(500) DEFAULT ''             COMMENT '驳回原因',
    `view_count`     INT          DEFAULT 0               COMMENT '浏览数',
    `like_count`     INT          DEFAULT 0               COMMENT '点赞数',
    `favorite_count` INT          DEFAULT 0               COMMENT '收藏数',
    `comment_count`  INT          DEFAULT 0               COMMENT '评论数',
    `published_at`   DATETIME     DEFAULT NULL            COMMENT '发布时间',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术文章表';

-- ----------------------------
-- 45. 文章标签关联表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article_tag`;
CREATE TABLE `knowledge_article_tag` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `article_id` BIGINT   NOT NULL                  COMMENT '文章ID',
    `tag_id`     BIGINT   NOT NULL                  COMMENT '标签ID',
    `weight`     DOUBLE   DEFAULT 1.0               COMMENT '标签权重',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT   DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- ----------------------------
-- 46. 文章评论表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article_comment`;
CREATE TABLE `knowledge_article_comment` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id` BIGINT        NOT NULL                COMMENT '文章ID',
    `user_id`    BIGINT        NOT NULL                COMMENT '评论者ID',
    `parent_id`  BIGINT        DEFAULT 0               COMMENT '父评论ID，0表示顶级评论',
    `content`    VARCHAR(1000) NOT NULL                COMMENT '评论内容',
    `status`     TINYINT       DEFAULT 0               COMMENT '0正常 1隐藏',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT       DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';

-- ----------------------------
-- 47. 文章点赞表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article_like`;
CREATE TABLE `knowledge_article_like` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '用户ID',
    `article_id` BIGINT   NOT NULL                 COMMENT '文章ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT  DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';

-- ----------------------------
-- 48. 文章收藏表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article_favorite`;
CREATE TABLE `knowledge_article_favorite` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '用户ID',
    `article_id` BIGINT   NOT NULL                 COMMENT '文章ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT  DEFAULT 0                COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏表';

-- ----------------------------
-- 49. 文章浏览记录表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_article_view_log`;
CREATE TABLE `knowledge_article_view_log` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `article_id` BIGINT       NOT NULL                COMMENT '文章ID',
    `user_id`    BIGINT       DEFAULT NULL            COMMENT '用户ID（未登录为NULL）',
    `ip`         VARCHAR(50)  DEFAULT ''              COMMENT '访问IP',
    `user_agent` VARCHAR(500) DEFAULT ''              COMMENT '浏览器UA',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章浏览记录表';

-- ----------------------------
-- 50. 问答问题表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_question`;
CREATE TABLE `knowledge_question` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '提问者ID',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '分类ID',
    `crop_id`        BIGINT       DEFAULT NULL            COMMENT '关联作物ID',
    `crop_type`      VARCHAR(50)  DEFAULT NULL            COMMENT '作物类型',
    `land_id`        BIGINT       DEFAULT NULL            COMMENT '关联地块ID',
    `batch_no`       VARCHAR(50)  DEFAULT NULL            COMMENT '生产批次号',
    `region`         VARCHAR(100) DEFAULT NULL            COMMENT '地区',
    `growth_stage`   VARCHAR(50)  DEFAULT NULL            COMMENT '生长阶段',
    `title`          VARCHAR(200) NOT NULL                COMMENT '问题标题',
    `content`        TEXT                                  COMMENT '问题描述',
    `image_urls`     TEXT                                  COMMENT '问题图片URL列表(JSON)',
    `status`         VARCHAR(20)  DEFAULT '0'             COMMENT '0待回答 1已解决 2已关闭 PENDING待处理 RESOLVED已解决',
    `view_count`     INT          DEFAULT 0               COMMENT '浏览数',
    `answer_count`   INT          DEFAULT 0               COMMENT '回答数',
    `best_answer_id` BIGINT       DEFAULT NULL            COMMENT '采纳的最佳回答ID',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答问题表';

-- ----------------------------
-- 51. 问答回答表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_answer`;
CREATE TABLE `knowledge_answer` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '回答ID',
    `question_id` BIGINT   NOT NULL                 COMMENT '问题ID',
    `user_id`     BIGINT   NOT NULL                 COMMENT '回答者ID',
    `content`     TEXT     NOT NULL                 COMMENT '回答内容',
    `is_accepted` TINYINT  DEFAULT 0               COMMENT '是否被采纳: 0否 1是',
    `like_count`  INT      DEFAULT 0               COMMENT '点赞数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT  DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_accepted` (`is_accepted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答回答表';

-- ----------------------------
-- 52. 智能提问问题表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_smart_question`;
CREATE TABLE `knowledge_smart_question` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '农户用户ID',
    `title`         VARCHAR(100)  NOT NULL                COMMENT '问题标题',
    `crop_type`      VARCHAR(50)  DEFAULT NULL            COMMENT '作物类型',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '问题分类ID',
    `description`   TEXT                                   COMMENT '问题描述',
    `region`         VARCHAR(100) DEFAULT NULL            COMMENT '地区',
    `growth_stage`   VARCHAR(50)  DEFAULT NULL            COMMENT '生长阶段',
    `question_status` TINYINT     DEFAULT 0               COMMENT '0待识别 1YOLO已识别 2Agent已回答 3已回复 4待专家审核 5专家已回复 6已解决 7已关闭',
    `yolo_status`    TINYINT      DEFAULT 0               COMMENT '0未识别 1成功 2失败 3无需识别',
    `agent_status`   TINYINT      DEFAULT 0               COMMENT '0未调用 1已生成 2失败 3评分通过 4评分未通过',
    `view_count`     INT          DEFAULT 0               COMMENT '浏览量',
    `answer_count`   INT          DEFAULT 0               COMMENT '回答数',
    `best_answer_id` BIGINT       DEFAULT NULL            COMMENT '最佳答案ID',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_question_status` (`question_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能提问问题表';

-- ----------------------------
-- 53. 智能提问图片表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_question_image`;
CREATE TABLE `knowledge_question_image` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `question_id`   BIGINT       NOT NULL                COMMENT '问题ID',
    `image_url`     VARCHAR(255) NOT NULL                COMMENT '图片地址',
    `original_name` VARCHAR(255) DEFAULT NULL            COMMENT '原始文件名',
    `file_size`     BIGINT       DEFAULT NULL            COMMENT '文件大小',
    `file_type`     VARCHAR(20)  DEFAULT NULL            COMMENT '文件类型',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题图片表';

-- ----------------------------
-- 54. YOLO识别结果表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_yolo_result`;
CREATE TABLE `knowledge_yolo_result` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '识别结果ID',
    `question_id`     BIGINT        NOT NULL                COMMENT '问题ID',
    `image_id`        BIGINT        DEFAULT NULL            COMMENT '图片ID',
    `model_id`        VARCHAR(100)  DEFAULT 'pest-detection-yolov8/1' COMMENT '模型ID',
    `disease_name`    VARCHAR(100)  DEFAULT NULL            COMMENT '识别出的病虫害名称',
    `confidence`      DECIMAL(5,4)  DEFAULT NULL            COMMENT '最高置信度',
    `prediction_json` TEXT                                    COMMENT '完整识别结果JSON',
    `result_image_url` VARCHAR(255) DEFAULT NULL            COMMENT '带检测框结果图地址',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`         TINYINT       DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_image_id` (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='YOLO识别结果表';

-- ----------------------------
-- 55. Agent诊断回答表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_smart_answer`;
CREATE TABLE `knowledge_smart_answer` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Agent回答ID',
    `question_id`       BIGINT       NOT NULL               COMMENT '问题ID',
    `diagnosis`        VARCHAR(255)  DEFAULT NULL           COMMENT '初步诊断',
    `cause_analysis`   TEXT                                 COMMENT '原因分析',
    `treatment_suggestion` TEXT                              COMMENT '处理建议',
    `prevention_advice` TEXT                                  COMMENT '预防措施',
    `risk_level`        VARCHAR(50)  DEFAULT NULL           COMMENT '风险等级',
    `need_expert_review` TINYINT     DEFAULT 1              COMMENT '是否需要专家审核',
    `answer_content`    TEXT                                  COMMENT '完整回答内容',
    `score`             INT          DEFAULT 0              COMMENT '回答评分',
    `returned_to_farmer` TINYINT     DEFAULT 0              COMMENT '是否已自动返回农户',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent诊断回答表';

-- ----------------------------
-- 56. 回答评分表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_smart_score`;
CREATE TABLE `knowledge_smart_score` (
    `id`                BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评分ID',
    `question_id`       BIGINT   NOT NULL               COMMENT '问题ID',
    `agent_answer_id`   BIGINT   NOT NULL               COMMENT 'Agent回答ID',
    `yolo_score`        INT      DEFAULT 0              COMMENT 'YOLO置信度得分',
    `crop_match_score`  INT      DEFAULT 0              COMMENT '作物匹配得分',
    `completeness_score` INT     DEFAULT 0              COMMENT '完整性得分',
    `safety_score`      INT      DEFAULT 0              COMMENT '安全性得分',
    `clarity_score`     INT      DEFAULT 0              COMMENT '清晰度得分',
    `total_score`       INT      DEFAULT 0              COMMENT '总分',
    `score_reason`      TEXT                             COMMENT '评分原因',
    `can_return_to_farmer` TINYINT DEFAULT 0            COMMENT '是否可以返回农户',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`           TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_agent_answer_id` (`agent_answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答评分表';

-- ----------------------------
-- 57. 专家回答表（智能问答模块）
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_expert_answer`;
CREATE TABLE `knowledge_expert_answer` (
    `id`                    BIGINT   NOT NULL AUTO_INCREMENT COMMENT '专家回答ID',
    `question_id`           BIGINT   NOT NULL               COMMENT '问题ID',
    `expert_id`             BIGINT   NOT NULL               COMMENT '专家ID',
    `answer_content`        TEXT     NOT NULL               COMMENT '专家回答内容',
    `reference_agent_answer` TINYINT DEFAULT 0              COMMENT '是否参考Agent回答',
    `is_adopted`            TINYINT  DEFAULT 0              COMMENT '是否被采纳',
    `create_time`           DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`               TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_expert_id` (`expert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专家回答表';

-- ----------------------------
-- 58. 讲座表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_lecture`;
CREATE TABLE `knowledge_lecture` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '讲座ID',
    `user_id`            BIGINT       NOT NULL               COMMENT '发布者ID',
    `title`              VARCHAR(200) NOT NULL               COMMENT '讲座标题',
    `content`           TEXT                                  COMMENT '讲座详情',
    `summary`           VARCHAR(500)  DEFAULT ''             COMMENT '摘要',
    `cover_image`        VARCHAR(500) DEFAULT ''             COMMENT '封面图',
    `speaker`            VARCHAR(100) DEFAULT ''             COMMENT '主讲人',
    `speaker_title`      VARCHAR(100) DEFAULT ''             COMMENT '主讲人职称',
    `lecture_time`       DATETIME     NOT NULL               COMMENT '讲座时间',
    `location`           VARCHAR(200) DEFAULT ''             COMMENT '讲座地点/线上链接',
    `max_participants`   INT          DEFAULT 0              COMMENT '最大报名人数，0表示不限',
    `current_participants` INT        DEFAULT 0              COMMENT '当前报名人数',
    `status`             TINYINT      DEFAULT 0              COMMENT '0草稿 1报名中 2报名截止 3已结束',
    `registration_deadline` DATETIME  DEFAULT NULL           COMMENT '报名截止时间',
    `create_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_lecture_time` (`lecture_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讲座通知表';

-- ----------------------------
-- 59. 讲座报名表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_lecture_reg`;
CREATE TABLE `knowledge_lecture_reg` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `lecture_id`  BIGINT   NOT NULL               COMMENT '讲座ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '报名者ID',
    `status`      TINYINT  DEFAULT 0              COMMENT '0已报名 1已签到 2已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lecture_user` (`lecture_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讲座报名表';

-- ----------------------------
-- 60. 农技视频表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_video`;
CREATE TABLE `knowledge_video` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '视频ID',
    `title`           VARCHAR(100)  NOT NULL               COMMENT '视频标题',
    `description`     TEXT                                  COMMENT '视频简介',
    `category_id`     BIGINT        DEFAULT NULL           COMMENT '视频分类ID',
    `category_name`   VARCHAR(50)   DEFAULT NULL           COMMENT '分类名称',
    `crop_type`       VARCHAR(50)   DEFAULT NULL           COMMENT '作物类型',
    `tags`            VARCHAR(255)  DEFAULT NULL           COMMENT '标签，逗号分隔',
    `file_name`       VARCHAR(255)  NOT NULL               COMMENT '视频文件名',
    `video_url`       VARCHAR(255)  NOT NULL               COMMENT '视频访问地址',
    `cover_url`       VARCHAR(255)  DEFAULT NULL           COMMENT '封面图地址',
    `duration`        INT           DEFAULT 0              COMMENT '视频时长，单位秒',
    `view_count`      INT           DEFAULT 0              COMMENT '播放量',
    `like_count`      INT           DEFAULT 0              COMMENT '点赞数',
    `favorite_count`  INT           DEFAULT 0              COMMENT '收藏数',
    `recommend_weight` INT          DEFAULT 0              COMMENT '人工推荐权重',
    `status`          TINYINT       DEFAULT 1              COMMENT '1启用 0禁用',
    `create_user_id`  BIGINT        DEFAULT NULL           COMMENT '创建人ID',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT       DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_name` (`file_name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农技视频表';

-- ----------------------------
-- 61. 视频收藏表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_video_favorite`;
CREATE TABLE `knowledge_video_favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `video_id`    BIGINT   NOT NULL               COMMENT '视频ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频收藏表';

-- ----------------------------
-- 62. 视频点赞表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_video_like`;
CREATE TABLE `knowledge_video_like` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `video_id`    BIGINT   NOT NULL               COMMENT '视频ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频点赞表';

-- ----------------------------
-- 63. 专家信息档案表（通过 user_id 关联 sys_user）
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_expert_profile`;
CREATE TABLE `knowledge_expert_profile` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '专家档案ID',
    `user_id`     BIGINT       NOT NULL               COMMENT '关联用户ID',
    `type`        TINYINT      DEFAULT 0              COMMENT '0农业 1渔业',
    `industry`    VARCHAR(100) DEFAULT ''             COMMENT '行业',
    `major`       VARCHAR(100) DEFAULT ''             COMMENT '专长',
    `wechat`      VARCHAR(50)  DEFAULT NULL           COMMENT '微信',
    `qq`          VARCHAR(50)  DEFAULT NULL           COMMENT 'QQ',
    `introduction` TEXT                                COMMENT '专家简介',
    `status`      TINYINT      DEFAULT 0              COMMENT '0待审核 1已认证 2已驳回',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专家信息档案表';

-- ----------------------------
-- 64. 用户行为追踪表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_user_behavior`;
CREATE TABLE `knowledge_user_behavior` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '行为ID',
    `user_id`       BIGINT       NOT NULL               COMMENT '用户ID',
    `article_id`    BIGINT       DEFAULT NULL           COMMENT '文章ID',
    `video_id`      BIGINT       DEFAULT NULL           COMMENT '视频ID',
    `target_type`   VARCHAR(20)  DEFAULT 'ARTICLE'     COMMENT '目标类型：ARTICLE/VIDEO',
    `behavior_type` VARCHAR(50)  NOT NULL               COMMENT '行为: view/like/collect/comment/question/dislike',
    `stay_seconds`  INT          DEFAULT 0              COMMENT '停留时间',
    `weight`        DOUBLE       DEFAULT 1.0            COMMENT '行为权重',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`       TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为追踪表';

-- ----------------------------
-- 65. 用户兴趣画像表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_user_profile`;
CREATE TABLE `knowledge_user_profile` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '画像ID',
    `user_id`          BIGINT        NOT NULL               COMMENT '用户ID',
    `interest_tags`    TEXT                                  COMMENT '兴趣标签JSON',
    `crop_preference`  VARCHAR(255)  DEFAULT NULL           COMMENT '偏好作物',
    `tech_preference`  VARCHAR(255)  DEFAULT NULL           COMMENT '偏好技术',
    `region_preference` VARCHAR(255) DEFAULT NULL           COMMENT '偏好地区',
    `profile_text`     TEXT                                  COMMENT '用户画像自然语言描述',
    `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT       DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣画像表';

-- ----------------------------
-- 66. 推荐审计日志表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_recommend_log`;
CREATE TABLE `knowledge_recommend_log` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`    BIGINT        NOT NULL               COMMENT '用户ID',
    `article_id` BIGINT        NOT NULL               COMMENT '推荐文章ID',
    `score`      DOUBLE        DEFAULT NULL           COMMENT '推荐得分',
    `reason`     TEXT                                  COMMENT '推荐理由',
    `strategy`   VARCHAR(100)  DEFAULT NULL           COMMENT '策略: tag_match/vector_sim/hot/cold_start/trust',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT       DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐审计日志表';

-- ----------------------------
-- 67. 病虫害知识表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_pest`;
CREATE TABLE `knowledge_pest` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '病虫害ID',
    `name`         VARCHAR(100) NOT NULL               COMMENT '病虫害名称',
    `category`     VARCHAR(50)  DEFAULT NULL           COMMENT '分类: 虫害/病害',
    `affected_crops` VARCHAR(500) DEFAULT NULL         COMMENT '受害作物',
    `symptoms`     TEXT                                COMMENT '症状描述',
    `cause`        TEXT                                COMMENT '发病原因',
    `prevention`   TEXT                                COMMENT '防治措施',
    `img_url`      VARCHAR(500) DEFAULT NULL           COMMENT '示例图片',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病虫害知识表';

-- ----------------------------
-- 68. 用户作物兴趣关联表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_user_crop_interest`;
CREATE TABLE `knowledge_user_crop_interest` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id`    BIGINT   NOT NULL               COMMENT '用户ID',
    `crop_id`    BIGINT   NOT NULL               COMMENT '作物ID',
    `weight`     DOUBLE   DEFAULT 1.0            COMMENT '兴趣权重',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`    TINYINT  DEFAULT 0              COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_crop` (`user_id`, `crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户作物兴趣关联表';

-- =================================================================
-- 第五部分：初始化数据
-- =================================================================

-- ----------------------------
-- 初始化部门
-- ----------------------------
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `status`, `create_by`, `create_time`) VALUES
(100, 0, '0', '智慧农业平台', 0, '管理员', '15888888888', 0, 'admin', NOW()),
(101, 100, '0,100', '农场管理部', 1, '农场主管', '15888888889', 0, 'admin', NOW()),
(102, 100, '0,100', '溯源管理部', 2, '溯源主管', '15888888890', 0, 'admin', NOW()),
(103, 100, '0,100', '技术推广部', 3, '技术主管', '15888888891', 0, 'admin', NOW()),
(104, 100, '0,100', '审核管理部', 4, '审核主管', '15888888892', 0, 'admin', NOW());

-- ----------------------------
-- 初始化角色（6个角色）
-- ----------------------------
INSERT INTO `sys_role` (`id`, `code`, `name`, `role_key`, `role_sort`, `status`, `description`, `create_by`, `create_time`) VALUES
(1, 'ADMIN',       '超级管理员',    'admin',       1, 0, '系统超级管理员，拥有所有权限',       'admin', NOW()),
(2, 'FARM_ADMIN',  '农场管理员',    'farm_admin',  2, 0, '农场管理者，负责农场生产管理',       'admin', NOW()),
(3, 'TRACE_ADMIN', '溯源企业用户',  'trace_admin', 3, 0, '溯源企业用户，负责产品溯源管理',     'admin', NOW()),
(4, 'EXPERT',      '农技专家',      'expert',      4, 0, '农业技术专家，发布文章、回答问题',   'admin', NOW()),
(5, 'FARMER',      '普通农户',      'farmer',      5, 0, '普通农户用户，浏览知识、提问',       'admin', NOW()),
(6, 'CONSUMER',    '消费者',        'consumer',    6, 0, '消费者用户，只查看公开溯源信息',             'admin', NOW());

-- ----------------------------
-- 初始化用户（BCrypt密码: 123456 → $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2）
-- ----------------------------
INSERT INTO `sys_user` (`id`, `dept_id`, `username`, `password`, `nickname`, `user_type`, `phone`, `email`, `status`, `create_by`, `create_time`) VALUES
(1,  100, 'admin',       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员',     0, '13800138000', 'admin@agri.com',      0, 'admin', NOW()),
(2,  101, 'farm_admin',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '农场管理员',     1, '13800138001', 'farm@agri.com',       0, 'admin', NOW()),
(3,  102, 'trace_admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '溯源企业管理员', 2, '13800138002', 'trace@agri.com',      0, 'admin', NOW()),
(4,  103, 'expert',      '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '农业专家',       3, '13800138003', 'expert@agri.com',     0, 'admin', NOW()),
(5,  100, 'farmer',      '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '普通农户',       4, '13800138004', 'farmer@agri.com',     0, 'admin', NOW()),
(6,  104, 'consumer',    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '消费者',         5, '13800138005', 'consumer@agri.com',   0, 'admin', NOW());

-- ----------------------------
-- 初始化用户角色关联
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin → ADMIN
(2, 2), -- farm_admin → FARM_ADMIN
(3, 3), -- trace_admin → TRACE_ADMIN
(4, 4), -- expert → EXPERT
(5, 5), -- farmer → FARMER
(6, 6); -- consumer → CONSUMER

-- ----------------------------
-- 初始化菜单（前端菜单结构）
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
-- 一级菜单
(1,  '首页驾驶舱',     0, 1, '/dashboard',         '',                   'M', 0, 0, '',           'dashboard',    'admin', NOW()),
(2,  '农场生产管理',   0, 2, '/farm',              '',                   'M', 0, 0, '',           'farm',         'admin', NOW()),
(3,  '农产品溯源管理', 0, 3, '/trace',             '',                   'M', 0, 0, '',           'trace',        'admin', NOW()),
(4,  '农业技术推广',   0, 4, '/knowledge',         '',                   'M', 0, 0, '',           'knowledge',    'admin', NOW()),
(5,  '系统管理',       0, 5, '/system',            '',                   'M', 0, 0, '',           'system',       'admin', NOW()),
-- 首页驾驶舱子菜单
(10, '平台概览',       1, 1, 'overview',           'dashboard/index',    'C', 0, 0, '',           'chart',        'admin', NOW()),
-- 农场生产管理子菜单
(20, '农场管理',       2, 1, 'farm',               'farm/farm/index',    'C', 0, 0, 'farm:farm:list',       'enterprise', 'admin', NOW()),
(21, '地块管理',       2, 2, 'land',               'farm/land/index',    'C', 0, 0, 'farm:land:list',       'grid',       'admin', NOW()),
(22, '作物管理',       2, 3, 'crop',               'farm/crop/index',    'C', 0, 0, 'farm:crop:list',       'crop',       'admin', NOW()),
(23, '种植计划',       2, 4, 'planting',            'farm/planting/index','C', 0, 0, 'farm:planting:list',   'calendar',   'admin', NOW()),
(24, '农事任务',       2, 5, 'task',               'farm/task/index',    'C', 0, 0, 'farm:task:list',       'task',       'admin', NOW()),
(25, '设备管理',       2, 6, 'device',             'farm/device/index',  'C', 0, 0, 'farm:device:list',     'cpu',        'admin', NOW()),
(26, '环境监测',       2, 7, 'environment',         'farm/environment/index','C',0,0,'farm:environment:list','monitor','admin', NOW()),
(27, '预警管理',       2, 8, 'warning',            'farm/warning/index', 'C', 0, 0, 'farm:warning:list',    'bell',       'admin', NOW()),
-- 溯源管理子菜单
(30, '产品管理',       3, 1, 'product',            'trace/product/index','C', 0, 0, 'trace:product:list',   'goods',      'admin', NOW()),
(31, '批次管理',       3, 2, 'batch',              'trace/batch/index',  'C', 0, 0, 'trace:batch:list',     'box',        'admin', NOW()),
(32, '生产记录',       3, 3, 'production',          'trace/production/index','C',0,0,'trace:production:list','plant',   'admin', NOW()),
(33, '加工记录',       3, 4, 'processing',          'trace/processing/index','C',0,0,'trace:processing:list','process','admin', NOW()),
(34, '质检记录',       3, 5, 'quality',            'trace/quality/index','C', 0, 0, 'trace:quality:list',   'check',      'admin', NOW()),
(35, '仓储记录',       3, 6, 'storage',            'trace/storage/index', 'C', 0, 0, 'trace:storage:list',   'warehouse',  'admin', NOW()),
(36, '物流记录',       3, 7, 'logistics',           'trace/logistics/index','C',0,0,'trace:logistics:list','truck',     'admin', NOW()),
(37, '销售记录',       3, 8, 'sales',              'trace/sales/index',  'C', 0, 0, 'trace:sales:list',     'sale',       'admin', NOW()),
(38, '溯源码管理',     3, 9, 'qrcode',             'trace/qrcode/index', 'C', 0, 0, 'trace:qrcode:list',    'qrcode',     'admin', NOW()),
(39, '区块链存证',     3,10, 'chain',              'trace/chain/index',  'C', 0, 0, 'trace:chain:list',     'link',       'admin', NOW()),
(49, '公开溯源',       3,11, 'public',             'trace/public',       'C', 0, 0, 'trace:public:list',    'search',     'admin', NOW()),
-- 农业技术推广子菜单
(40, '农技文章',       4, 1, 'article',            'knowledge/article/index','C',0,0,'knowledge:article:list','document','admin', NOW()),
(41, '技术分类',       4, 2, 'category',           'knowledge/category/index','C',0,0,'knowledge:category:list','folder','admin', NOW()),
(42, '问答社区',       4, 3, 'question',            'knowledge/question/index','C',0,0,'knowledge:question:list','chat','admin', NOW()),
(43, '智能问答',       4, 4, 'smartqa',            'knowledge/smartqa/index','C',0,0,'knowledge:smartqa:list','cpu','admin', NOW()),
(44, '专家管理',       4, 5, 'expert',             'knowledge/expert/index','C',0,0,'knowledge:expert:list','user','admin', NOW()),
(45, '农技讲座',       4, 6, 'lecture',             'knowledge/lecture/index','C',0,0,'knowledge:lecture:list','video','admin', NOW()),
(46, '农技视频',       4, 7, 'video',              'knowledge/video/index','C', 0, 0, 'knowledge:video:list', 'film', 'admin', NOW()),
(47, '病虫害知识',     4, 8, 'pest',               'knowledge/pest/index','C', 0, 0, 'knowledge:pest:list',   'warning', 'admin', NOW()),
(48, '推荐服务',       4, 9, 'recommend',           'knowledge/recommend/index','C',0,0,'knowledge:recommend:list','star','admin', NOW()),
-- 系统管理子菜单
(50, '用户管理',       5, 1, 'user',               'system/user/index',  'C', 0, 0, 'system:user:list',     'peoples',    'admin', NOW()),
(51, '角色管理',       5, 2, 'role',               'system/role/index',  'C', 0, 0, 'system:role:list',     'role',       'admin', NOW()),
(52, '菜单管理',       5, 3, 'menu',               'system/menu/index',  'C', 0, 0, 'system:menu:list',     'tree-table', 'admin', NOW()),
(53, '字典管理',       5, 4, 'dict',               'system/dict/index',  'C', 0, 0, 'system:dict:list',     'dict',       'admin', NOW()),
(54, '系统配置',       5, 5, 'config',             'system/config/index','C', 0, 0, 'system:config:list',   'settings',   'admin', NOW()),
(55, '操作日志',       5, 6, 'log',                'system/log/index',   'C', 0, 0, 'system:log:list',      'log',        'admin', NOW()),
-- 按钮级权限
(101, '用户新增',      50, 1, '', '', 'F', 0, 0, 'system:user:add',    '#', 'admin', NOW()),
(102, '用户修改',      50, 2, '', '', 'F', 0, 0, 'system:user:edit',   '#', 'admin', NOW()),
(103, '用户删除',      50, 3, '', '', 'F', 0, 0, 'system:user:remove', '#', 'admin', NOW()),
(111, '角色新增',      51, 1, '', '', 'F', 0, 0, 'system:role:add',    '#', 'admin', NOW()),
(112, '角色修改',      51, 2, '', '', 'F', 0, 0, 'system:role:edit',   '#', 'admin', NOW()),
(113, '角色删除',      51, 3, '', '', 'F', 0, 0, 'system:role:remove', '#', 'admin', NOW()),
(121, '文章新增',      40, 1, '', '', 'F', 0, 0, 'knowledge:article:add',  '#', 'admin', NOW()),
(122, '文章审核',      40, 2, '', '', 'F', 0, 0, 'knowledge:article:audit','#', 'admin', NOW()),
(131, '产品新增',      30, 1, '', '', 'F', 0, 0, 'trace:product:add',  '#', 'admin', NOW()),
(141, '农场新增',      20, 1, '', '', 'F', 0, 0, 'farm:farm:add',      '#', 'admin', NOW());

-- ----------------------------
-- 初始化角色菜单关联
-- ----------------------------
-- 超级管理员(ADMIN): 拥有所有菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu;

-- 农场管理员(FARM_ADMIN): 首页 + 农场生产管理
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2,1),(2,2),(2,10),(2,20),(2,21),(2,22),(2,23),(2,24),(2,25),(2,26),(2,27),(2,141);

-- 溯源企业用户(TRACE_ADMIN): 首页 + 溯源管理
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3,1),(3,3),(3,10),(3,30),(3,31),(3,32),(3,33),(3,34),(3,35),(3,36),(3,37),(3,38),(3,39),(3,131);

-- 农技专家(EXPERT): 首页 + 技术推广(除推荐服务)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4,1),(4,4),(4,10),(4,40),(4,41),(4,42),(4,43),(4,44),(4,45),(4,46),(4,47),(4,121),(4,122);

-- 普通农户(FARMER): 首页 + 技术推广(只读)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(5,1),(5,4),(5,10),(5,40),(5,41),(5,42),(5,43),(5,45),(5,46),(5,47);

-- 消费者(CONSUMER): 首页 + 公开溯源
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(6,1),(6,3),(6,49);

-- ----------------------------
-- 初始化技术分类
-- ----------------------------
INSERT INTO `knowledge_category` (`name`, `code`, `sort_order`, `description`) VALUES
('种植技术', 'PLANTING',     1, '农作物种植相关技术'),
('养殖技术', 'BREEDING',     2, '畜牧水产养殖技术'),
('植保',     'PLANT_PROTECT',3, '植物保护与病虫害防治'),
('土肥',     'SOIL_FERT',    4, '土壤改良与肥料使用'),
('政策法规', 'POLICY',       5, '农业相关政策法规'),
('线上讲座', 'LECTURE',      6, '专家线上讲座通知');

-- ----------------------------
-- 初始化作物字典
-- ----------------------------
INSERT INTO `farm_crop` (`name`, `code`, `sort_order`, `type`) VALUES
('水稻', 'RICE',   1, 0),
('小麦', 'WHEAT',  2, 0),
('玉米', 'CORN',   3, 0),
('大豆', 'SOYBEAN',4, 0),
('苹果', 'APPLE',  5, 0),
('柑橘', 'CITRUS', 6, 0),
('草莓', 'STRAWBERRY', 7, 0),
('蔬菜', 'VEGETABLE', 8, 0),
('鱼类', 'FISH',    9, 1),
('虾类', 'SHRIMP', 10, 1);

-- ----------------------------
-- 初始化设备定义
-- ----------------------------
INSERT INTO `farm_device` (`id`, `type`, `device_num`, `name`, `device_type`, `need_statistics`, `park_device`, `img`) VALUES
(1, 0, '01', '空气温度',   1, 1, 0, 'icon-1.png'),
(2, 0, '02', '空气湿度',   1, 1, 0, 'icon-2.png'),
(3, 0, '03', 'CO2浓度',    1, 1, 0, 'icon-3.png'),
(4, 0, '04', '光照强度',   1, 1, 0, 'icon-4.png'),
(5, 0, '05', '土壤温度',   1, 1, 0, 'icon-5.png'),
(6, 0, '06', '土壤水分',   1, 1, 0, 'icon-6.png'),
(61, 0,'61', '水泵',        0, 0, 0, 'icon-61.png'),
(62, 0,'62', '天窗',        0, 0, 0, 'icon-62.png'),
(31, 1,'31', '鱼池水温',    1, 1, 0, 'icon-31.png'),
(32, 1,'32', '溶氧含量',    1, 1, 0, 'icon-32.png'),
(71, 1,'71', '进水开关',    0, 0, 0, 'icon-71.png'),
(74, 1,'74', '增氧开关',    0, 0, 0, 'icon-74.png');

-- ----------------------------
-- 初始化敏感词
-- ----------------------------
INSERT INTO `sys_sensitive_word` (`word`, `level`) VALUES
('虚假宣传', 2),
('违禁药',   2),
('违规广告', 1);

-- ----------------------------
-- 初始化字典数据
-- ----------------------------
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(1, '用户类型', 'sys_user_type', 0, 'admin', NOW()),
(2, '文章状态', 'article_status', 0, 'admin', NOW()),
(3, '地块类型', 'land_type', 0, 'admin', NOW()),
(4, '设备类型', 'device_type', 0, 'admin', NOW());

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(1, '超级管理员',   '0', 'sys_user_type', 0, 'admin', NOW()),
(2, '农场管理员',   '1', 'sys_user_type', 0, 'admin', NOW()),
(3, '溯源企业用户', '2', 'sys_user_type', 0, 'admin', NOW()),
(4, '农技专家',     '3', 'sys_user_type', 0, 'admin', NOW()),
(5, '普通农户',     '4', 'sys_user_type', 0, 'admin', NOW()),
(6, '消费者',       '5', 'sys_user_type', 0, 'admin', NOW()),
(7, '草稿',     '0', 'article_status', 0, 'admin', NOW()),
(8, '待审核',   '1', 'article_status', 0, 'admin', NOW()),
(9, '已发布',   '2', 'article_status', 0, 'admin', NOW()),
(10,'已驳回',   '3', 'article_status', 0, 'admin', NOW()),
(11,'大棚',     '0', 'land_type', 0, 'admin', NOW()),
(12,'鱼塘',     '1', 'land_type', 0, 'admin', NOW()),
(13,'大田',     '2', 'land_type', 0, 'admin', NOW()),
(14,'仓库',     '3', 'land_type', 0, 'admin', NOW()),
(15,'农业设备', '0', 'device_type', 0, 'admin', NOW()),
(16,'渔业设备', '1', 'device_type', 0, 'admin', NOW()),
(17,'大田设备', '2', 'device_type', 0, 'admin', NOW()),
(18,'仓库设备', '3', 'device_type', 0, 'admin', NOW());

-- ----------------------------
-- 初始化系统配置
-- ----------------------------
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`) VALUES
('系统名称', 'sys.name', '智慧农业综合服务平台', 0, 'admin', NOW()),
('系统版本', 'sys.version', '1.0.0', 0, 'admin', NOW()),
('文件上传路径', 'sys.file.path', './runtime/files', 0, 'admin', NOW()),
('溯源码前缀', 'sys.qrcode.prefix', 'AGRI', 0, 'admin', NOW());

-- ----------------------------
-- 初始化示例农场数据
-- ----------------------------
INSERT INTO `farm_enterprise` (`id`, `name`, `address`, `contact_name`, `contact_phone`, `status`) VALUES
(1, '智慧农业示范农场', '宁夏银川市贺兰县', '张三', '13900001111', 0);

INSERT INTO `farm_land` (`id`, `enterprise_id`, `number`, `type`, `display_name`, `area`, `location`) VALUES
(1, 1, 1, 0, '1号大棚', 500.00, '园区东区'),
(2, 1, 2, 0, '2号蔬菜大棚', 600.00, '园区东区'),
(3, 1, 1, 2, '1号大田', 2000.00, '园区南区'),
(4, 1, 1, 1, '1号鱼塘', 800.00, '园区北区'),
(5, 1, 1, 3, '1号仓库', 300.00, '园区西区');

-- ----------------------------
-- 初始化示例溯源产品
-- ----------------------------
INSERT INTO `trace_product` (`id`, `product_code`, `product_name`, `category`, `specification`, `unit`, `origin`, `enterprise_id`, `farm_id`, `crop_id`) VALUES
(1, 'P20240001', '有机草莓', '水果', '500g/盒', '盒', '宁夏银川', 1, 1, 7),
(2, 'P20240002', '优质苹果', '水果', '5kg/箱', '箱', '宁夏银川', 1, 3, 5);

INSERT INTO `trace_batch` (`id`, `batch_no`, `product_code`, `product_name`, `quantity`, `unit`, `production_date`, `farm_id`, `land_id`, `crop_id`, `status`) VALUES
(1, 'B202406001', 'P20240001', '有机草莓', 500.0000, '盒', '2024-06-01', 1, 1, 7, 0),
(2, 'B202406002', 'P20240002', '优质苹果', 200.0000, '箱', '2024-06-15', 1, 3, 5, 0);

-- ----------------------------
-- 初始化示例专家信息
-- ----------------------------
INSERT INTO `knowledge_expert_profile` (`id`, `user_id`, `type`, `industry`, `major`, `status`) VALUES
(1, 4, 0, '种植业', '杂交水稻、果树栽培', 1);

-- ----------------------------
-- 初始化病虫害知识
-- ----------------------------
INSERT INTO `knowledge_pest` (`id`, `name`, `category`, `affected_crops`, `symptoms`, `cause`, `prevention`) VALUES
(1, '稻瘟病', '病害', '水稻', '叶片出现梭形病斑，边缘褐色，中间灰白色', '高温高湿环境，病菌孢子传播', '选用抗病品种，合理施肥，药剂防治'),
(2, '蚜虫',   '虫害', '小麦、玉米、蔬菜', '叶片卷曲，生长受阻，分泌蜜露', '干旱少雨，天敌减少', '生物防治，黄色粘虫板，药剂喷洒');

-- ----------------------------
-- 初始化示例农技文章
-- ----------------------------
INSERT INTO `knowledge_article` (`id`, `user_id`, `category_id`, `crop_id`, `title`, `content`, `summary`, `status`, `published_at`) VALUES
(1, 4, 1, 1, '水稻高产栽培技术要点', '水稻高产栽培需要注意品种选择、育秧、水肥管理和病虫害防治等关键环节。本文详细介绍了各环节的技术要点和注意事项。', '水稻高产栽培技术要点介绍', 2, NOW()),
(2, 4, 3, 5, '苹果常见病虫害防治指南', '苹果种植过程中常见的病虫害包括轮纹病、炭疽病、蚜虫等。本文提供了系统的防治方案。', '苹果病虫害防治方法总结', 2, NOW());

-- ----------------------------
-- RAG 知识检索表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_search_log`;
DROP TABLE IF EXISTS `knowledge_keyword`;
DROP TABLE IF EXISTS `knowledge_document_chunk`;
DROP TABLE IF EXISTS `knowledge_document`;

CREATE TABLE `knowledge_document` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文档ID',
    `article_id`  BIGINT       DEFAULT NULL             COMMENT '来源文章ID',
    `title`       VARCHAR(200) NOT NULL                 COMMENT '文档标题',
    `source`      VARCHAR(255) DEFAULT NULL             COMMENT '来源说明',
    `doc_type`    VARCHAR(50)  DEFAULT 'ARTICLE'        COMMENT '文档类型',
    `status`      VARCHAR(30)  DEFAULT 'INDEXED'        COMMENT '索引状态',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG文档表';

CREATE TABLE `knowledge_document_chunk` (
    `id`               BIGINT NOT NULL AUTO_INCREMENT COMMENT '文档片段ID',
    `document_id`      BIGINT NOT NULL                COMMENT '文档ID',
    `chunk_index`      INT    NOT NULL                COMMENT '片段序号',
    `content`          TEXT   NOT NULL                COMMENT '片段内容',
    `chunk_size`       INT    DEFAULT 0               COMMENT '片段长度',
    `embedding_vector` TEXT   DEFAULT NULL            COMMENT '预留向量字段',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`          TINYINT  DEFAULT 0             COMMENT '删除标识',
    PRIMARY KEY (`id`),
    INDEX `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG文档片段表';

CREATE TABLE `knowledge_keyword` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关键词ID',
    `keyword`     VARCHAR(100) NOT NULL                COMMENT '关键词',
    `chunk_id`    BIGINT       NOT NULL                COMMENT '片段ID',
    `document_id` BIGINT       NOT NULL                COMMENT '文档ID',
    `frequency`   INT          DEFAULT 1               COMMENT '词频',
    `tfidf`       DOUBLE       DEFAULT 0               COMMENT '局部权重',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '删除标识',
    PRIMARY KEY (`id`),
    INDEX `idx_keyword` (`keyword`),
    INDEX `idx_chunk_id` (`chunk_id`),
    INDEX `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG关键词表';

CREATE TABLE `knowledge_search_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`       BIGINT       DEFAULT NULL             COMMENT '用户ID',
    `query_text`    VARCHAR(500) NOT NULL                 COMMENT '检索问题',
    `result_count`  INT          DEFAULT 0                COMMENT '结果数量',
    `top_chunk_ids` VARCHAR(500) DEFAULT NULL             COMMENT '命中片段ID',
    `spent_ms`      INT          DEFAULT 0                COMMENT '耗时毫秒',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '删除标识',
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG检索日志表';

SET FOREIGN_KEY_CHECKS = 1;
