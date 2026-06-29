-- ============================================================
-- All extracted CREATE TABLE statements
-- Source root: D:\作业\texun
-- Generated at: 2026-06-21 18:10:44
-- Note: Statements are grouped by original SQL file.
-- ============================================================

-- ------------------------------------------------------------
-- Source: agriculture-platform\sql\agriculture_platform_init.sql
-- Tables: 72
-- ------------------------------------------------------------
CREATE TABLE `sys_dept` (
    `dept_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '閮ㄩ棬ID',
    `parent_id`   BIGINT       DEFAULT 0                COMMENT '鐖堕儴闂↖D',
    `ancestors`   VARCHAR(50)  DEFAULT ''               COMMENT '绁栫骇鍒楄〃',
    `dept_name`   VARCHAR(30)  DEFAULT ''               COMMENT '閮ㄩ棬鍚嶇О',
    `order_num`   INT          DEFAULT 0                COMMENT '鏄剧ず椤哄簭',
    `leader`      VARCHAR(20)  DEFAULT NULL             COMMENT '璐熻矗浜?,
    `phone`       VARCHAR(11)  DEFAULT NULL             COMMENT '鑱旂郴鐢佃瘽',
    `email`       VARCHAR(50)  DEFAULT NULL             COMMENT '閭',
    `status`      TINYINT      DEFAULT 0                COMMENT '閮ㄩ棬鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織锛?瀛樺湪 1鍒犻櫎锛?,
    PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='閮ㄩ棬琛?;

CREATE TABLE `sys_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
    `dept_id`       BIGINT       DEFAULT NULL             COMMENT '閮ㄩ棬ID',
    `username`      VARCHAR(50)  NOT NULL                 COMMENT '鐧诲綍鐢ㄦ埛鍚?,
    `password`      VARCHAR(200) NOT NULL                 COMMENT 'BCrypt鍔犲瘑瀵嗙爜',
    `nickname`      VARCHAR(50)  DEFAULT ''               COMMENT '鏄电О/濮撳悕',
    `user_type`     TINYINT      DEFAULT 4                COMMENT '鐢ㄦ埛绫诲瀷: 0瓒呯 1鍐滃満绠＄悊鍛?2婧簮浼佷笟 3鍐滄妧涓撳 4鏅€氬啘鎴?5鍐呭瀹℃牳鍛?,
    `email`         VARCHAR(100) DEFAULT ''               COMMENT '閭',
    `phone`         VARCHAR(20)  DEFAULT ''               COMMENT '鎵嬫満鍙?,
    `sex`           TINYINT      DEFAULT 2                COMMENT '鎬у埆: 0鐢?1濂?2鏈煡',
    `avatar`        VARCHAR(500) DEFAULT ''               COMMENT '澶村儚鍦板潃',
    `status`        TINYINT      DEFAULT 0                COMMENT '璐﹀彿鐘舵€? 0姝ｅ父 1绂佺敤',
    `login_ip`      VARCHAR(128) DEFAULT ''               COMMENT '鏈€鍚庣櫥褰旾P',
    `login_date`    DATETIME     DEFAULT NULL             COMMENT '鏈€鍚庣櫥褰曟椂闂?,
    `last_login_at` DATETIME     DEFAULT NULL             COMMENT '鏈€鍚庣櫥褰曟椂闂?,
    `create_by`     VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`     VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`        VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織锛?瀛樺湪 1鍒犻櫎锛?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛琛?;

CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '瑙掕壊ID',
    `code`        VARCHAR(30)  NOT NULL                 COMMENT '瑙掕壊缂栫爜',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '瑙掕壊鍚嶇О',
    `role_key`    VARCHAR(100) NOT NULL DEFAULT ''      COMMENT '瑙掕壊鏉冮檺瀛楃涓?,
    `role_sort`   INT          DEFAULT 0                COMMENT '鏄剧ず椤哄簭',
    `data_scope`  TINYINT      DEFAULT 1                COMMENT '鏁版嵁鑼冨洿锛?鍏ㄩ儴 2鑷畾涔?3鏈儴闂?4鏈儴闂ㄥ強浠ヤ笅锛?,
    `status`      TINYINT      DEFAULT 0                COMMENT '瑙掕壊鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `description` VARCHAR(200) DEFAULT ''               COMMENT '瑙掕壊鎻忚堪',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`      VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙掕壊琛?;

CREATE TABLE `sys_menu` (
    `menu_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '鑿滃崟ID',
    `menu_name`  VARCHAR(50)   NOT NULL                 COMMENT '鑿滃崟鍚嶇О',
    `parent_id`  BIGINT        DEFAULT 0                COMMENT '鐖惰彍鍗旾D',
    `order_num`  INT           DEFAULT 0                COMMENT '鏄剧ず椤哄簭',
    `path`       VARCHAR(200)  DEFAULT ''               COMMENT '璺敱鍦板潃',
    `component`  VARCHAR(255)  DEFAULT NULL             COMMENT '缁勪欢璺緞',
    `query`      VARCHAR(255)  DEFAULT NULL             COMMENT '璺敱鍙傛暟',
    `is_frame`   TINYINT       DEFAULT 1                COMMENT '鏄惁涓哄閾撅紙0鏄?1鍚︼級',
    `is_cache`   TINYINT       DEFAULT 0                COMMENT '鏄惁缂撳瓨锛?缂撳瓨 1涓嶇紦瀛橈級',
    `menu_type`  CHAR(1)       DEFAULT ''               COMMENT '鑿滃崟绫诲瀷锛圡鐩綍 C鑿滃崟 F鎸夐挳锛?,
    `visible`    TINYINT       DEFAULT 0                COMMENT '鑿滃崟鐘舵€侊紙0鏄剧ず 1闅愯棌锛?,
    `status`     TINYINT       DEFAULT 0                COMMENT '鑿滃崟鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `perms`      VARCHAR(100)  DEFAULT NULL             COMMENT '鏉冮檺鏍囪瘑',
    `icon`       VARCHAR(100)  DEFAULT '#'              COMMENT '鑿滃崟鍥炬爣',
    `create_by`  VARCHAR(64)   DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`  VARCHAR(64)   DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`     VARCHAR(500)  DEFAULT ''               COMMENT '澶囨敞',
    `deleted`    TINYINT       DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鑿滃崟鏉冮檺琛?;

CREATE TABLE `sys_user_role` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '鐢ㄦ埛ID',
    `role_id`    BIGINT   NOT NULL                 COMMENT '瑙掕壊ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT   DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛瑙掕壊鍏宠仈琛?;

CREATE TABLE `sys_role_menu` (
    `id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`  BIGINT NOT NULL                 COMMENT '瑙掕壊ID',
    `menu_id`  BIGINT NOT NULL                 COMMENT '鑿滃崟ID',
    `deleted`  TINYINT DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙掕壊鑿滃崟鍏宠仈琛?;

CREATE TABLE `sys_post` (
    `post_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '宀椾綅ID',
    `post_code`  VARCHAR(64)  NOT NULL                 COMMENT '宀椾綅缂栫爜',
    `post_name`  VARCHAR(50)  NOT NULL                 COMMENT '宀椾綅鍚嶇О',
    `post_sort`  INT          NOT NULL                 COMMENT '鏄剧ず椤哄簭',
    `status`     TINYINT      NOT NULL DEFAULT 0       COMMENT '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宀椾綅淇℃伅琛?;

CREATE TABLE `sys_dict_type` (
    `dict_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '瀛楀吀涓婚敭',
    `dict_name`  VARCHAR(100) DEFAULT ''               COMMENT '瀛楀吀鍚嶇О',
    `dict_type`  VARCHAR(100) DEFAULT ''               COMMENT '瀛楀吀绫诲瀷',
    `status`     TINYINT      DEFAULT 0                COMMENT '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`dict_id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瀛楀吀绫诲瀷琛?;

CREATE TABLE `sys_dict_data` (
    `dict_code`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '瀛楀吀缂栫爜',
    `dict_sort`  INT          DEFAULT 0                COMMENT '瀛楀吀鎺掑簭',
    `dict_label` VARCHAR(100) DEFAULT ''               COMMENT '瀛楀吀鏍囩',
    `dict_value` VARCHAR(100) DEFAULT ''               COMMENT '瀛楀吀閿€?,
    `dict_type`  VARCHAR(100) DEFAULT ''               COMMENT '瀛楀吀绫诲瀷',
    `css_class`  VARCHAR(100) DEFAULT NULL             COMMENT '鏍峰紡灞炴€?,
    `list_class` VARCHAR(100) DEFAULT NULL             COMMENT '琛ㄦ牸鍥炴樉鏍峰紡',
    `is_default` TINYINT      DEFAULT 0                COMMENT '鏄惁榛樿锛?鍚?1鏄級',
    `status`     TINYINT      DEFAULT 0                COMMENT '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
    `create_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`  VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`     VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`dict_code`),
    INDEX `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瀛楀吀鏁版嵁琛?;

CREATE TABLE `sys_config` (
    `config_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍙傛暟涓婚敭',
    `config_name` VARCHAR(100) DEFAULT ''               COMMENT '鍙傛暟鍚嶇О',
    `config_key`  VARCHAR(100) DEFAULT ''               COMMENT '鍙傛暟閿悕',
    `config_value` VARCHAR(500) DEFAULT ''              COMMENT '鍙傛暟閿€?,
    `config_type` TINYINT      DEFAULT 1                COMMENT '绯荤粺鍐呯疆锛?鏄?1鍚︼級',
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`      VARCHAR(500) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍙傛暟閰嶇疆琛?;

CREATE TABLE `sys_file` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏂囦欢ID',
    `user_id`       BIGINT       DEFAULT NULL             COMMENT '涓婁紶鑰匢D',
    `original_name` VARCHAR(200) NOT NULL                 COMMENT '鍘熷鏂囦欢鍚?,
    `file_name`     VARCHAR(200) NOT NULL                 COMMENT '瀛樺偍鏂囦欢鍚嶏紙UUID锛?,
    `file_path`     VARCHAR(500) NOT NULL                 COMMENT '鏂囦欢瀛樺偍璺緞',
    `file_url`      VARCHAR(500) NOT NULL                 COMMENT '鏂囦欢璁块棶URL',
    `file_size`     BIGINT       DEFAULT 0                COMMENT '鏂囦欢澶у皬锛堝瓧鑺傦級',
    `content_type`  VARCHAR(100) DEFAULT ''               COMMENT 'MIME绫诲瀷',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囦欢璧勬簮琛?;

CREATE TABLE `sys_oper_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織涓婚敭',
    `user_id`     BIGINT       DEFAULT NULL             COMMENT '鎿嶄綔鑰匢D',
    `username`    VARCHAR(50)  DEFAULT ''               COMMENT '鎿嶄綔鑰呯敤鎴峰悕',
    `module`      VARCHAR(50)  DEFAULT ''               COMMENT '妯″潡鍚嶇О',
    `type`        VARCHAR(20)  DEFAULT ''               COMMENT '鎿嶄綔绫诲瀷',
    `description` VARCHAR(200) DEFAULT ''               COMMENT '鎿嶄綔鎻忚堪',
    `method`      VARCHAR(200) DEFAULT ''               COMMENT '璇锋眰鏂规硶',
    `url`         VARCHAR(500) DEFAULT ''               COMMENT '璇锋眰URL',
    `params`      TEXT                                  COMMENT '璇锋眰鍙傛暟',
    `result`      TINYINT      DEFAULT 1                COMMENT '缁撴灉锛?鎴愬姛 0澶辫触',
    `error_msg`   TEXT                                  COMMENT '閿欒淇℃伅',
    `cost_time`   BIGINT       DEFAULT 0                COMMENT '鑰楁椂锛堟绉掞級',
    `ip`          VARCHAR(50)  DEFAULT ''               COMMENT '鎿嶄綔IP',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_module` (`module`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎿嶄綔鏃ュ織琛?;

CREATE TABLE `sys_login_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁块棶ID',
    `username`    VARCHAR(50)  DEFAULT ''               COMMENT '鐢ㄦ埛璐﹀彿',
    `ipaddr`      VARCHAR(128) DEFAULT ''               COMMENT '鐧诲綍IP鍦板潃',
    `status`      TINYINT      DEFAULT 0                COMMENT '鐧诲綍鐘舵€侊紙0鎴愬姛 1澶辫触锛?,
    `msg`         VARCHAR(255) DEFAULT ''               COMMENT '鎻愮ず淇℃伅',
    `access_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '璁块棶鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绯荤粺璁块棶璁板綍琛?;

CREATE TABLE `sys_notice` (
    `notice_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍏憡ID',
    `notice_title` VARCHAR(50) NOT NULL                 COMMENT '鍏憡鏍囬',
    `notice_type` TINYINT      NOT NULL DEFAULT 1       COMMENT '鍏憡绫诲瀷锛?閫氱煡 2鍏憡锛?,
    `notice_content` LONGBLOB  DEFAULT NULL             COMMENT '鍏憡鍐呭',
    `status`      TINYINT      DEFAULT 0                COMMENT '鍏憡鐘舵€侊紙0姝ｅ父 1鍏抽棴锛?,
    `create_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鍒涘缓鑰?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_by`   VARCHAR(64)  DEFAULT ''               COMMENT '鏇存柊鑰?,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`      VARCHAR(255) DEFAULT NULL             COMMENT '澶囨敞',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='閫氱煡鍏憡琛?;

CREATE TABLE `sys_sensitive_word` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `word`       VARCHAR(50) NOT NULL                 COMMENT '鏁忔劅璇?,
    `level`      TINYINT     DEFAULT 1                COMMENT '鏁忔劅绛夌骇锛?涓€鑸?2涓ラ噸',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`    TINYINT     DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏁忔劅璇嶈〃';

CREATE TABLE `sys_feedback` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `user_id`     BIGINT        DEFAULT NULL             COMMENT '鍙嶉鐢ㄦ埛ID',
    `content`     VARCHAR(3000) DEFAULT NULL             COMMENT '鍙嶉鍐呭',
    `img`         VARCHAR(1000) DEFAULT NULL             COMMENT '鍥剧墖',
    `contacts`    VARCHAR(500)  DEFAULT NULL             COMMENT '鑱旂郴鏂瑰紡',
    `status`      TINYINT       DEFAULT 0                COMMENT '0鏈鐞?1宸插鐞?,
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT       DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鍙嶉琛?;

CREATE TABLE `farm_enterprise` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '基地ID',
    `base_name`            VARCHAR(100) NOT NULL                 COMMENT '基地名称',
    `enterprise_name`      VARCHAR(100) DEFAULT ''               COMMENT '所属企业/合作社',
    `base_type`            VARCHAR(50)  DEFAULT ''               COMMENT '基地类型',
    `main_crop`            VARCHAR(50)  DEFAULT '草莓'           COMMENT '主栽作物',
    `strawberry_variety`   VARCHAR(100) DEFAULT ''               COMMENT '草莓品种',
    `region`               VARCHAR(100) DEFAULT ''               COMMENT '所在地区',
    `detail_address`       VARCHAR(200) DEFAULT ''               COMMENT '详细地址',
    `total_area`           DECIMAL(10,2) DEFAULT 0               COMMENT '总面积（亩）',
    `greenhouse_count`     INT          DEFAULT 0               COMMENT '温室数量',
    `planting_area`        DECIMAL(10,2) DEFAULT 0               COMMENT '草莓种植面积（亩）',
    `planting_mode`        VARCHAR(50)  DEFAULT ''               COMMENT '种植模式',
    `irrigation_mode`      VARCHAR(50)  DEFAULT ''               COMMENT '灌溉方式',
    `expected_yield`       DECIMAL(10,2) DEFAULT 0               COMMENT '年预计产量（kg）',
    `enable_monitor`       TINYINT      DEFAULT 0               COMMENT '是否接入环境监测',
    `enable_disease_detect` TINYINT     DEFAULT 0               COMMENT '是否启用病虫害识别',
    `enable_trace`         TINYINT      DEFAULT 0               COMMENT '是否启用溯源',
    `lng`                  DECIMAL(10,6) DEFAULT NULL           COMMENT '经度（可空）',
    `lat`                  DECIMAL(10,6) DEFAULT NULL           COMMENT '纬度（可空）',
    `base_code`            VARCHAR(50)  DEFAULT ''               COMMENT '基地编码',
    `manager_name`         VARCHAR(50)  DEFAULT ''               COMMENT '基地负责人',
    `manager_phone`        VARCHAR(20)  DEFAULT ''               COMMENT '联系电话',
    `admin_id`             BIGINT       DEFAULT NULL            COMMENT '管理员用户ID（关联sys_user）',
    `status`               TINYINT      DEFAULT 0               COMMENT '0正常 1停用 2建设中',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`              TINYINT      DEFAULT 0               COMMENT '删除标志',
    PRIMARY KEY (`id`),
    INDEX `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='草莓基地表';

CREATE TABLE `farm_land` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍦板潡ID',
    `enterprise_id` BIGINT      NOT NULL                 COMMENT '鎵€灞炰紒涓欼D',
    `number`       INT          NOT NULL                 COMMENT '澶ф/楸煎缂栧彿',
    `type`         TINYINT      NOT NULL DEFAULT 0       COMMENT '绫诲瀷: 0澶ф 1楸煎 2澶х敯 3浠撳簱',
    `display_name` VARCHAR(100) DEFAULT ''               COMMENT '鏄剧ず鍚嶇О',
    `camera_pass_num` VARCHAR(50) DEFAULT '1'            COMMENT '鎽勫儚澶撮€氶亾鍙?,
    `area`         DECIMAL(10,2) DEFAULT 0               COMMENT '闈㈢Н锛堝钩鏂圭背锛?,
    `location`     VARCHAR(200) DEFAULT ''               COMMENT '浣嶇疆鎻忚堪',
    `status`       TINYINT      DEFAULT 0                COMMENT '0姝ｅ父 1鍋滅敤',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`      TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍦板潡琛紙澶ф/楸煎/澶х敯/浠撳簱锛?;

CREATE TABLE `farm_crop` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '浣滅墿ID',
    `name`       VARCHAR(50)  NOT NULL                 COMMENT '浣滅墿鍚嶇О',
    `code`       VARCHAR(30)  NOT NULL                 COMMENT '浣滅墿缂栫爜',
    `icon`       VARCHAR(500) DEFAULT ''               COMMENT '浣滅墿鍥炬爣',
    `sort_order` INT          DEFAULT 0                COMMENT '鎺掑簭搴忓彿',
    `type`       TINYINT      DEFAULT 0                COMMENT '绫诲瀷: 0鍐滀綔鐗?1娓斾笟 2鐣滅墽涓?,
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`    TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浣滅墿瀛楀吀琛?;

CREATE TABLE `farm_planting_plan` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁″垝ID',
    `enterprise_id`  BIGINT       NOT NULL               COMMENT '鎵€灞炰紒涓欼D',
    `land_id`        BIGINT       NOT NULL               COMMENT '鍦板潡ID',
    `crop_id`        BIGINT       NOT NULL               COMMENT '浣滅墿ID',
    `plan_name`      VARCHAR(100) NOT NULL               COMMENT '璁″垝鍚嶇О',
    `season`         INT          DEFAULT 1              COMMENT '瀛ｆ: 1鏄?2澶?3绉?4鍐?,
    `start_date`     DATE         DEFAULT NULL           COMMENT '寮€濮嬫棩鏈?,
    `end_date`       DATE         DEFAULT NULL           COMMENT '棰勮缁撴潫鏃ユ湡',
    `expected_yield` DECIMAL(10,2) DEFAULT 0             COMMENT '棰勬湡浜ч噺',
    `yield_unit`     VARCHAR(20)  DEFAULT 'kg'           COMMENT '浜ч噺鍗曚綅',
    `responsible_id` BIGINT       DEFAULT NULL           COMMENT '璐熻矗浜篒D锛堝叧鑱攕ys_user锛?,
    `status`         TINYINT      DEFAULT 0              COMMENT '0璁″垝涓?1杩涜涓?2宸插畬鎴?3宸插彇娑?,
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`         VARCHAR(500) DEFAULT NULL           COMMENT '澶囨敞',
    `deleted`        TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绉嶆璁″垝琛?;

CREATE TABLE `farm_task` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '浠诲姟ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '鎵€灞炰紒涓欼D',
    `land_id`         BIGINT       NOT NULL               COMMENT '鍦板潡ID',
    `planting_plan_id` BIGINT      DEFAULT NULL            COMMENT '鍏宠仈绉嶆璁″垝ID',
    `task_name`       VARCHAR(100) NOT NULL               COMMENT '浠诲姟鍚嶇О',
    `task_type`       VARCHAR(30)  DEFAULT ''             COMMENT '浠诲姟绫诲瀷: 鎾/鏂借偉/鐏屾簤/鎵撹嵂/閲囨敹/鍏朵粬',
    `description`     TEXT                                COMMENT '浠诲姟鎻忚堪',
    `start_time`      DATETIME     DEFAULT NULL           COMMENT '寮€濮嬫椂闂?,
    `end_time`        DATETIME     DEFAULT NULL           COMMENT '缁撴潫鏃堕棿',
    `assignee_id`     BIGINT       DEFAULT NULL           COMMENT '鎵ц浜篒D锛堝叧鑱攕ys_user锛?,
    `status`          TINYINT      DEFAULT 0              COMMENT '0寰呮墽琛?1杩涜涓?2宸插畬鎴?3宸插彇娑?,
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`          VARCHAR(500) DEFAULT NULL           COMMENT '澶囨敞',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_assignee_id` (`assignee_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滀簨浠诲姟琛?;

CREATE TABLE `farm_device` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁惧ID',
    `type`         TINYINT      NOT NULL DEFAULT 0       COMMENT '閫傜敤绫诲瀷: 0鍐滀笟 1娓斾笟 2澶х敯 3浠撳簱',
    `device_num`   VARCHAR(10)  NOT NULL                 COMMENT '璁惧缂栧彿',
    `name`         VARCHAR(50)  NOT NULL                 COMMENT '璁惧鍚嶇О',
    `device_type`  TINYINT      NOT NULL DEFAULT 0       COMMENT '0鎺у埗璁惧(鍔ㄦ€? 1閲囬泦璁惧(闈欐€?',
    `need_statistics` TINYINT   DEFAULT 0                COMMENT '鏄惁闇€瑕佺粺璁?,
    `park_device`  TINYINT      DEFAULT 0                COMMENT '鏄惁涓哄洯鍖鸿澶?,
    `img`          VARCHAR(100) DEFAULT ''               COMMENT '璁惧鍥炬爣',
    `can_modify`   TINYINT      DEFAULT 0                COMMENT '鏄惁鏀寔鎵嬪姩淇敼鏁板€?,
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`      TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_num` (`device_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁惧瀹氫箟琛?;

CREATE TABLE `farm_device_rel` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍏宠仈ID',
    `device_id`       BIGINT       NOT NULL               COMMENT '璁惧ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '浼佷笟ID',
    `land_id`         BIGINT       NOT NULL               COMMENT '鍦板潡ID',
    `device_switch_num` TINYINT    DEFAULT 1              COMMENT '璁惧寮€鍏充釜鏁?,
    `device_type`     TINYINT      DEFAULT 0              COMMENT '璁惧绫诲瀷: 0鍐滀笟 1娓斾笟 2澶х敯 3浠撳簱',
    `device_state`    TINYINT      DEFAULT 0              COMMENT '璁惧鐘舵€? 0杩愯涓?1棰勮 2鏂嚎',
    `unique_flag`     VARCHAR(50)  DEFAULT ''             COMMENT '浼佷笟ID+澶ф缂栧彿+璁惧缂栧彿鍞竴閿?,
    `is_modify`       TINYINT      DEFAULT 0              COMMENT '鏄惁鎵嬪姩淇敼: 0鍚?1鏄?,
    `modify_val`      VARCHAR(50)  DEFAULT ''             COMMENT '淇敼鍊?,
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_flag` (`unique_flag`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁惧鍏宠仈琛?;

CREATE TABLE `farm_device_control` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁剧疆ID',
    `enterprise_id`    BIGINT       NOT NULL               COMMENT '浼佷笟ID',
    `land_id`          BIGINT       NOT NULL               COMMENT '鍦板潡ID',
    `device_id`        BIGINT       NOT NULL               COMMENT '鎺у埗鍣ㄨ澶嘔D',
    `device_switch_num` INT         DEFAULT 1              COMMENT '鎺у埗鍣ㄥ紑鍏崇紪鍙?,
    `control_type`     TINYINT      NOT NULL DEFAULT 0     COMMENT '鎺у埗绫诲瀷: 0瀹氭椂 1寰幆 2鏅鸿兘',
    `open_device`      VARCHAR(100) DEFAULT NULL           COMMENT '鏅鸿兘鎺у埗-鎵撳紑璁惧',
    `close_device`     VARCHAR(100) DEFAULT NULL           COMMENT '鏅鸿兘鎺у埗-鍏抽棴璁惧',
    `open_type`        INT          DEFAULT NULL           COMMENT '鎵撳紑璁惧鏉′欢: 0澶т簬 1灏忎簬',
    `close_type`       INT          DEFAULT NULL           COMMENT '鍏抽棴璁惧鏉′欢: 0澶т簬 1灏忎簬',
    `open_val`         DECIMAL(4,1) DEFAULT NULL           COMMENT '鎵撳紑璁惧鏁板€?,
    `close_val`        DECIMAL(4,1) DEFAULT NULL           COMMENT '鍏抽棴璁惧鏁板€?,
    `start_time`       TIME         NOT NULL               COMMENT '寮€濮嬫椂闂?,
    `end_time`         TIME         DEFAULT NULL           COMMENT '缁撴潫鏃堕棿',
    `loop_type`        INT          DEFAULT NULL           COMMENT '寰幆缁撴潫绫诲瀷: 0鏃堕棿 1娆℃暟',
    `loop_cnt`         INT          DEFAULT NULL           COMMENT '寰幆娆℃暟',
    `duration_time`    INT          DEFAULT NULL           COMMENT '鎸佺画鏃堕暱(鍒嗛挓)',
    `interval_time`    INT          DEFAULT NULL           COMMENT '闂撮殧鏃堕暱(鍒嗛挓)',
    `loop_week`        VARCHAR(100) DEFAULT NULL           COMMENT '寰幆鏄熸湡(閫楀彿鍒嗛殧 1-7)',
    `use_state`        TINYINT      DEFAULT 1              COMMENT '0绂佺敤 1鍚敤',
    `create_time`      DATETIME     NOT NULL               COMMENT '鍒涘缓鏃堕棿',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`          TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_land_id` (`land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎺у埗鍣ㄨ缃〃';

CREATE TABLE `farm_env_data` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '鏁版嵁ID',
    `enterprise_id` BIGINT        NOT NULL                COMMENT '浼佷笟ID',
    `land_id`       BIGINT        NOT NULL                COMMENT '鍦板潡ID',
    `device_id`    BIGINT         NOT NULL                COMMENT '璁惧ID',
    `land_type`    TINYINT        DEFAULT 0               COMMENT '鍦板潡绫诲瀷: 0澶ф 1楸煎 2澶х敯 3浠撳簱',
    `data_value`   DECIMAL(11,2)  DEFAULT NULL            COMMENT '閲囬泦鏁版嵁鍊?,
    `year`         INT            NOT NULL                COMMENT '骞翠唤',
    `year_date`    INT            NOT NULL                COMMENT '鏃ユ湡(濡?118 琛ㄧず绗?18澶?',
    `year_date_hour` INT          NOT NULL                COMMENT '鏃ユ湡灏忔椂(YYYYMMDDHH)',
    `create_time`  DATETIME       NOT NULL                COMMENT '閲囬泦鏃堕棿',
    `deleted`      TINYINT        DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_land` (`enterprise_id`, `land_id`),
    INDEX `idx_year_date_hour` (`year_date_hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐜鏁版嵁閲囬泦琛?;

CREATE TABLE `farm_data_sum` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '姹囨€籌D',
    `enterprise_id` BIGINT        NOT NULL DEFAULT 0      COMMENT '浼佷笟ID',
    `land_id`       BIGINT        NOT NULL DEFAULT 0      COMMENT '鍦板潡ID',
    `device_id`    BIGINT         NOT NULL DEFAULT 0      COMMENT '璁惧ID',
    `year`         YEAR           NOT NULL DEFAULT '2000' COMMENT '骞村害',
    `sum_data`     DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '绱鏁板€?,
    `update_time`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`      TINYINT        DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_land` (`enterprise_id`, `land_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骞村害鏁版嵁姹囨€昏〃';

CREATE TABLE `farm_employee` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍛樺伐ID',
    `enterprise_id`   BIGINT       NOT NULL               COMMENT '浼佷笟ID',
    `user_id`         BIGINT       DEFAULT NULL           COMMENT '鍏宠仈绯荤粺鐢ㄦ埛ID',
    `name`            VARCHAR(50)  NOT NULL               COMMENT '鍛樺伐濮撳悕',
    `sex`             TINYINT      DEFAULT 1              COMMENT '0濂?1鐢?,
    `icon`            VARCHAR(200) DEFAULT NULL           COMMENT '澶村儚',
    `mobile`          VARCHAR(50)  DEFAULT ''             COMMENT '鎵嬫満鍙?,
    `position`        VARCHAR(50)  DEFAULT ''             COMMENT '鑱屼綅',
    `age`             INT          DEFAULT 0              COMMENT '骞撮緞',
    `certificates`    VARCHAR(200) DEFAULT NULL           COMMENT '璧勬牸璇侀檮浠?,
    `certificates_valid` DATE     DEFAULT NULL            COMMENT '璧勬牸璇佹湁鏁堟湡',
    `healthy_state`   TINYINT      DEFAULT 1              COMMENT '鍋ュ悍鐘舵€? 0寮傚父 1姝ｅ父',
    `address`         VARCHAR(200) DEFAULT NULL           COMMENT '鎴风睄鍦板潃',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_enterprise_id` (`enterprise_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滃満鍛樺伐琛?;

CREATE TABLE `trace_product` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '浜у搧ID',
    `product_code`  VARCHAR(50)  NOT NULL                COMMENT '浜у搧缂栫爜',
    `product_name`  VARCHAR(100) NOT NULL                COMMENT '浜у搧鍚嶇О',
    `category`      VARCHAR(50)  DEFAULT NULL            COMMENT '浜у搧绫诲埆',
    `specification` VARCHAR(200) DEFAULT NULL            COMMENT '瑙勬牸',
    `unit`          VARCHAR(20)  DEFAULT NULL            COMMENT '鍗曚綅',
    `origin`        VARCHAR(100) DEFAULT NULL            COMMENT '浜у湴',
    `enterprise_id` BIGINT       DEFAULT NULL            COMMENT '鎵€灞炰紒涓欼D',
    `producer_name` VARCHAR(100) DEFAULT NULL            COMMENT '鐢熶骇鑰呭悕绉?,
    `farm_id`       BIGINT       DEFAULT NULL            COMMENT '鍏宠仈鍐滃満ID',
    `crop_id`       BIGINT       DEFAULT NULL            COMMENT '鍏宠仈浣滅墿ID',
    `status`        TINYINT      DEFAULT 0               COMMENT '0涓嬫灦 1涓婃灦',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_code` (`product_code`),
    INDEX `idx_category` (`category`),
    INDEX `idx_farm_id` (`farm_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婧簮浜у搧琛?;

CREATE TABLE `trace_batch` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '鎵规ID',
    `batch_no`         VARCHAR(50)   NOT NULL                COMMENT '鎵规鍙?,
    `product_code`     VARCHAR(50)   NOT NULL                COMMENT '浜у搧缂栫爜',
    `product_name`     VARCHAR(100)  DEFAULT NULL            COMMENT '浜у搧鍚嶇О',
    `quantity`         DECIMAL(18,4) NOT NULL                COMMENT '鏁伴噺',
    `unit`             VARCHAR(20)   DEFAULT NULL            COMMENT '鍗曚綅',
    `production_date`  DATE          DEFAULT NULL            COMMENT '鐢熶骇鏃ユ湡',
    `expiry_date`      DATE          DEFAULT NULL            COMMENT '淇濊川鏈熻嚦',
    `farm_id`          BIGINT        DEFAULT NULL            COMMENT '鍏宠仈鍐滃満ID',
    `land_id`          BIGINT        DEFAULT NULL            COMMENT '鍏宠仈鍦板潡ID',
    `crop_id`          BIGINT        DEFAULT NULL            COMMENT '鍏宠仈浣滅墿ID',
    `planting_plan_id` BIGINT        DEFAULT NULL            COMMENT '鍏宠仈绉嶆璁″垝ID',
    `producer_id`      BIGINT        DEFAULT NULL            COMMENT '鐢熶骇鑰匢D',
    `producer_name`    VARCHAR(100)  DEFAULT NULL            COMMENT '鐢熶骇鑰呭悕绉?,
    `status`           TINYINT       DEFAULT 0               COMMENT '鐘舵€? 0鐢熶骇涓?1宸插畬鎴?2宸插け鏁?,
    `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`           VARCHAR(500)  DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`          TINYINT       DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_farm_id` (`farm_id`),
    INDEX `idx_land_id` (`land_id`),
    INDEX `idx_crop_id` (`crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婧簮鎵规琛?;

CREATE TABLE `trace_production_record` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`         VARCHAR(50)  NOT NULL                COMMENT '鎵规缂栧彿',
    `sow_time`         DATETIME     DEFAULT NULL            COMMENT '鎾鏃堕棿',
    `fertilizer_record` VARCHAR(500) DEFAULT NULL           COMMENT '鏂借偉璁板綍',
    `pesticide_record`  VARCHAR(500) DEFAULT NULL           COMMENT '鍐滆嵂浣跨敤璁板綍',
    `irrigation_record` VARCHAR(500) DEFAULT NULL           COMMENT '鐏屾簤璁板綍',
    `soil_temperature`  VARCHAR(50) DEFAULT NULL            COMMENT '鍦熷￥娓╁害',
    `soil_humidity`     VARCHAR(50) DEFAULT NULL            COMMENT '鍦熷￥婀垮害',
    `harvest_time`      DATETIME    DEFAULT NULL            COMMENT '閲囨敹鏃堕棿',
    `responsible_person` VARCHAR(100) DEFAULT NULL          COMMENT '绉嶆璐熻矗浜?,
    `image_url`         VARCHAR(500) DEFAULT NULL           COMMENT '鍥剧墖鍦板潃',
    `operator_id`       BIGINT      DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`     VARCHAR(100) DEFAULT NULL           COMMENT '鎿嶄綔鑰呭鍚?,
    `chain_hash`        VARCHAR(64) DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`       DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`       DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`            VARCHAR(500) DEFAULT NULL           COMMENT '澶囨敞',
    `deleted`           TINYINT     DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢熶骇/绉嶆璁板綍琛?;

CREATE TABLE `trace_processing_record` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`              VARCHAR(50)  NOT NULL                COMMENT '鎵规缂栧彿',
    `processing_enterprise` VARCHAR(200) DEFAULT NULL            COMMENT '鍔犲伐浼佷笟',
    `processing_time`       DATETIME     DEFAULT NULL            COMMENT '鍔犲伐鏃堕棿',
    `processing_method`     VARCHAR(200) DEFAULT NULL            COMMENT '鍔犲伐鏂瑰紡',
    `processing_temperature` VARCHAR(50) DEFAULT NULL            COMMENT '鍔犲伐娓╁害',
    `quality_result`        VARCHAR(50)  DEFAULT NULL            COMMENT '璐ㄦ缁撴灉',
    `inspector`             VARCHAR(100) DEFAULT NULL            COMMENT '妫€娴嬩汉鍛?,
    `report_url`            VARCHAR(500) DEFAULT NULL            COMMENT '妫€娴嬫姤鍛婂湴鍧€',
    `operator_id`           BIGINT       DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`         VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `chain_hash`            VARCHAR(64)  DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`                VARCHAR(500) DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`               TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍔犲伐璁板綍琛?;

CREATE TABLE `trace_quality_record` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`            VARCHAR(50)  NOT NULL                COMMENT '鎵规缂栧彿',
    `processing_record_id` BIGINT      DEFAULT NULL            COMMENT '鍏宠仈鍔犲伐璁板綍ID',
    `quality_date`        DATETIME     DEFAULT NULL            COMMENT '璐ㄦ鏃ユ湡',
    `quality_result`      VARCHAR(50)  DEFAULT NULL            COMMENT '璐ㄦ缁撴灉',
    `inspector`           VARCHAR(100) DEFAULT NULL            COMMENT '妫€娴嬩汉鍛?,
    `report_url`          VARCHAR(500) DEFAULT NULL            COMMENT '鎶ュ憡鏂囦欢鍦板潃',
    `inspection_items`    VARCHAR(500) DEFAULT NULL            COMMENT '妫€娴嬮」鐩?,
    `inspection_standard` VARCHAR(200) DEFAULT NULL            COMMENT '妫€娴嬫爣鍑?,
    `qualified`           TINYINT      DEFAULT 1               COMMENT '鏄惁鍚堟牸: 0涓嶅悎鏍?1鍚堟牸',
    `operator_id`         BIGINT       DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`       VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `chain_hash`          VARCHAR(64)  DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`              VARCHAR(500) DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`             TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_processing_record_id` (`processing_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璐ㄦ璁板綍琛?;

CREATE TABLE `trace_storage_record` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`         VARCHAR(50)  NOT NULL                COMMENT '鎵规缂栧彿',
    `quantity`         INT          DEFAULT 0               COMMENT '搴撳瓨鏁伴噺',
    `change_type`      VARCHAR(20)  DEFAULT NULL            COMMENT '搴撳瓨鍙樺姩绫诲瀷: IN鍏ュ簱 OUT鍑哄簱',
    `change_quantity`  INT          DEFAULT 0               COMMENT '鍙樺姩鏁伴噺',
    `after_quantity`   INT          DEFAULT 0               COMMENT '鍙樺姩鍚庡簱瀛?,
    `change_time`      DATETIME     DEFAULT NULL            COMMENT '鍙樺姩鏃堕棿',
    `operator_name`    VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔浜?,
    `operator_id`      BIGINT       DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `related_record_id` BIGINT      DEFAULT NULL            COMMENT '鍏宠仈涓氬姟ID',
    `chain_hash`       VARCHAR(64)  DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`           VARCHAR(500) DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`          TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浠撳偍/搴撳瓨璁板綍琛?;

CREATE TABLE `trace_logistics_record` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`              VARCHAR(50)  NOT NULL                COMMENT '鎵规缂栧彿',
    `logistics_company`     VARCHAR(200) DEFAULT NULL            COMMENT '鐗╂祦鍏徃',
    `transport_vehicle`     VARCHAR(100) DEFAULT NULL            COMMENT '杩愯緭杞﹁締',
    `driver_name`           VARCHAR(50)  DEFAULT NULL            COMMENT '鍙告満濮撳悕',
    `driver_phone`          VARCHAR(20)  DEFAULT NULL            COMMENT '鍙告満鐢佃瘽',
    `from_address`          VARCHAR(500) DEFAULT NULL            COMMENT '鍙戣揣鍦板潃',
    `to_address`            VARCHAR(500) DEFAULT NULL            COMMENT '鏀惰揣鍦板潃',
    `ship_time`             DATETIME     DEFAULT NULL            COMMENT '鍙戣揣鏃堕棿',
    `arrival_time`          DATETIME     DEFAULT NULL            COMMENT '鍒拌揣鏃堕棿',
    `transport_temperature` VARCHAR(50)  DEFAULT NULL            COMMENT '杩愯緭娓╁害',
    `transport_humidity`    VARCHAR(50)  DEFAULT NULL            COMMENT '杩愯緭婀垮害',
    `logistics_status`      VARCHAR(50)  DEFAULT NULL            COMMENT '鐗╂祦鐘舵€?,
    `operator_id`           BIGINT       DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`         VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `chain_hash`            VARCHAR(64)  DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`                VARCHAR(500) DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`               TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐗╂祦璁板綍琛?;

CREATE TABLE `trace_sales_record` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `batch_no`      VARCHAR(50)   NOT NULL                COMMENT '鎵规缂栧彿',
    `seller`        VARCHAR(200)  DEFAULT NULL            COMMENT '閿€鍞晢',
    `sales_region`  VARCHAR(200)  DEFAULT NULL            COMMENT '閿€鍞湴鍖?,
    `listing_time`  DATETIME      DEFAULT NULL            COMMENT '涓婃灦鏃堕棿',
    `sales_price`   DECIMAL(18,2) DEFAULT NULL            COMMENT '閿€鍞环鏍?,
    `stock_quantity` INT          DEFAULT 0               COMMENT '搴撳瓨鏁伴噺',
    `sales_status`  VARCHAR(50)   DEFAULT NULL            COMMENT '閿€鍞姸鎬?,
    `operator_id`   BIGINT        DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name` VARCHAR(100)  DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `chain_hash`    VARCHAR(64)   DEFAULT NULL            COMMENT '閾句笂鍝堝笇',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `remark`        VARCHAR(500)  DEFAULT NULL            COMMENT '澶囨敞',
    `deleted`       TINYINT       DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='閿€鍞褰曡〃';

CREATE TABLE `trace_qrcode` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '浜岀淮鐮両D',
    `qr_code`      VARCHAR(255) NOT NULL                COMMENT '浜岀淮鐮佸唴瀹?,
    `product_code` VARCHAR(50)  NOT NULL                COMMENT '浜у搧缂栫爜',
    `batch_no`     VARCHAR(50)  NOT NULL                COMMENT '鎵规鍙?,
    `trace_id`     VARCHAR(64)  DEFAULT NULL            COMMENT '婧簮ID',
    `status`       TINYINT      DEFAULT 0               COMMENT '鐘舵€? 0鏈縺娲?1宸叉縺娲?2宸插け鏁?,
    `scan_count`   INT          DEFAULT 0               COMMENT '鎵弿娆℃暟',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`      TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_qr_code` (`qr_code`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婧簮鐮佽〃';

CREATE TABLE `trace_query_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    `qr_code`     VARCHAR(255) DEFAULT NULL            COMMENT '婧簮鐮?,
    `batch_no`    VARCHAR(50)  DEFAULT NULL            COMMENT '鎵规鍙?,
    `query_ip`    VARCHAR(50)  DEFAULT NULL            COMMENT '鏌ヨIP',
    `query_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鏌ヨ鏃堕棿',
    `user_agent`  VARCHAR(500) DEFAULT NULL            COMMENT '鐢ㄦ埛浠ｇ悊',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_query_time` (`query_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍏紑鏌ヨ鏃ュ織琛?;

CREATE TABLE `trace_chain_header` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '閾惧ごID',
    `chain_id`      VARCHAR(64)  NOT NULL                COMMENT '閾綢D',
    `chain_name`    VARCHAR(100) NOT NULL                COMMENT '閾惧悕绉?,
    `description`   VARCHAR(500) DEFAULT NULL            COMMENT '鎻忚堪',
    `current_height` BIGINT      DEFAULT 0               COMMENT '褰撳墠楂樺害',
    `latest_hash`   VARCHAR(64)  DEFAULT NULL            COMMENT '鏈€鏂板尯鍧楀搱甯?,
    `status`        TINYINT      DEFAULT 0               COMMENT '鐘舵€? 0姝ｅ父 1澶辨晥',
    `creator_id`    VARCHAR(64)  DEFAULT NULL            COMMENT '鍒涘缓浜篒D',
    `creator_name`  VARCHAR(100) DEFAULT NULL            COMMENT '鍒涘缓浜哄鍚?,
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_chain_id` (`chain_id`),
    INDEX `idx_chain_name` (`chain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍝堝笇閾惧ご琛?;

CREATE TABLE `trace_chain_block` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍖哄潡ID',
    `chain_id`       VARCHAR(64)  NOT NULL                COMMENT '閾綢D',
    `block_hash`     VARCHAR(64)  NOT NULL                COMMENT '鍖哄潡鍝堝笇',
    `previous_hash`  VARCHAR(64)  NOT NULL                COMMENT '鍓嶄竴鍖哄潡鍝堝笇',
    `data_hash`      VARCHAR(64)  NOT NULL                COMMENT '鏁版嵁鍝堝笇',
    `data_content`   TEXT                                  COMMENT '鏁版嵁鍐呭(JSON)',
    `nonce`          BIGINT       DEFAULT 0               COMMENT '闅忔満鏁?,
    `timestamp`      BIGINT       DEFAULT NULL            COMMENT '鏃堕棿鎴?,
    `operator_id`    VARCHAR(64)  DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`  VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `operation_type` VARCHAR(50)  DEFAULT NULL            COMMENT '鎿嶄綔绫诲瀷',
    `product_code`   VARCHAR(50)  DEFAULT NULL            COMMENT '浜у搧缂栫爜',
    `batch_no`       VARCHAR(50)  DEFAULT NULL            COMMENT '鎵规鍙?,
    `block_height`   INT          DEFAULT 0               COMMENT '鍖哄潡楂樺害',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_block_hash` (`block_hash`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍖哄潡鏁版嵁琛?;

CREATE TABLE `trace_chain_verification` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '楠岃瘉ID',
    `chain_id`            VARCHAR(64)  NOT NULL                COMMENT '閾綢D',
    `block_hash`          VARCHAR(64)  NOT NULL                COMMENT '鍖哄潡鍝堝笇',
    `data_hash`           VARCHAR(64)  DEFAULT NULL            COMMENT '鏁版嵁鍝堝笇',
    `original_data`       TEXT                                  COMMENT '鍘熷鏁版嵁',
    `verified`            TINYINT      DEFAULT 0               COMMENT '楠岃瘉缁撴灉: 0鏈€氳繃 1閫氳繃',
    `verification_result` VARCHAR(500) DEFAULT NULL            COMMENT '楠岃瘉缁撴灉鎻忚堪',
    `operator_id`         VARCHAR(64)  DEFAULT NULL            COMMENT '鎿嶄綔鑰匢D',
    `operator_name`       VARCHAR(100) DEFAULT NULL            COMMENT '鎿嶄綔鑰呭鍚?,
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`             TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_block_hash` (`block_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='閾鹃獙璇佽褰曡〃';

CREATE TABLE `trace_enterprise_profile` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '妗ｆID',
    `user_id`         BIGINT       NOT NULL                COMMENT '鍏宠仈鐢ㄦ埛ID',
    `enterprise_name` VARCHAR(200) DEFAULT ''              COMMENT '浼佷笟鍚嶇О',
    `license_no`      VARCHAR(100) DEFAULT ''              COMMENT '钀ヤ笟鎵х収鍙?,
    `address`         VARCHAR(300) DEFAULT ''              COMMENT '浼佷笟鍦板潃',
    `contact_person`  VARCHAR(50)  DEFAULT ''              COMMENT '鑱旂郴浜?,
    `contact_phone`   VARCHAR(20)  DEFAULT ''              COMMENT '鑱旂郴鐢佃瘽',
    `business_scope`  VARCHAR(500) DEFAULT ''             COMMENT '缁忚惀鑼冨洿',
    `status`          TINYINT      DEFAULT 0              COMMENT '0寰呭鏍?1宸茶璇?2宸查┏鍥?,
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`         TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婧簮浼佷笟淇℃伅琛?;

CREATE TABLE `knowledge_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍒嗙被ID',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '鍒嗙被鍚嶇О',
    `code`        VARCHAR(30)  NOT NULL                 COMMENT '鍒嗙被缂栫爜',
    `sort_order`  INT          DEFAULT 0                COMMENT '鎺掑簭搴忓彿',
    `description` VARCHAR(200) DEFAULT ''               COMMENT '鍒嗙被鎻忚堪',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎶€鏈垎绫昏〃';

CREATE TABLE `knowledge_tag` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '鏍囩ID',
    `name`       VARCHAR(50) NOT NULL                 COMMENT '鏍囩鍚嶇О',
    `tag_type`   VARCHAR(50) DEFAULT ''               COMMENT '鏍囩绫诲瀷锛氫綔鐗?鐥呰櫕瀹?鎶€鏈?鍦板尯/瀛ｈ妭/鏀跨瓥',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`    TINYINT     DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏍囩琛?;

CREATE TABLE `knowledge_article` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏂囩珷ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '浣滆€匢D',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '鍒嗙被ID',
    `crop_id`        BIGINT       DEFAULT NULL            COMMENT '鍏宠仈浣滅墿ID',
    `title`          VARCHAR(200) NOT NULL                COMMENT '鏂囩珷鏍囬',
    `content`        LONGTEXT                              COMMENT '鏂囩珷姝ｆ枃锛堝瘜鏂囨湰锛?,
    `summary`       VARCHAR(500)  DEFAULT ''              COMMENT '鎽樿',
    `cover_image`    VARCHAR(500) DEFAULT ''              COMMENT '灏侀潰鍥綰RL',
    `source`         VARCHAR(100) DEFAULT ''              COMMENT '鏂囩珷鏉ユ簮鍚嶇О',
    `source_url`     VARCHAR(500) DEFAULT ''              COMMENT '鏂囩珷鏉ユ簮閾炬帴',
    `trusted_level`  VARCHAR(50)  DEFAULT 'normal'       COMMENT '鍙俊绛夌骇锛歰fficial/expert/normal',
    `status`         TINYINT      DEFAULT 0              COMMENT '0鑽夌 1寰呭鏍?2宸插彂甯?3宸查┏鍥?4宸插綊妗?,
    `auditor_id`     BIGINT       DEFAULT NULL            COMMENT '瀹℃牳浜篒D',
    `reject_reason`  VARCHAR(500) DEFAULT ''             COMMENT '椹冲洖鍘熷洜',
    `view_count`     INT          DEFAULT 0               COMMENT '娴忚鏁?,
    `like_count`     INT          DEFAULT 0               COMMENT '鐐硅禐鏁?,
    `favorite_count` INT          DEFAULT 0               COMMENT '鏀惰棌鏁?,
    `comment_count`  INT          DEFAULT 0               COMMENT '璇勮鏁?,
    `published_at`   DATETIME     DEFAULT NULL            COMMENT '鍙戝竷鏃堕棿',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎶€鏈枃绔犺〃';

CREATE TABLE `knowledge_article_tag` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鍏宠仈ID',
    `article_id` BIGINT   NOT NULL                  COMMENT '鏂囩珷ID',
    `tag_id`     BIGINT   NOT NULL                  COMMENT '鏍囩ID',
    `weight`     DOUBLE   DEFAULT 1.0               COMMENT '鏍囩鏉冮噸',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT   DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏍囩鍏宠仈琛?;

CREATE TABLE `knowledge_article_comment` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '璇勮ID',
    `article_id` BIGINT        NOT NULL                COMMENT '鏂囩珷ID',
    `user_id`    BIGINT        NOT NULL                COMMENT '璇勮鑰匢D',
    `parent_id`  BIGINT        DEFAULT 0               COMMENT '鐖惰瘎璁篒D锛?琛ㄧず椤剁骇璇勮',
    `content`    VARCHAR(1000) NOT NULL                COMMENT '璇勮鍐呭',
    `status`     TINYINT       DEFAULT 0               COMMENT '0姝ｅ父 1闅愯棌',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`    TINYINT       DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷璇勮琛?;

CREATE TABLE `knowledge_article_like` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鐐硅禐ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '鐢ㄦ埛ID',
    `article_id` BIGINT   NOT NULL                 COMMENT '鏂囩珷ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT  DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鐐硅禐琛?;

CREATE TABLE `knowledge_article_favorite` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鏀惰棌ID',
    `user_id`    BIGINT   NOT NULL                 COMMENT '鐢ㄦ埛ID',
    `article_id` BIGINT   NOT NULL                 COMMENT '鏂囩珷ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT  DEFAULT 0                COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏀惰棌琛?;

CREATE TABLE `knowledge_article_view_log` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `article_id` BIGINT       NOT NULL                COMMENT '鏂囩珷ID',
    `user_id`    BIGINT       DEFAULT NULL            COMMENT '鐢ㄦ埛ID锛堟湭鐧诲綍涓篘ULL锛?,
    `ip`         VARCHAR(50)  DEFAULT ''              COMMENT '璁块棶IP',
    `user_agent` VARCHAR(500) DEFAULT ''              COMMENT '娴忚鍣║A',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷娴忚璁板綍琛?;

CREATE TABLE `knowledge_question` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '闂ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '鎻愰棶鑰匢D',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '鍒嗙被ID',
    `crop_id`        BIGINT       DEFAULT NULL            COMMENT '鍏宠仈浣滅墿ID',
    `title`          VARCHAR(200) NOT NULL                COMMENT '闂鏍囬',
    `content`        TEXT                                  COMMENT '闂鎻忚堪',
    `image_urls`     TEXT                                  COMMENT '闂鍥剧墖URL鍒楄〃(JSON)',
    `status`         TINYINT      DEFAULT 0               COMMENT '0寰呭洖绛?1宸茶В鍐?2宸插叧闂?,
    `view_count`     INT          DEFAULT 0               COMMENT '娴忚鏁?,
    `answer_count`   INT          DEFAULT 0               COMMENT '鍥炵瓟鏁?,
    `best_answer_id` BIGINT       DEFAULT NULL            COMMENT '閲囩撼鐨勬渶浣冲洖绛擨D',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闂瓟闂琛?;

CREATE TABLE `knowledge_answer` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鍥炵瓟ID',
    `question_id` BIGINT   NOT NULL                 COMMENT '闂ID',
    `user_id`     BIGINT   NOT NULL                 COMMENT '鍥炵瓟鑰匢D',
    `content`     TEXT     NOT NULL                 COMMENT '鍥炵瓟鍐呭',
    `is_accepted` TINYINT  DEFAULT 0               COMMENT '鏄惁琚噰绾? 0鍚?1鏄?,
    `like_count`  INT      DEFAULT 0               COMMENT '鐐硅禐鏁?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT  DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_accepted` (`is_accepted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闂瓟鍥炵瓟琛?;

CREATE TABLE `knowledge_smart_question` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '闂ID',
    `user_id`        BIGINT       NOT NULL                COMMENT '鍐滄埛鐢ㄦ埛ID',
    `title`         VARCHAR(100)  NOT NULL                COMMENT '闂鏍囬',
    `crop_type`      VARCHAR(50)  DEFAULT NULL            COMMENT '浣滅墿绫诲瀷',
    `category_id`    BIGINT       DEFAULT NULL            COMMENT '闂鍒嗙被ID',
    `description`   TEXT                                   COMMENT '闂鎻忚堪',
    `region`         VARCHAR(100) DEFAULT NULL            COMMENT '鍦板尯',
    `growth_stage`   VARCHAR(50)  DEFAULT NULL            COMMENT '鐢熼暱闃舵',
    `question_status` TINYINT     DEFAULT 0               COMMENT '0寰呰瘑鍒?1YOLO宸茶瘑鍒?2Agent宸插洖绛?3宸插洖澶?4寰呬笓瀹跺鏍?5涓撳宸插洖澶?6宸茶В鍐?7宸插叧闂?,
    `yolo_status`    TINYINT      DEFAULT 0               COMMENT '0鏈瘑鍒?1鎴愬姛 2澶辫触 3鏃犻渶璇嗗埆',
    `agent_status`   TINYINT      DEFAULT 0               COMMENT '0鏈皟鐢?1宸茬敓鎴?2澶辫触 3璇勫垎閫氳繃 4璇勫垎鏈€氳繃',
    `view_count`     INT          DEFAULT 0               COMMENT '娴忚閲?,
    `answer_count`   INT          DEFAULT 0               COMMENT '鍥炵瓟鏁?,
    `best_answer_id` BIGINT       DEFAULT NULL            COMMENT '鏈€浣崇瓟妗圛D',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`        TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_question_status` (`question_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏅鸿兘鎻愰棶闂琛?;

CREATE TABLE `knowledge_question_image` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍥剧墖ID',
    `question_id`   BIGINT       NOT NULL                COMMENT '闂ID',
    `image_url`     VARCHAR(255) NOT NULL                COMMENT '鍥剧墖鍦板潃',
    `original_name` VARCHAR(255) DEFAULT NULL            COMMENT '鍘熷鏂囦欢鍚?,
    `file_size`     BIGINT       DEFAULT NULL            COMMENT '鏂囦欢澶у皬',
    `file_type`     VARCHAR(20)  DEFAULT NULL            COMMENT '鏂囦欢绫诲瀷',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闂鍥剧墖琛?;

CREATE TABLE `knowledge_yolo_result` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '璇嗗埆缁撴灉ID',
    `question_id`     BIGINT        NOT NULL                COMMENT '闂ID',
    `image_id`        BIGINT        DEFAULT NULL            COMMENT '鍥剧墖ID',
    `model_id`        VARCHAR(100)  DEFAULT 'pest-detection-yolov8/1' COMMENT '妯″瀷ID',
    `disease_name`    VARCHAR(100)  DEFAULT NULL            COMMENT '璇嗗埆鍑虹殑鐥呰櫕瀹冲悕绉?,
    `confidence`      DECIMAL(5,4)  DEFAULT NULL            COMMENT '鏈€楂樼疆淇″害',
    `prediction_json` TEXT                                    COMMENT '瀹屾暣璇嗗埆缁撴灉JSON',
    `result_image_url` VARCHAR(255) DEFAULT NULL            COMMENT '甯︽娴嬫缁撴灉鍥惧湴鍧€',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`         TINYINT       DEFAULT 0               COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_image_id` (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='YOLO璇嗗埆缁撴灉琛?;

CREATE TABLE `knowledge_smart_answer` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Agent鍥炵瓟ID',
    `question_id`       BIGINT       NOT NULL               COMMENT '闂ID',
    `diagnosis`        VARCHAR(255)  DEFAULT NULL           COMMENT '鍒濇璇婃柇',
    `cause_analysis`   TEXT                                 COMMENT '鍘熷洜鍒嗘瀽',
    `treatment_suggestion` TEXT                              COMMENT '澶勭悊寤鸿',
    `prevention_advice` TEXT                                  COMMENT '棰勯槻鎺柦',
    `risk_level`        VARCHAR(50)  DEFAULT NULL           COMMENT '椋庨櫓绛夌骇',
    `need_expert_review` TINYINT     DEFAULT 1              COMMENT '鏄惁闇€瑕佷笓瀹跺鏍?,
    `answer_content`    TEXT                                  COMMENT '瀹屾暣鍥炵瓟鍐呭',
    `score`             INT          DEFAULT 0              COMMENT '鍥炵瓟璇勫垎',
    `returned_to_farmer` TINYINT     DEFAULT 0              COMMENT '鏄惁宸茶嚜鍔ㄨ繑鍥炲啘鎴?,
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`           TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent璇婃柇鍥炵瓟琛?;

CREATE TABLE `knowledge_smart_score` (
    `id`                BIGINT   NOT NULL AUTO_INCREMENT COMMENT '璇勫垎ID',
    `question_id`       BIGINT   NOT NULL               COMMENT '闂ID',
    `agent_answer_id`   BIGINT   NOT NULL               COMMENT 'Agent鍥炵瓟ID',
    `yolo_score`        INT      DEFAULT 0              COMMENT 'YOLO缃俊搴﹀緱鍒?,
    `crop_match_score`  INT      DEFAULT 0              COMMENT '浣滅墿鍖归厤寰楀垎',
    `completeness_score` INT     DEFAULT 0              COMMENT '瀹屾暣鎬у緱鍒?,
    `safety_score`      INT      DEFAULT 0              COMMENT '瀹夊叏鎬у緱鍒?,
    `clarity_score`     INT      DEFAULT 0              COMMENT '娓呮櫚搴﹀緱鍒?,
    `total_score`       INT      DEFAULT 0              COMMENT '鎬诲垎',
    `score_reason`      TEXT                             COMMENT '璇勫垎鍘熷洜',
    `can_return_to_farmer` TINYINT DEFAULT 0            COMMENT '鏄惁鍙互杩斿洖鍐滄埛',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`           TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_agent_answer_id` (`agent_answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍥炵瓟璇勫垎琛?;

CREATE TABLE `knowledge_expert_answer` (
    `id`                    BIGINT   NOT NULL AUTO_INCREMENT COMMENT '涓撳鍥炵瓟ID',
    `question_id`           BIGINT   NOT NULL               COMMENT '闂ID',
    `expert_id`             BIGINT   NOT NULL               COMMENT '涓撳ID',
    `answer_content`        TEXT     NOT NULL               COMMENT '涓撳鍥炵瓟鍐呭',
    `reference_agent_answer` TINYINT DEFAULT 0              COMMENT '鏄惁鍙傝€傾gent鍥炵瓟',
    `is_adopted`            TINYINT  DEFAULT 0              COMMENT '鏄惁琚噰绾?,
    `create_time`           DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`               TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_expert_id` (`expert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='涓撳鍥炵瓟琛?;

CREATE TABLE `knowledge_lecture` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '璁插骇ID',
    `user_id`            BIGINT       NOT NULL               COMMENT '鍙戝竷鑰匢D',
    `title`              VARCHAR(200) NOT NULL               COMMENT '璁插骇鏍囬',
    `content`           TEXT                                  COMMENT '璁插骇璇︽儏',
    `summary`           VARCHAR(500)  DEFAULT ''             COMMENT '鎽樿',
    `cover_image`        VARCHAR(500) DEFAULT ''             COMMENT '灏侀潰鍥?,
    `speaker`            VARCHAR(100) DEFAULT ''             COMMENT '涓昏浜?,
    `speaker_title`      VARCHAR(100) DEFAULT ''             COMMENT '涓昏浜鸿亴绉?,
    `lecture_time`       DATETIME     NOT NULL               COMMENT '璁插骇鏃堕棿',
    `location`           VARCHAR(200) DEFAULT ''             COMMENT '璁插骇鍦扮偣/绾夸笂閾炬帴',
    `max_participants`   INT          DEFAULT 0              COMMENT '鏈€澶ф姤鍚嶄汉鏁帮紝0琛ㄧず涓嶉檺',
    `current_participants` INT        DEFAULT 0              COMMENT '褰撳墠鎶ュ悕浜烘暟',
    `status`             TINYINT      DEFAULT 0              COMMENT '0鑽夌 1鎶ュ悕涓?2鎶ュ悕鎴 3宸茬粨鏉?,
    `registration_deadline` DATETIME  DEFAULT NULL           COMMENT '鎶ュ悕鎴鏃堕棿',
    `create_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`            TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_lecture_time` (`lecture_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁插骇閫氱煡琛?;

CREATE TABLE `knowledge_lecture_reg` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鎶ュ悕ID',
    `lecture_id`  BIGINT   NOT NULL               COMMENT '璁插骇ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '鎶ュ悕鑰匢D',
    `status`      TINYINT  DEFAULT 0              COMMENT '0宸叉姤鍚?1宸茬鍒?2宸插彇娑?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lecture_user` (`lecture_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁插骇鎶ュ悕琛?;

CREATE TABLE `knowledge_video` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '瑙嗛ID',
    `title`           VARCHAR(100)  NOT NULL               COMMENT '瑙嗛鏍囬',
    `description`     TEXT                                  COMMENT '瑙嗛绠€浠?,
    `category_id`     BIGINT        DEFAULT NULL           COMMENT '瑙嗛鍒嗙被ID',
    `category_name`   VARCHAR(50)   DEFAULT NULL           COMMENT '鍒嗙被鍚嶇О',
    `crop_type`       VARCHAR(50)   DEFAULT NULL           COMMENT '浣滅墿绫诲瀷',
    `tags`            VARCHAR(255)  DEFAULT NULL           COMMENT '鏍囩锛岄€楀彿鍒嗛殧',
    `file_name`       VARCHAR(255)  NOT NULL               COMMENT '瑙嗛鏂囦欢鍚?,
    `video_url`       VARCHAR(255)  NOT NULL               COMMENT '瑙嗛璁块棶鍦板潃',
    `cover_url`       VARCHAR(255)  DEFAULT NULL           COMMENT '灏侀潰鍥惧湴鍧€',
    `duration`        INT           DEFAULT 0              COMMENT '瑙嗛鏃堕暱锛屽崟浣嶇',
    `view_count`      INT           DEFAULT 0              COMMENT '鎾斁閲?,
    `like_count`      INT           DEFAULT 0              COMMENT '鐐硅禐鏁?,
    `favorite_count`  INT           DEFAULT 0              COMMENT '鏀惰棌鏁?,
    `recommend_weight` INT          DEFAULT 0              COMMENT '浜哄伐鎺ㄨ崘鏉冮噸',
    `status`          TINYINT       DEFAULT 1              COMMENT '1鍚敤 0绂佺敤',
    `create_user_id`  BIGINT        DEFAULT NULL           COMMENT '鍒涘缓浜篒D',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`         TINYINT       DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_name` (`file_name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滄妧瑙嗛琛?;

CREATE TABLE `knowledge_video_favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鏀惰棌ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '鐢ㄦ埛ID',
    `video_id`    BIGINT   NOT NULL               COMMENT '瑙嗛ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙嗛鏀惰棌琛?;

CREATE TABLE `knowledge_video_like` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鐐硅禐ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '鐢ㄦ埛ID',
    `video_id`    BIGINT   NOT NULL               COMMENT '瑙嗛ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙嗛鐐硅禐琛?;

CREATE TABLE `knowledge_expert_profile` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '涓撳妗ｆID',
    `user_id`     BIGINT       NOT NULL               COMMENT '鍏宠仈鐢ㄦ埛ID',
    `type`        TINYINT      DEFAULT 0              COMMENT '0鍐滀笟 1娓斾笟',
    `industry`    VARCHAR(100) DEFAULT ''             COMMENT '琛屼笟',
    `major`       VARCHAR(100) DEFAULT ''             COMMENT '涓撻暱',
    `wechat`      VARCHAR(50)  DEFAULT NULL           COMMENT '寰俊',
    `qq`          VARCHAR(50)  DEFAULT NULL           COMMENT 'QQ',
    `introduction` TEXT                                COMMENT '涓撳绠€浠?,
    `status`      TINYINT      DEFAULT 0              COMMENT '0寰呭鏍?1宸茶璇?2宸查┏鍥?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='涓撳淇℃伅妗ｆ琛?;

CREATE TABLE `knowledge_user_behavior` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '琛屼负ID',
    `user_id`       BIGINT       NOT NULL               COMMENT '鐢ㄦ埛ID',
    `article_id`    BIGINT       DEFAULT NULL           COMMENT '鏂囩珷ID',
    `video_id`      BIGINT       DEFAULT NULL           COMMENT '瑙嗛ID',
    `target_type`   VARCHAR(20)  DEFAULT 'ARTICLE'     COMMENT '鐩爣绫诲瀷锛欰RTICLE/VIDEO',
    `behavior_type` VARCHAR(50)  NOT NULL               COMMENT '琛屼负: view/like/collect/comment/question/dislike',
    `stay_seconds`  INT          DEFAULT 0              COMMENT '鍋滅暀鏃堕棿',
    `weight`        DOUBLE       DEFAULT 1.0            COMMENT '琛屼负鏉冮噸',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛琛屼负杩借釜琛?;

CREATE TABLE `knowledge_user_profile` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '鐢诲儚ID',
    `user_id`          BIGINT        NOT NULL               COMMENT '鐢ㄦ埛ID',
    `interest_tags`    TEXT                                  COMMENT '鍏磋叮鏍囩JSON',
    `crop_preference`  VARCHAR(255)  DEFAULT NULL           COMMENT '鍋忓ソ浣滅墿',
    `tech_preference`  VARCHAR(255)  DEFAULT NULL           COMMENT '鍋忓ソ鎶€鏈?,
    `region_preference` VARCHAR(255) DEFAULT NULL           COMMENT '鍋忓ソ鍦板尯',
    `profile_text`     TEXT                                  COMMENT '鐢ㄦ埛鐢诲儚鑷劧璇█鎻忚堪',
    `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`          TINYINT       DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鍏磋叮鐢诲儚琛?;

CREATE TABLE `knowledge_recommend_log` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    `user_id`    BIGINT        NOT NULL               COMMENT '鐢ㄦ埛ID',
    `article_id` BIGINT        NOT NULL               COMMENT '鎺ㄨ崘鏂囩珷ID',
    `score`      DOUBLE        DEFAULT NULL           COMMENT '鎺ㄨ崘寰楀垎',
    `reason`     TEXT                                  COMMENT '鎺ㄨ崘鐞嗙敱',
    `strategy`   VARCHAR(100)  DEFAULT NULL           COMMENT '绛栫暐: tag_match/vector_sim/hot/cold_start/trust',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT       DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎺ㄨ崘瀹¤鏃ュ織琛?;

CREATE TABLE `knowledge_pest` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鐥呰櫕瀹矷D',
    `name`         VARCHAR(100) NOT NULL               COMMENT '鐥呰櫕瀹冲悕绉?,
    `category`     VARCHAR(50)  DEFAULT NULL           COMMENT '鍒嗙被: 铏/鐥呭',
    `affected_crops` VARCHAR(500) DEFAULT NULL         COMMENT '鍙楀浣滅墿',
    `symptoms`     TEXT                                COMMENT '鐥囩姸鎻忚堪',
    `cause`        TEXT                                COMMENT '鍙戠梾鍘熷洜',
    `prevention`   TEXT                                COMMENT '闃叉不鎺柦',
    `img_url`      VARCHAR(500) DEFAULT NULL           COMMENT '绀轰緥鍥剧墖',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`      TINYINT      DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐥呰櫕瀹崇煡璇嗚〃';

CREATE TABLE `knowledge_user_crop_interest` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '鍏宠仈ID',
    `user_id`    BIGINT   NOT NULL               COMMENT '鐢ㄦ埛ID',
    `crop_id`    BIGINT   NOT NULL               COMMENT '浣滅墿ID',
    `weight`     DOUBLE   DEFAULT 1.0            COMMENT '鍏磋叮鏉冮噸',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`    TINYINT  DEFAULT 0              COMMENT '鍒犻櫎鏍囧織',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_crop` (`user_id`, `crop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛浣滅墿鍏磋叮鍏宠仈琛?;

CREATE TABLE `knowledge_document` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏂囨。ID',
    `article_id`  BIGINT       DEFAULT NULL             COMMENT '鏉ユ簮鏂囩珷ID',
    `title`       VARCHAR(200) NOT NULL                 COMMENT '鏂囨。鏍囬',
    `source`      VARCHAR(255) DEFAULT NULL             COMMENT '鏉ユ簮璇存槑',
    `doc_type`    VARCHAR(50)  DEFAULT 'ARTICLE'        COMMENT '鏂囨。绫诲瀷',
    `status`      VARCHAR(30)  DEFAULT 'INDEXED'        COMMENT '绱㈠紩鐘舵€?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG鏂囨。琛?;

CREATE TABLE `knowledge_document_chunk` (
    `id`               BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏂囨。鐗囨ID',
    `document_id`      BIGINT NOT NULL                COMMENT '鏂囨。ID',
    `chunk_index`      INT    NOT NULL                COMMENT '鐗囨搴忓彿',
    `content`          TEXT   NOT NULL                COMMENT '鐗囨鍐呭',
    `chunk_size`       INT    DEFAULT 0               COMMENT '鐗囨闀垮害',
    `embedding_vector` TEXT   DEFAULT NULL            COMMENT '棰勭暀鍚戦噺瀛楁',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`          TINYINT  DEFAULT 0             COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG鏂囨。鐗囨琛?;

CREATE TABLE `knowledge_keyword` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鍏抽敭璇岻D',
    `keyword`     VARCHAR(100) NOT NULL                COMMENT '鍏抽敭璇?,
    `chunk_id`    BIGINT       NOT NULL                COMMENT '鐗囨ID',
    `document_id` BIGINT       NOT NULL                COMMENT '鏂囨。ID',
    `frequency`   INT          DEFAULT 1               COMMENT '璇嶉',
    `tfidf`       DOUBLE       DEFAULT 0               COMMENT '灞€閮ㄦ潈閲?,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_keyword` (`keyword`),
    INDEX `idx_chunk_id` (`chunk_id`),
    INDEX `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG鍏抽敭璇嶈〃';

CREATE TABLE `knowledge_search_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    `user_id`       BIGINT       DEFAULT NULL             COMMENT '鐢ㄦ埛ID',
    `query_text`    VARCHAR(500) NOT NULL                 COMMENT '妫€绱㈤棶棰?,
    `result_count`  INT          DEFAULT 0                COMMENT '缁撴灉鏁伴噺',
    `top_chunk_ids` VARCHAR(500) DEFAULT NULL             COMMENT '鍛戒腑鐗囨ID',
    `spent_ms`      INT          DEFAULT 0                COMMENT '鑰楁椂姣',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`       TINYINT      DEFAULT 0                COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG妫€绱㈡棩蹇楄〃';

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V1__init_schema.sql
-- Tables: 19
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `code`        VARCHAR(30)  NOT NULL COMMENT '瑙掕壊缂栫爜锛屽 FARMER/PROMOTER/EXPERT/ADMIN',
    `name`        VARCHAR(50)  NOT NULL COMMENT '瑙掕壊鍚嶇О',
    `description` VARCHAR(200) DEFAULT '' COMMENT '瑙掕壊鎻忚堪',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?-姝ｅ父 1-宸插垹闄?,
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙掕壊琛?;

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`        BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `username`  VARCHAR(50)  NOT NULL COMMENT '鐧诲綍鐢ㄦ埛鍚?,
    `password`  VARCHAR(200) NOT NULL COMMENT 'BCrypt 鍔犲瘑瀵嗙爜',
    `nickname`  VARCHAR(50)  DEFAULT '' COMMENT '鏄电О',
    `phone`     VARCHAR(20)  DEFAULT '' COMMENT '鎵嬫満鍙?,
    `email`     VARCHAR(100) DEFAULT '' COMMENT '閭',
    `avatar`    VARCHAR(500) DEFAULT '' COMMENT '澶村儚URL',
    `status`    TINYINT      NOT NULL DEFAULT 0 COMMENT '鐘舵€侊細0-姝ｅ父 1-绂佺敤',
    `created_at` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`   TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛琛?;

CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `role_id`    BIGINT NOT NULL COMMENT '瑙掕壊ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛瑙掕壊鍏宠仈琛?;

CREATE TABLE IF NOT EXISTS `tech_category` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(50)  NOT NULL COMMENT '鍒嗙被鍚嶇О',
    `code`        VARCHAR(30)  NOT NULL COMMENT '鍒嗙被缂栫爜',
    `sort_order`  INT          DEFAULT 0 COMMENT '鎺掑簭搴忓彿',
    `description` VARCHAR(200) DEFAULT '' COMMENT '鍒嗙被鎻忚堪',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎶€鏈垎绫昏〃';

CREATE TABLE IF NOT EXISTS `crop_dict` (
    `id`         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(50)  NOT NULL COMMENT '浣滅墿鍚嶇О',
    `code`       VARCHAR(30)  NOT NULL COMMENT '浣滅墿缂栫爜',
    `icon`       VARCHAR(500) DEFAULT '' COMMENT '浣滅墿鍥炬爣',
    `sort_order` INT          DEFAULT 0 COMMENT '鎺掑簭搴忓彿',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浣滅墿瀛楀吀琛?;

CREATE TABLE IF NOT EXISTS `tech_tag` (
    `id`         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(50) NOT NULL COMMENT '鏍囩鍚嶇О',
    `created_at` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏍囩琛?;

CREATE TABLE IF NOT EXISTS `tech_article` (
    `id`            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT       NOT NULL COMMENT '浣滆€匢D',
    `category_id`   BIGINT       DEFAULT NULL COMMENT '鍒嗙被ID',
    `crop_id`       BIGINT       DEFAULT NULL COMMENT '鍏宠仈浣滅墿ID',
    `title`         VARCHAR(200) NOT NULL COMMENT '鏂囩珷鏍囬',
    `content`       LONGTEXT     COMMENT '鏂囩珷姝ｆ枃锛堝瘜鏂囨湰锛?,
    `summary`       VARCHAR(500) DEFAULT '' COMMENT '鎽樿',
    `cover_image`   VARCHAR(500) DEFAULT '' COMMENT '灏侀潰鍥綰RL',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '鐘舵€侊細DRAFT/PENDING_AUDIT/PUBLISHED/REJECTED/ARCHIVED',
    `reject_reason` VARCHAR(500) DEFAULT '' COMMENT '椹冲洖鍘熷洜',
    `view_count`    INT          DEFAULT 0 COMMENT '娴忚鏁?,
    `like_count`    INT          DEFAULT 0 COMMENT '鐐硅禐鏁?,
    `favorite_count` INT         DEFAULT 0 COMMENT '鏀惰棌鏁?,
    `comment_count` INT          DEFAULT 0 COMMENT '璇勮鏁?,
    `published_at`  DATETIME     DEFAULT NULL COMMENT '鍙戝竷鏃堕棿',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎶€鏈枃绔犺〃';

CREATE TABLE IF NOT EXISTS `tech_article_tag` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `article_id` BIGINT NOT NULL COMMENT '鏂囩珷ID',
    `tag_id`     BIGINT NOT NULL COMMENT '鏍囩ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏍囩鍏宠仈琛?;

CREATE TABLE IF NOT EXISTS `tech_article_comment` (
    `id`         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    `article_id` BIGINT   NOT NULL COMMENT '鏂囩珷ID',
    `user_id`    BIGINT   NOT NULL COMMENT '璇勮鑰匢D',
    `parent_id`  BIGINT   DEFAULT 0 COMMENT '鐖惰瘎璁篒D锛?琛ㄧず椤剁骇璇勮',
    `content`    VARCHAR(1000) NOT NULL COMMENT '璇勮鍐呭',
    `status`     TINYINT  NOT NULL DEFAULT 0 COMMENT '0-姝ｅ父 1-闅愯棌',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷璇勮琛?;

CREATE TABLE IF NOT EXISTS `tech_article_like` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `article_id` BIGINT NOT NULL COMMENT '鏂囩珷ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鐐硅禐琛?;

CREATE TABLE IF NOT EXISTS `tech_article_favorite` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `article_id` BIGINT NOT NULL COMMENT '鏂囩珷ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷鏀惰棌琛?;

CREATE TABLE IF NOT EXISTS `tech_article_view_log` (
    `id`         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    `article_id` BIGINT      NOT NULL COMMENT '鏂囩珷ID',
    `user_id`    BIGINT      DEFAULT NULL COMMENT '鐢ㄦ埛ID锛堟湭鐧诲綍涓篘ULL锛?,
    `ip`         VARCHAR(50) DEFAULT '' COMMENT '璁块棶IP',
    `user_agent` VARCHAR(500) DEFAULT '' COMMENT '娴忚鍣║A',
    `created_at` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囩珷娴忚璁板綍琛?;

CREATE TABLE IF NOT EXISTS `tech_question` (
    `id`           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`      BIGINT       NOT NULL COMMENT '鎻愰棶鑰匢D',
    `category_id`  BIGINT       DEFAULT NULL COMMENT '鍒嗙被ID',
    `crop_id`      BIGINT       DEFAULT NULL COMMENT '鍏宠仈浣滅墿ID',
    `title`        VARCHAR(200) NOT NULL COMMENT '闂鏍囬',
    `content`      TEXT         COMMENT '闂鎻忚堪',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'OPEN' COMMENT '鐘舵€侊細OPEN/RESOLVED/CLOSED',
    `view_count`   INT          DEFAULT 0 COMMENT '娴忚鏁?,
    `answer_count` INT          DEFAULT 0 COMMENT '鍥炵瓟鏁?,
    `best_answer_id` BIGINT     DEFAULT NULL COMMENT '閲囩撼鐨勬渶浣冲洖绛擨D',
    `created_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`      TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_id` (`crop_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滄妧闂瓟闂琛?;

CREATE TABLE IF NOT EXISTS `tech_answer` (
    `id`          BIGINT  AUTO_INCREMENT PRIMARY KEY,
    `question_id` BIGINT  NOT NULL COMMENT '闂ID',
    `user_id`     BIGINT  NOT NULL COMMENT '鍥炵瓟鑰匢D',
    `content`     TEXT    NOT NULL COMMENT '鍥炵瓟鍐呭',
    `is_accepted` TINYINT NOT NULL DEFAULT 0 COMMENT '鏄惁琚噰绾筹細0-鍚?1-鏄?,
    `like_count`  INT     DEFAULT 0 COMMENT '鐐硅禐鏁?,
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT  NOT NULL DEFAULT 0,
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_accepted` (`is_accepted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滄妧闂瓟绛旀琛?;

CREATE TABLE IF NOT EXISTS `tech_lecture` (
    `id`                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`           BIGINT       NOT NULL COMMENT '鍙戝竷鑰匢D',
    `title`             VARCHAR(200) NOT NULL COMMENT '璁插骇鏍囬',
    `content`           TEXT         COMMENT '璁插骇璇︽儏',
    `summary`           VARCHAR(500) DEFAULT '' COMMENT '鎽樿',
    `cover_image`       VARCHAR(500) DEFAULT '' COMMENT '灏侀潰鍥?,
    `speaker`           VARCHAR(100) DEFAULT '' COMMENT '涓昏浜?,
    `speaker_title`     VARCHAR(100) DEFAULT '' COMMENT '涓昏浜鸿亴绉?,
    `lecture_time`      DATETIME     NOT NULL COMMENT '璁插骇鏃堕棿',
    `location`          VARCHAR(200) DEFAULT '' COMMENT '璁插骇鍦扮偣/绾夸笂閾炬帴',
    `max_participants`  INT          DEFAULT 0 COMMENT '鏈€澶ф姤鍚嶄汉鏁帮紝0琛ㄧず涓嶉檺',
    `current_participants` INT       DEFAULT 0 COMMENT '褰撳墠鎶ュ悕浜烘暟',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '鐘舵€侊細DRAFT/OPEN/CLOSED/FINISHED',
    `registration_deadline` DATETIME DEFAULT NULL COMMENT '鎶ュ悕鎴鏃堕棿',
    `created_at`        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_lecture_time` (`lecture_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁插骇閫氱煡琛?;

CREATE TABLE IF NOT EXISTS `tech_lecture_reg` (
    `id`          BIGINT    AUTO_INCREMENT PRIMARY KEY,
    `lecture_id`  BIGINT    NOT NULL COMMENT '璁插骇ID',
    `user_id`     BIGINT    NOT NULL COMMENT '鎶ュ悕鑰匢D',
    `status`      TINYINT   NOT NULL DEFAULT 0 COMMENT '0-宸叉姤鍚?1-宸茬鍒?2-宸插彇娑?,
    `created_at`  DATETIME  DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT   NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_lecture_user` (`lecture_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁插骇鎶ュ悕琛?;

CREATE TABLE IF NOT EXISTS `sys_sensitive_word` (
    `id`         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    `word`       VARCHAR(50) NOT NULL COMMENT '鏁忔劅璇?,
    `level`      TINYINT     NOT NULL DEFAULT 1 COMMENT '鏁忔劅绛夌骇锛?-涓€鑸?2-涓ラ噸',
    `created_at` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏁忔劅璇嶈〃';

CREATE TABLE IF NOT EXISTS `sys_file` (
    `id`            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT       DEFAULT NULL COMMENT '涓婁紶鑰匢D',
    `original_name` VARCHAR(200) NOT NULL COMMENT '鍘熷鏂囦欢鍚?,
    `file_name`     VARCHAR(200) NOT NULL COMMENT '瀛樺偍鏂囦欢鍚嶏紙UUID锛?,
    `file_path`     VARCHAR(500) NOT NULL COMMENT '鏂囦欢瀛樺偍璺緞',
    `file_url`      VARCHAR(500) NOT NULL COMMENT '鏂囦欢璁块棶URL',
    `file_size`     BIGINT       DEFAULT 0 COMMENT '鏂囦欢澶у皬锛堝瓧鑺傦級',
    `content_type`  VARCHAR(100) DEFAULT '' COMMENT 'MIME绫诲瀷',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏂囦欢璧勬簮琛?;

CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT       DEFAULT NULL COMMENT '鎿嶄綔鑰匢D',
    `username`    VARCHAR(50)  DEFAULT '' COMMENT '鎿嶄綔鑰呯敤鎴峰悕',
    `module`      VARCHAR(50)  DEFAULT '' COMMENT '妯″潡鍚嶇О',
    `type`        VARCHAR(20)  DEFAULT '' COMMENT '鎿嶄綔绫诲瀷',
    `description` VARCHAR(200) DEFAULT '' COMMENT '鎿嶄綔鎻忚堪',
    `method`      VARCHAR(200) DEFAULT '' COMMENT '璇锋眰鏂规硶',
    `url`         VARCHAR(500) DEFAULT '' COMMENT '璇锋眰URL',
    `params`      TEXT         COMMENT '璇锋眰鍙傛暟',
    `result`      TINYINT      DEFAULT 1 COMMENT '缁撴灉锛?-鎴愬姛 0-澶辫触',
    `error_msg`   TEXT         COMMENT '閿欒淇℃伅',
    `cost_time`   BIGINT       DEFAULT 0 COMMENT '鑰楁椂锛堟绉掞級',
    `ip`          VARCHAR(50)  DEFAULT '' COMMENT '鎿嶄綔IP',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_module` (`module`),
    INDEX `idx_type` (`type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎿嶄綔鏃ュ織琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V10__recommend_module.sql
-- Tables: 3
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    article_id BIGINT NOT NULL COMMENT '鏂囩珷ID',
    behavior_type VARCHAR(50) NOT NULL COMMENT 'view/like/collect/comment/question/dislike',
    stay_seconds INT DEFAULT 0 COMMENT '鍋滅暀鏃堕棿',
    weight DOUBLE DEFAULT 1.0 COMMENT '琛屼负鏉冮噸',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_article_id (article_id),
    INDEX idx_behavior_type (behavior_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鎺ㄨ崘琛屼负琛?;

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    interest_tags TEXT COMMENT '鍏磋叮鏍囩JSON',
    crop_preference VARCHAR(255) COMMENT '鍋忓ソ浣滅墿',
    tech_preference VARCHAR(255) COMMENT '鍋忓ソ鎶€鏈?,
    region_preference VARCHAR(255) COMMENT '鍋忓ソ鍦板尯',
    profile_text TEXT COMMENT '鐢ㄦ埛鐢诲儚鑷劧璇█鎻忚堪',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鍏磋叮鐢诲儚琛?;

CREATE TABLE IF NOT EXISTS recommend_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    article_id BIGINT NOT NULL COMMENT '鎺ㄨ崘鏂囩珷ID',
    score DOUBLE COMMENT '鎺ㄨ崘寰楀垎',
    reason TEXT COMMENT '鎺ㄨ崘鐞嗙敱',
    strategy VARCHAR(100) COMMENT '鎺ㄨ崘绛栫暐锛歵ag_match/vector_sim/hot/cold_start/trust',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_article_id (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎺ㄨ崘瀹¤鏃ュ織琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V11__yolo_detection.sql
-- Tables: 1
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS yolo_detection_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '涓婚敭ID',
    question_id BIGINT NOT NULL COMMENT '鍐滄埛闂ID',
    disease_name VARCHAR(100) COMMENT '璇嗗埆鍑虹殑鐥呰櫕瀹冲悕绉?,
    confidence DECIMAL(5,4) COMMENT '鏈€楂樼疆淇″害',
    prediction_json TEXT COMMENT '瀹屾暣璇嗗埆缁撴灉JSON',
    result_image_url VARCHAR(255) COMMENT '璇嗗埆缁撴灉鍥剧墖鍦板潃锛屽彲涓虹┖',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='YOLO鐥呰櫕瀹宠瘑鍒粨鏋滆〃';

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V12__smartqa_tables.sql
-- Tables: 6
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS agri_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '闂ID',
    user_id BIGINT NOT NULL COMMENT '鍐滄埛鐢ㄦ埛ID',
    title VARCHAR(100) NOT NULL COMMENT '闂鏍囬',
    crop_type VARCHAR(50) COMMENT '浣滅墿绫诲瀷',
    category_id BIGINT COMMENT '闂鍒嗙被ID',
    description TEXT COMMENT '闂鎻忚堪',
    region VARCHAR(100) COMMENT '鍦板尯',
    growth_stage VARCHAR(50) COMMENT '鐢熼暱闃舵',
    question_status TINYINT DEFAULT 0 COMMENT '0寰呰瘑鍒?1YOLO宸茶瘑鍒?2Agent宸插洖绛?3鑷姩鍥炲鎴愬姛 4寰呬笓瀹跺鏍?5涓撳宸插洖澶?6宸茶В鍐?7宸插叧闂?,
    yolo_status TINYINT DEFAULT 0 COMMENT '0鏈瘑鍒?1璇嗗埆鎴愬姛 2璇嗗埆澶辫触 3鏃犻渶璇嗗埆',
    agent_status TINYINT DEFAULT 0 COMMENT '0鏈皟鐢?1宸茬敓鎴愬洖绛?2鐢熸垚澶辫触 3璇勫垎閫氳繃 4璇勫垎鏈€氳繃',
    view_count INT DEFAULT 0 COMMENT '娴忚閲?,
    answer_count INT DEFAULT 0 COMMENT '鍥炵瓟鏁?,
    best_answer_id BIGINT COMMENT '鏈€浣崇瓟妗圛D',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_user_id (user_id),
    INDEX idx_question_status (question_status),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滄埛闂琛?;

CREATE TABLE IF NOT EXISTS question_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '鍥剧墖ID',
    question_id BIGINT NOT NULL COMMENT '闂ID',
    image_url VARCHAR(255) NOT NULL COMMENT '鍥剧墖鍦板潃',
    original_name VARCHAR(255) COMMENT '鍘熷鏂囦欢鍚?,
    file_size BIGINT COMMENT '鏂囦欢澶у皬',
    file_type VARCHAR(20) COMMENT '鏂囦欢绫诲瀷',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闂鍥剧墖琛?;

CREATE TABLE IF NOT EXISTS yolo_detection_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '璇嗗埆缁撴灉ID',
    question_id BIGINT NOT NULL COMMENT '闂ID',
    image_id BIGINT COMMENT '鍥剧墖ID',
    model_id VARCHAR(100) DEFAULT 'pest-detection-yolov8/1' COMMENT '妯″瀷ID',
    disease_name VARCHAR(100) COMMENT '璇嗗埆鍑虹殑鐥呰櫕瀹冲悕绉?,
    confidence DECIMAL(5,4) COMMENT '鏈€楂樼疆淇″害',
    prediction_json TEXT COMMENT '瀹屾暣璇嗗埆缁撴灉JSON',
    result_image_url VARCHAR(255) COMMENT '甯︽娴嬫缁撴灉鍥惧湴鍧€',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_question_id (question_id),
    INDEX idx_image_id (image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='YOLO璇嗗埆缁撴灉琛?;

CREATE TABLE IF NOT EXISTS agent_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Agent鍥炵瓟ID',
    question_id BIGINT NOT NULL COMMENT '闂ID',
    diagnosis VARCHAR(255) COMMENT '鍒濇璇婃柇',
    cause_analysis TEXT COMMENT '鍘熷洜鍒嗘瀽',
    treatment_suggestion TEXT COMMENT '澶勭悊寤鸿',
    prevention_advice TEXT COMMENT '棰勯槻鎺柦',
    risk_level VARCHAR(50) COMMENT '椋庨櫓绛夌骇',
    need_expert_review TINYINT DEFAULT 1 COMMENT '鏄惁闇€瑕佷笓瀹跺鏍?,
    answer_content TEXT COMMENT '瀹屾暣鍥炵瓟鍐呭',
    score INT DEFAULT 0 COMMENT '鍥炵瓟璇勫垎',
    returned_to_farmer TINYINT DEFAULT 0 COMMENT '鏄惁宸茶嚜鍔ㄨ繑鍥炲啘鎴?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent璇婃柇鍥炵瓟琛?;

CREATE TABLE IF NOT EXISTS answer_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '璇勫垎ID',
    question_id BIGINT NOT NULL COMMENT '闂ID',
    agent_answer_id BIGINT NOT NULL COMMENT 'Agent鍥炵瓟ID',
    yolo_score INT DEFAULT 0 COMMENT 'YOLO缃俊搴﹀緱鍒?,
    crop_match_score INT DEFAULT 0 COMMENT '浣滅墿鍖归厤寰楀垎',
    completeness_score INT DEFAULT 0 COMMENT '瀹屾暣鎬у緱鍒?,
    safety_score INT DEFAULT 0 COMMENT '瀹夊叏鎬у緱鍒?,
    clarity_score INT DEFAULT 0 COMMENT '娓呮櫚搴﹀緱鍒?,
    total_score INT DEFAULT 0 COMMENT '鎬诲垎',
    score_reason TEXT COMMENT '璇勫垎鍘熷洜',
    can_return_to_farmer TINYINT DEFAULT 0 COMMENT '鏄惁鍙互杩斿洖鍐滄埛',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_question_id (question_id),
    INDEX idx_agent_answer_id (agent_answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍥炵瓟璇勫垎琛?;

CREATE TABLE IF NOT EXISTS expert_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '涓撳鍥炵瓟ID',
    question_id BIGINT NOT NULL COMMENT '闂ID',
    expert_id BIGINT NOT NULL COMMENT '涓撳ID',
    answer_content TEXT NOT NULL COMMENT '涓撳鍥炵瓟鍐呭',
    reference_agent_answer TINYINT DEFAULT 0 COMMENT '鏄惁鍙傝€傾gent鍥炵瓟',
    expert_score INT COMMENT '涓撳瀵笰gent鍥炵瓟璇勫垎',
    is_best TINYINT DEFAULT 0 COMMENT '鏄惁鏈€浣崇瓟妗?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    deleted TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX idx_question_id (question_id),
    INDEX idx_expert_id (expert_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='涓撳鍥炵瓟琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V16__ensure_smartqa_review_tables.sql
-- Tables: 6
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS agri_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    crop_type VARCHAR(50),
    category_id BIGINT,
    description TEXT,
    region VARCHAR(100),
    growth_stage VARCHAR(50),
    question_status TINYINT DEFAULT 0,
    yolo_status TINYINT DEFAULT 0,
    agent_status TINYINT DEFAULT 0,
    view_count INT DEFAULT 0,
    answer_count INT DEFAULT 0,
    best_answer_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_agri_question_user_id (user_id),
    INDEX idx_agri_question_status (question_status),
    INDEX idx_agri_question_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS question_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_size BIGINT,
    file_type VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_question_image_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS yolo_detection_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    image_id BIGINT,
    model_id VARCHAR(100) DEFAULT 'pest-detection-yolov8/1',
    disease_name VARCHAR(100),
    confidence DECIMAL(5,4),
    prediction_json TEXT,
    result_image_url VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_yolo_result_question_id (question_id),
    INDEX idx_yolo_result_image_id (image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agent_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    diagnosis VARCHAR(255),
    cause_analysis TEXT,
    treatment_suggestion TEXT,
    prevention_advice TEXT,
    risk_level VARCHAR(50),
    need_expert_review TINYINT DEFAULT 1,
    answer_content TEXT,
    score INT DEFAULT 0,
    returned_to_farmer TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_agent_answer_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS answer_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    agent_answer_id BIGINT NOT NULL,
    yolo_score INT DEFAULT 0,
    crop_match_score INT DEFAULT 0,
    completeness_score INT DEFAULT 0,
    safety_score INT DEFAULT 0,
    clarity_score INT DEFAULT 0,
    total_score INT DEFAULT 0,
    score_reason TEXT,
    can_return_to_farmer TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_answer_score_question_id (question_id),
    INDEX idx_answer_score_agent_answer_id (agent_answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS expert_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    expert_id BIGINT NOT NULL,
    answer_content TEXT NOT NULL,
    reference_agent_answer TINYINT DEFAULT 0,
    expert_score INT,
    is_best TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_expert_answer_question_id (question_id),
    INDEX idx_expert_answer_expert_id (expert_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V17__enhance_article_comment.sql
-- Tables: 1
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tech_article_comment_like` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `comment_id` BIGINT NOT NULL COMMENT '璇勮ID',
    `user_id`    BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT  NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璇勮鐐硅禐琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V18__video_module.sql
-- Tables: 3
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `agri_video` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '瑙嗛ID',
    `title`           VARCHAR(100) NOT NULL COMMENT '瑙嗛鏍囬',
    `description`     TEXT COMMENT '瑙嗛绠€浠?,
    `category_id`     BIGINT COMMENT '瑙嗛鍒嗙被ID',
    `category_name`   VARCHAR(50) COMMENT '鍒嗙被鍚嶇О',
    `crop_type`       VARCHAR(50) COMMENT '浣滅墿绫诲瀷',
    `tags`            VARCHAR(255) COMMENT '鏍囩锛岄€楀彿鍒嗛殧',
    `file_name`       VARCHAR(255) NOT NULL COMMENT '瑙嗛鏂囦欢鍚?,
    `video_url`       VARCHAR(255) NOT NULL COMMENT '瑙嗛璁块棶鍦板潃',
    `cover_url`       VARCHAR(255) COMMENT '灏侀潰鍥惧湴鍧€',
    `duration`        INT DEFAULT 0 COMMENT '瑙嗛鏃堕暱锛屽崟浣嶇',
    `view_count`      INT DEFAULT 0 COMMENT '鎾斁閲?,
    `like_count`      INT DEFAULT 0 COMMENT '鐐硅禐鏁?,
    `favorite_count`  INT DEFAULT 0 COMMENT '鏀惰棌鏁?,
    `recommend_weight` INT DEFAULT 0 COMMENT '浜哄伐鎺ㄨ崘鏉冮噸',
    `status`          TINYINT DEFAULT 1 COMMENT '1鍚敤 0绂佺敤',
    `create_user_id`  BIGINT COMMENT '鍒涘缓浜篒D',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`         TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    UNIQUE KEY `uk_file_name` (`file_name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_crop_type` (`crop_type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍐滄妧瑙嗛琛?;

CREATE TABLE IF NOT EXISTS `video_favorite` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `video_id`    BIGINT NOT NULL COMMENT '瑙嗛ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鏀惰棌鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插彇娑?,
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙嗛鏀惰棌琛?;

CREATE TABLE IF NOT EXISTS `video_like` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `video_id`    BIGINT NOT NULL COMMENT '瑙嗛ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鐐硅禐鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插彇娑?,
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    INDEX `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='瑙嗛鐐硅禐琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\main\resources\db\migration\V19__video_recommend_enhance.sql
-- Tables: 1
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_profile_tag` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '鐢诲儚鏍囩ID',
    `user_id`    BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `tag_name`   VARCHAR(50) NOT NULL COMMENT '鏍囩鍚嶇О',
    `tag_type`   VARCHAR(30) COMMENT 'CROP/CATEGORY/TAG',
    `weight`     INT DEFAULT 0 COMMENT '鏉冮噸',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT DEFAULT 0 COMMENT '0鏈垹闄?1宸插垹闄?,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鐢诲儚鏍囩琛?;

-- ------------------------------------------------------------
-- Source: kuangjia\agri-promo-service\src\test\resources\schema-h2.sql
-- Tables: 22
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(50) DEFAULT '',
    phone VARCHAR(20) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    avatar VARCHAR(500) DEFAULT '',
    status INT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL,
    sort_order INT DEFAULT 0,
    description VARCHAR(200) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS crop_dict (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL,
    icon VARCHAR(500) DEFAULT '',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    tag_type VARCHAR(50) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auditor_id BIGINT,
    category_id BIGINT,
    crop_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content CLOB,
    summary VARCHAR(500) DEFAULT '',
    cover_image VARCHAR(500) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    reject_reason VARCHAR(500) DEFAULT '',
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    published_at TIMESTAMP,
    source_url VARCHAR(500) DEFAULT '',
    source VARCHAR(100) DEFAULT '',
    trusted_level VARCHAR(50) DEFAULT 'normal',
    verified_by VARCHAR(100) DEFAULT '',
    safety_tip CLOB,
    crop_type VARCHAR(100) DEFAULT '',
    region VARCHAR(100) DEFAULT '',
    season VARCHAR(100) DEFAULT '',
    difficulty VARCHAR(50) DEFAULT '涓?,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    weight DOUBLE DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    behavior_type VARCHAR(50) NOT NULL,
    stay_seconds INT DEFAULT 0,
    weight DOUBLE DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interest_tags CLOB,
    crop_preference VARCHAR(255),
    tech_preference VARCHAR(255),
    region_preference VARCHAR(255),
    profile_text CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS recommend_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    score DOUBLE,
    reason CLOB,
    strategy VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    content VARCHAR(1000) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_article_view_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    user_id BIGINT,
    ip VARCHAR(50) DEFAULT '',
    user_agent VARCHAR(500) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    crop_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content CLOB,
    image_urls CLOB,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    view_count INT DEFAULT 0,
    answer_count INT DEFAULT 0,
    best_answer_id BIGINT,
    yolo_status INT DEFAULT 0,
    yolo_disease_name VARCHAR(100),
    yolo_confidence DECIMAL(5,4),
    agent_suggestion CLOB,
    agent_score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content CLOB NOT NULL,
    is_accepted INT NOT NULL DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_lecture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content CLOB,
    summary VARCHAR(500) DEFAULT '',
    cover_image VARCHAR(500) DEFAULT '',
    speaker_name VARCHAR(100) DEFAULT '',
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    join_url VARCHAR(500) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    register_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tech_lecture_reg (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_sensitive_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(50) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    original_name VARCHAR(200) NOT NULL,
    file_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT DEFAULT 0,
    content_type VARCHAR(100) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50) DEFAULT '',
    module VARCHAR(50) DEFAULT '',
    type VARCHAR(20) DEFAULT '',
    description VARCHAR(200) DEFAULT '',
    method VARCHAR(200) DEFAULT '',
    url VARCHAR(500) DEFAULT '',
    params CLOB,
    result INT DEFAULT 1,
    error_msg CLOB,
    cost_time BIGINT DEFAULT 0,
    ip VARCHAR(50) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0
);

-- ------------------------------------------------------------
-- Source: kuangjia\news-please\newsplease\init-postgresql-db.sql
-- Tables: 2
-- ------------------------------------------------------------
CREATE TABLE ArchiveVersions (
  id SERIAL PRIMARY KEY,
  date_modify timestamp(0) NOT NULL,
  date_download timestamp(0) NOT NULL,
  localpath varchar(255) NOT NULL,
  filename varchar(2000) NOT NULL,
  source_domain varchar(255) NOT NULL,
  url varchar(2000) NOT NULL,
  image_url varchar(2000),
  title varchar(255) NOT NULL,
  title_page varchar(255) NOT NULL,
  title_rss varchar(255),
  maintext text NOT NULL,
  description text,
  date_publish timestamp(0),
  authors varchar(255) ARRAY,
  language varchar(255),
  ancestor int NOT NULL DEFAULT 0,
  descendant int NOT NULL,
  version int NOT NULL DEFAULT 2
);

CREATE TABLE CurrentVersions (
  id SERIAL PRIMARY KEY,
  date_modify timestamp(0) NOT NULL,
  date_download timestamp(0) NOT NULL,
  localpath varchar(255) NOT NULL,
  filename varchar(2000) NOT NULL,
  source_domain varchar(255) NOT NULL,
  url varchar(2000) NOT NULL,
  image_url varchar(2000),
  title varchar(255) NOT NULL,
  title_page varchar(255) NOT NULL,
  title_rss varchar(255),
  maintext text NOT NULL,
  description text,
  date_publish timestamp(0),
  authors varchar(255) ARRAY,
  language varchar(255),
  ancestor int NOT NULL DEFAULT 0,
  descendant int NOT NULL DEFAULT 0,
  version int NOT NULL DEFAULT 1,
  CONSTRAINT unique_url UNIQUE(url)
);

-- ------------------------------------------------------------
-- Source: kuangjia\sql\recommend_module.sql
-- Tables: 3
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    article_id BIGINT NOT NULL COMMENT '鏂囩珷ID',
    behavior_type VARCHAR(50) NOT NULL COMMENT 'view/like/collect/comment/question/dislike',
    stay_seconds INT DEFAULT 0 COMMENT '鍋滅暀鏃堕棿',
    weight DOUBLE DEFAULT 1.0 COMMENT '琛屼负鏉冮噸',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_article_id (article_id),
    INDEX idx_behavior_type (behavior_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鎺ㄨ崘琛屼负琛?;

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    interest_tags TEXT COMMENT '鍏磋叮鏍囩JSON',
    crop_preference VARCHAR(255) COMMENT '鍋忓ソ浣滅墿',
    tech_preference VARCHAR(255) COMMENT '鍋忓ソ鎶€鏈?,
    region_preference VARCHAR(255) COMMENT '鍋忓ソ鍦板尯',
    profile_text TEXT COMMENT '鐢ㄦ埛鐢诲儚鑷劧璇█鎻忚堪',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛鍏磋叮鐢诲儚琛?;

CREATE TABLE IF NOT EXISTS recommend_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    article_id BIGINT NOT NULL COMMENT '鎺ㄨ崘鏂囩珷ID',
    score DOUBLE COMMENT '鎺ㄨ崘寰楀垎',
    reason TEXT COMMENT '鎺ㄨ崘鐞嗙敱',
    strategy VARCHAR(100) COMMENT '鎺ㄨ崘绛栫暐锛歵ag_match/vector_sim/hot/cold_start/trust',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_article_id (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎺ㄨ崘瀹¤鏃ュ織琛?;

-- ------------------------------------------------------------
-- Source: ruanjiansheji\HUIZHI-nongyeOS-cloud\sql\jl_20230223.sql
-- Tables: 19
-- ------------------------------------------------------------
create table sys_dept (
  dept_id           bigint(20)      not null auto_increment    comment '閮ㄩ棬id',
  parent_id         bigint(20)      default 0                  comment '鐖堕儴闂╥d',
  ancestors         varchar(50)     default ''                 comment '绁栫骇鍒楄〃',
  dept_name         varchar(30)     default ''                 comment '閮ㄩ棬鍚嶇О',
  order_num         int(4)          default 0                  comment '鏄剧ず椤哄簭',
  leader            varchar(20)     default null               comment '璐熻矗浜?,
  phone             varchar(11)     default null               comment '鑱旂郴鐢佃瘽',
  email             varchar(50)     default null               comment '閭',
  status            char(1)         default '0'                comment '閮ㄩ棬鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  del_flag          char(1)         default '0'                comment '鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?,
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time 	    datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment = '閮ㄩ棬琛?;

create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '鐢ㄦ埛ID',
  dept_id           bigint(20)      default null               comment '閮ㄩ棬ID',
  user_name         varchar(30)     not null                   comment '鐢ㄦ埛璐﹀彿',
  nick_name         varchar(30)     not null                   comment '鐢ㄦ埛鏄电О',
  user_type         varchar(2)      default '00'               comment '鐢ㄦ埛绫诲瀷锛?0绯荤粺鐢ㄦ埛锛?,
  email             varchar(50)     default ''                 comment '鐢ㄦ埛閭',
  phonenumber       varchar(11)     default ''                 comment '鎵嬫満鍙风爜',
  sex               char(1)         default '0'                comment '鐢ㄦ埛鎬у埆锛?鐢?1濂?2鏈煡锛?,
  avatar            varchar(100)    default ''                 comment '澶村儚鍦板潃',
  password          varchar(100)    default ''                 comment '瀵嗙爜',
  status            char(1)         default '0'                comment '甯愬彿鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  del_flag          char(1)         default '0'                comment '鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?,
  login_ip          varchar(128)    default ''                 comment '鏈€鍚庣櫥褰旾P',
  login_date        datetime                                   comment '鏈€鍚庣櫥褰曟椂闂?,
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time       datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  remark            varchar(500)    default null               comment '澶囨敞',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '鐢ㄦ埛淇℃伅琛?;

create table sys_post
(
  post_id       bigint(20)      not null auto_increment    comment '宀椾綅ID',
  post_code     varchar(64)     not null                   comment '宀椾綅缂栫爜',
  post_name     varchar(50)     not null                   comment '宀椾綅鍚嶇О',
  post_sort     int(4)          not null                   comment '鏄剧ず椤哄簭',
  status        char(1)         not null                   comment '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  create_by     varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time   datetime                                   comment '鍒涘缓鏃堕棿',
  update_by     varchar(64)     default ''			       comment '鏇存柊鑰?,
  update_time   datetime                                   comment '鏇存柊鏃堕棿',
  remark        varchar(500)    default null               comment '澶囨敞',
  primary key (post_id)
) engine=innodb comment = '宀椾綅淇℃伅琛?;

create table sys_role (
  role_id              bigint(20)      not null auto_increment    comment '瑙掕壊ID',
  role_name            varchar(30)     not null                   comment '瑙掕壊鍚嶇О',
  role_key             varchar(100)    not null                   comment '瑙掕壊鏉冮檺瀛楃涓?,
  role_sort            int(4)          not null                   comment '鏄剧ず椤哄簭',
  data_scope           char(1)         default '1'                comment '鏁版嵁鑼冨洿锛?锛氬叏閮ㄦ暟鎹潈闄?2锛氳嚜瀹氭暟鎹潈闄?3锛氭湰閮ㄩ棬鏁版嵁鏉冮檺 4锛氭湰閮ㄩ棬鍙婁互涓嬫暟鎹潈闄愶級',
  menu_check_strictly  tinyint(1)      default 1                  comment '鑿滃崟鏍戦€夋嫨椤规槸鍚﹀叧鑱旀樉绀?,
  dept_check_strictly  tinyint(1)      default 1                  comment '閮ㄩ棬鏍戦€夋嫨椤规槸鍚﹀叧鑱旀樉绀?,
  status               char(1)         not null                   comment '瑙掕壊鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  del_flag             char(1)         default '0'                comment '鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?,
  create_by            varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time          datetime                                   comment '鍒涘缓鏃堕棿',
  update_by            varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time          datetime                                   comment '鏇存柊鏃堕棿',
  remark               varchar(500)    default null               comment '澶囨敞',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '瑙掕壊淇℃伅琛?;

create table sys_menu (
  menu_id           bigint(20)      not null auto_increment    comment '鑿滃崟ID',
  menu_name         varchar(50)     not null                   comment '鑿滃崟鍚嶇О',
  parent_id         bigint(20)      default 0                  comment '鐖惰彍鍗旾D',
  order_num         int(4)          default 0                  comment '鏄剧ず椤哄簭',
  path              varchar(200)    default ''                 comment '璺敱鍦板潃',
  component         varchar(255)    default null               comment '缁勪欢璺緞',
  query             varchar(255)    default null               comment '璺敱鍙傛暟',
  is_frame          int(1)          default 1                  comment '鏄惁涓哄閾撅紙0鏄?1鍚︼級',
  is_cache          int(1)          default 0                  comment '鏄惁缂撳瓨锛?缂撳瓨 1涓嶇紦瀛橈級',
  menu_type         char(1)         default ''                 comment '鑿滃崟绫诲瀷锛圡鐩綍 C鑿滃崟 F鎸夐挳锛?,
  visible           char(1)         default 0                  comment '鑿滃崟鐘舵€侊紙0鏄剧ず 1闅愯棌锛?,
  status            char(1)         default 0                  comment '鑿滃崟鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  perms             varchar(100)    default null               comment '鏉冮檺鏍囪瘑',
  icon              varchar(100)    default '#'                comment '鑿滃崟鍥炬爣',
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time       datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  remark            varchar(500)    default ''                 comment '澶囨敞',
  primary key (menu_id)
) engine=innodb auto_increment=2000 comment = '鑿滃崟鏉冮檺琛?;

create table sys_user_role (
  user_id   bigint(20) not null comment '鐢ㄦ埛ID',
  role_id   bigint(20) not null comment '瑙掕壊ID',
  primary key(user_id, role_id)
) engine=innodb comment = '鐢ㄦ埛鍜岃鑹插叧鑱旇〃';

create table sys_role_menu (
  role_id   bigint(20) not null comment '瑙掕壊ID',
  menu_id   bigint(20) not null comment '鑿滃崟ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '瑙掕壊鍜岃彍鍗曞叧鑱旇〃';

create table sys_role_dept (
  role_id   bigint(20) not null comment '瑙掕壊ID',
  dept_id   bigint(20) not null comment '閮ㄩ棬ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '瑙掕壊鍜岄儴闂ㄥ叧鑱旇〃';

create table sys_user_post
(
  user_id   bigint(20) not null comment '鐢ㄦ埛ID',
  post_id   bigint(20) not null comment '宀椾綅ID',
  primary key (user_id, post_id)
) engine=innodb comment = '鐢ㄦ埛涓庡矖浣嶅叧鑱旇〃';

create table sys_oper_log (
  oper_id           bigint(20)      not null auto_increment    comment '鏃ュ織涓婚敭',
  title             varchar(50)     default ''                 comment '妯″潡鏍囬',
  business_type     int(2)          default 0                  comment '涓氬姟绫诲瀷锛?鍏跺畠 1鏂板 2淇敼 3鍒犻櫎锛?,
  method            varchar(100)    default ''                 comment '鏂规硶鍚嶇О',
  request_method    varchar(10)     default ''                 comment '璇锋眰鏂瑰紡',
  operator_type     int(1)          default 0                  comment '鎿嶄綔绫诲埆锛?鍏跺畠 1鍚庡彴鐢ㄦ埛 2鎵嬫満绔敤鎴凤級',
  oper_name         varchar(50)     default ''                 comment '鎿嶄綔浜哄憳',
  dept_name         varchar(50)     default ''                 comment '閮ㄩ棬鍚嶇О',
  oper_url          varchar(255)    default ''                 comment '璇锋眰URL',
  oper_ip           varchar(128)    default ''                 comment '涓绘満鍦板潃',
  oper_location     varchar(255)    default ''                 comment '鎿嶄綔鍦扮偣',
  oper_param        varchar(2000)   default ''                 comment '璇锋眰鍙傛暟',
  json_result       varchar(2000)   default ''                 comment '杩斿洖鍙傛暟',
  status            int(1)          default 0                  comment '鎿嶄綔鐘舵€侊紙0姝ｅ父 1寮傚父锛?,
  error_msg         varchar(2000)   default ''                 comment '閿欒娑堟伅',
  oper_time         datetime                                   comment '鎿嶄綔鏃堕棿',
  cost_time         bigint(20)      default 0                  comment '娑堣€楁椂闂?,
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb auto_increment=100 comment = '鎿嶄綔鏃ュ織璁板綍';

create table sys_dict_type
(
  dict_id          bigint(20)      not null auto_increment    comment '瀛楀吀涓婚敭',
  dict_name        varchar(100)    default ''                 comment '瀛楀吀鍚嶇О',
  dict_type        varchar(100)    default ''                 comment '瀛楀吀绫诲瀷',
  status           char(1)         default '0'                comment '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  create_by        varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time      datetime                                   comment '鍒涘缓鏃堕棿',
  update_by        varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time      datetime                                   comment '鏇存柊鏃堕棿',
  remark           varchar(500)    default null               comment '澶囨敞',
  primary key (dict_id),
  unique (dict_type)
) engine=innodb auto_increment=100 comment = '瀛楀吀绫诲瀷琛?;

create table sys_dict_data
(
  dict_code        bigint(20)      not null auto_increment    comment '瀛楀吀缂栫爜',
  dict_sort        int(4)          default 0                  comment '瀛楀吀鎺掑簭',
  dict_label       varchar(100)    default ''                 comment '瀛楀吀鏍囩',
  dict_value       varchar(100)    default ''                 comment '瀛楀吀閿€?,
  dict_type        varchar(100)    default ''                 comment '瀛楀吀绫诲瀷',
  css_class        varchar(100)    default null               comment '鏍峰紡灞炴€э紙鍏朵粬鏍峰紡鎵╁睍锛?,
  list_class       varchar(100)    default null               comment '琛ㄦ牸鍥炴樉鏍峰紡',
  is_default       char(1)         default 'N'                comment '鏄惁榛樿锛圷鏄?N鍚︼級',
  status           char(1)         default '0'                comment '鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?,
  create_by        varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time      datetime                                   comment '鍒涘缓鏃堕棿',
  update_by        varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time      datetime                                   comment '鏇存柊鏃堕棿',
  remark           varchar(500)    default null               comment '澶囨敞',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment = '瀛楀吀鏁版嵁琛?;

create table sys_config (
  config_id         int(5)          not null auto_increment    comment '鍙傛暟涓婚敭',
  config_name       varchar(100)    default ''                 comment '鍙傛暟鍚嶇О',
  config_key        varchar(100)    default ''                 comment '鍙傛暟閿悕',
  config_value      varchar(500)    default ''                 comment '鍙傛暟閿€?,
  config_type       char(1)         default 'N'                comment '绯荤粺鍐呯疆锛圷鏄?N鍚︼級',
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time       datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  remark            varchar(500)    default null               comment '澶囨敞',
  primary key (config_id)
) engine=innodb auto_increment=100 comment = '鍙傛暟閰嶇疆琛?;

create table sys_logininfor (
  info_id        bigint(20)     not null auto_increment   comment '璁块棶ID',
  user_name      varchar(50)    default ''                comment '鐢ㄦ埛璐﹀彿',
  ipaddr         varchar(128)   default ''                comment '鐧诲綍IP鍦板潃',
  status         char(1)        default '0'               comment '鐧诲綍鐘舵€侊紙0鎴愬姛 1澶辫触锛?,
  msg            varchar(255)   default ''                comment '鎻愮ず淇℃伅',
  access_time    datetime                                 comment '璁块棶鏃堕棿',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (access_time)
) engine=innodb auto_increment=100 comment = '绯荤粺璁块棶璁板綍';

create table sys_job (
  job_id              bigint(20)    not null auto_increment    comment '浠诲姟ID',
  job_name            varchar(64)   default ''                 comment '浠诲姟鍚嶇О',
  job_group           varchar(64)   default 'DEFAULT'          comment '浠诲姟缁勫悕',
  invoke_target       varchar(500)  not null                   comment '璋冪敤鐩爣瀛楃涓?,
  cron_expression     varchar(255)  default ''                 comment 'cron鎵ц琛ㄨ揪寮?,
  misfire_policy      varchar(20)   default '3'                comment '璁″垝鎵ц閿欒绛栫暐锛?绔嬪嵆鎵ц 2鎵ц涓€娆?3鏀惧純鎵ц锛?,
  concurrent          char(1)       default '1'                comment '鏄惁骞跺彂鎵ц锛?鍏佽 1绂佹锛?,
  status              char(1)       default '0'                comment '鐘舵€侊紙0姝ｅ父 1鏆傚仠锛?,
  create_by           varchar(64)   default ''                 comment '鍒涘缓鑰?,
  create_time         datetime                                 comment '鍒涘缓鏃堕棿',
  update_by           varchar(64)   default ''                 comment '鏇存柊鑰?,
  update_time         datetime                                 comment '鏇存柊鏃堕棿',
  remark              varchar(500)  default ''                 comment '澶囨敞淇℃伅',
  primary key (job_id, job_name, job_group)
) engine=innodb auto_increment=100 comment = '瀹氭椂浠诲姟璋冨害琛?;

create table sys_job_log (
  job_log_id          bigint(20)     not null auto_increment    comment '浠诲姟鏃ュ織ID',
  job_name            varchar(64)    not null                   comment '浠诲姟鍚嶇О',
  job_group           varchar(64)    not null                   comment '浠诲姟缁勫悕',
  invoke_target       varchar(500)   not null                   comment '璋冪敤鐩爣瀛楃涓?,
  job_message         varchar(500)                              comment '鏃ュ織淇℃伅',
  status              char(1)        default '0'                comment '鎵ц鐘舵€侊紙0姝ｅ父 1澶辫触锛?,
  exception_info      varchar(2000)  default ''                 comment '寮傚父淇℃伅',
  create_time         datetime                                  comment '鍒涘缓鏃堕棿',
  primary key (job_log_id)
) engine=innodb comment = '瀹氭椂浠诲姟璋冨害鏃ュ織琛?;

create table sys_notice (
  notice_id         int(4)          not null auto_increment    comment '鍏憡ID',
  notice_title      varchar(50)     not null                   comment '鍏憡鏍囬',
  notice_type       char(1)         not null                   comment '鍏憡绫诲瀷锛?閫氱煡 2鍏憡锛?,
  notice_content    longblob        default null               comment '鍏憡鍐呭',
  status            char(1)         default '0'                comment '鍏憡鐘舵€侊紙0姝ｅ父 1鍏抽棴锛?,
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time       datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  remark            varchar(255)    default null               comment '澶囨敞',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment = '閫氱煡鍏憡琛?;

create table gen_table (
  table_id          bigint(20)      not null auto_increment    comment '缂栧彿',
  table_name        varchar(200)    default ''                 comment '琛ㄥ悕绉?,
  table_comment     varchar(500)    default ''                 comment '琛ㄦ弿杩?,
  sub_table_name    varchar(64)     default null               comment '鍏宠仈瀛愯〃鐨勮〃鍚?,
  sub_table_fk_name varchar(64)     default null               comment '瀛愯〃鍏宠仈鐨勫閿悕',
  class_name        varchar(100)    default ''                 comment '瀹炰綋绫诲悕绉?,
  tpl_category      varchar(200)    default 'crud'             comment '浣跨敤鐨勬ā鏉匡紙crud鍗曡〃鎿嶄綔 tree鏍戣〃鎿嶄綔锛?,
  package_name      varchar(100)                               comment '鐢熸垚鍖呰矾寰?,
  module_name       varchar(30)                                comment '鐢熸垚妯″潡鍚?,
  business_name     varchar(30)                                comment '鐢熸垚涓氬姟鍚?,
  function_name     varchar(50)                                comment '鐢熸垚鍔熻兘鍚?,
  function_author   varchar(50)                                comment '鐢熸垚鍔熻兘浣滆€?,
  gen_type          char(1)         default '0'                comment '鐢熸垚浠ｇ爜鏂瑰紡锛?zip鍘嬬缉鍖?1鑷畾涔夎矾寰勶級',
  gen_path          varchar(200)    default '/'                comment '鐢熸垚璺緞锛堜笉濉粯璁ら」鐩矾寰勶級',
  options           varchar(1000)                              comment '鍏跺畠鐢熸垚閫夐」',
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time 	    datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  remark            varchar(500)    default null               comment '澶囨敞',
  primary key (table_id)
) engine=innodb auto_increment=1 comment = '浠ｇ爜鐢熸垚涓氬姟琛?;

create table gen_table_column (
  column_id         bigint(20)      not null auto_increment    comment '缂栧彿',
  table_id          varchar(64)                                comment '褰掑睘琛ㄧ紪鍙?,
  column_name       varchar(200)                               comment '鍒楀悕绉?,
  column_comment    varchar(500)                               comment '鍒楁弿杩?,
  column_type       varchar(100)                               comment '鍒楃被鍨?,
  java_type         varchar(500)                               comment 'JAVA绫诲瀷',
  java_field        varchar(200)                               comment 'JAVA瀛楁鍚?,
  is_pk             char(1)                                    comment '鏄惁涓婚敭锛?鏄級',
  is_increment      char(1)                                    comment '鏄惁鑷锛?鏄級',
  is_required       char(1)                                    comment '鏄惁蹇呭～锛?鏄級',
  is_insert         char(1)                                    comment '鏄惁涓烘彃鍏ュ瓧娈碉紙1鏄級',
  is_edit           char(1)                                    comment '鏄惁缂栬緫瀛楁锛?鏄級',
  is_list           char(1)                                    comment '鏄惁鍒楄〃瀛楁锛?鏄級',
  is_query          char(1)                                    comment '鏄惁鏌ヨ瀛楁锛?鏄級',
  query_type        varchar(200)    default 'EQ'               comment '鏌ヨ鏂瑰紡锛堢瓑浜庛€佷笉绛変簬銆佸ぇ浜庛€佸皬浜庛€佽寖鍥达級',
  html_type         varchar(200)                               comment '鏄剧ず绫诲瀷锛堟枃鏈銆佹枃鏈煙銆佷笅鎷夋銆佸閫夋銆佸崟閫夋銆佹棩鏈熸帶浠讹級',
  dict_type         varchar(200)    default ''                 comment '瀛楀吀绫诲瀷',
  sort              int                                        comment '鎺掑簭',
  create_by         varchar(64)     default ''                 comment '鍒涘缓鑰?,
  create_time 	    datetime                                   comment '鍒涘缓鏃堕棿',
  update_by         varchar(64)     default ''                 comment '鏇存柊鑰?,
  update_time       datetime                                   comment '鏇存柊鏃堕棿',
  primary key (column_id)
) engine=innodb auto_increment=1 comment = '浠ｇ爜鐢熸垚涓氬姟琛ㄥ瓧娈?;

-- ------------------------------------------------------------
-- Source: ruanjiansheji\HUIZHI-nongyeOS-cloud\sql\jl_config_20220929.sql
-- Tables: 12
-- ------------------------------------------------------------
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '淇敼鏃堕棿',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '绉熸埛瀛楁',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  `encrypted_data_key` text COMMENT '绉橀挜',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '鍐呭',
  `gmt_modified` datetime NOT NULL COMMENT '淇敼鏃堕棿',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '绉熸埛瀛楁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='澧炲姞绉熸埛瀛楁';

CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '淇敼鏃堕棿',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '绉熸埛瀛楁',
  `encrypted_data_key` text COMMENT '绉橀挜',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '淇敼鏃堕棿',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID锛岀┖瀛楃琛ㄧず鏁翠釜闆嗙兢',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閰嶉锛?琛ㄧず浣跨敤榛樿鍊?,
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '浣跨敤閲?,
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鍗曚釜閰嶇疆澶у皬涓婇檺锛屽崟浣嶄负瀛楄妭锛?琛ㄧず浣跨敤榛樿鍊?,
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鑱氬悎瀛愰厤缃渶澶т釜鏁帮紝锛?琛ㄧず浣跨敤榛樿鍊?,
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鍗曚釜鑱氬悎鏁版嵁鐨勫瓙閰嶇疆澶у皬涓婇檺锛屽崟浣嶄负瀛楄妭锛?琛ㄧず浣跨敤榛樿鍊?,
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鏈€澶у彉鏇村巻鍙叉暟閲?,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '淇敼鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='闆嗙兢銆佸悇Group瀹归噺淇℃伅琛?;

CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '绉熸埛瀛楁',
  `encrypted_data_key` text COMMENT '绉橀挜',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='澶氱鎴锋敼閫?;

CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閰嶉锛?琛ㄧず浣跨敤榛樿鍊?,
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '浣跨敤閲?,
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鍗曚釜閰嶇疆澶у皬涓婇檺锛屽崟浣嶄负瀛楄妭锛?琛ㄧず浣跨敤榛樿鍊?,
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鑱氬悎瀛愰厤缃渶澶т釜鏁?,
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鍗曚釜鑱氬悎鏁版嵁鐨勫瓙閰嶇疆澶у皬涓婇檺锛屽崟浣嶄负瀛楄妭锛?琛ㄧず浣跨敤榛樿鍊?,
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '鏈€澶у彉鏇村巻鍙叉暟閲?,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '淇敼鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='绉熸埛瀹归噺淇℃伅琛?;

CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '鍒涘缓鏃堕棿',
  `gmt_modified` bigint(20) NOT NULL COMMENT '淇敼鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
	`username` varchar(50) NOT NULL PRIMARY KEY,
	`password` varchar(500) NOT NULL,
	`enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
	`username` varchar(50) NOT NULL,
	`role` varchar(50) NOT NULL,
	UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

-- ------------------------------------------------------------
-- Source: ruanjiansheji\HUIZHI-nongyeOS-cloud\sql\jl_seata_20210128.sql
-- Tables: 4
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_gmt_modified_status` (`gmt_modified`, `status`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(96),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_branch_id` (`branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT(20)   NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(100) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';

-- ------------------------------------------------------------
-- Source: ruanjiansheji\HUIZHI-nongyeOS-cloud\sql\quartz.sql
-- Tables: 11
-- ------------------------------------------------------------
create table QRTZ_JOB_DETAILS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    job_name             varchar(200)    not null            comment '浠诲姟鍚嶇О',
    job_group            varchar(200)    not null            comment '浠诲姟缁勫悕',
    description          varchar(250)    null                comment '鐩稿叧浠嬬粛',
    job_class_name       varchar(250)    not null            comment '鎵ц浠诲姟绫诲悕绉?,
    is_durable           varchar(1)      not null            comment '鏄惁鎸佷箙鍖?,
    is_nonconcurrent     varchar(1)      not null            comment '鏄惁骞跺彂',
    is_update_data       varchar(1)      not null            comment '鏄惁鏇存柊鏁版嵁',
    requests_recovery    varchar(1)      not null            comment '鏄惁鎺ュ彈鎭㈠鎵ц',
    job_data             blob            null                comment '瀛樻斁鎸佷箙鍖杍ob瀵硅薄',
    primary key (sched_name, job_name, job_group)
) engine=innodb comment = '浠诲姟璇︾粏淇℃伅琛?;

create table QRTZ_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_name         varchar(200)    not null            comment '瑙﹀彂鍣ㄧ殑鍚嶅瓧',
    trigger_group        varchar(200)    not null            comment '瑙﹀彂鍣ㄦ墍灞炵粍鐨勫悕瀛?,
    job_name             varchar(200)    not null            comment 'qrtz_job_details琛╦ob_name鐨勫閿?,
    job_group            varchar(200)    not null            comment 'qrtz_job_details琛╦ob_group鐨勫閿?,
    description          varchar(250)    null                comment '鐩稿叧浠嬬粛',
    next_fire_time       bigint(13)      null                comment '涓婁竴娆¤Е鍙戞椂闂达紙姣锛?,
    prev_fire_time       bigint(13)      null                comment '涓嬩竴娆¤Е鍙戞椂闂达紙榛樿涓?1琛ㄧず涓嶈Е鍙戯級',
    priority             integer         null                comment '浼樺厛绾?,
    trigger_state        varchar(16)     not null            comment '瑙﹀彂鍣ㄧ姸鎬?,
    trigger_type         varchar(8)      not null            comment '瑙﹀彂鍣ㄧ殑绫诲瀷',
    start_time           bigint(13)      not null            comment '寮€濮嬫椂闂?,
    end_time             bigint(13)      null                comment '缁撴潫鏃堕棿',
    calendar_name        varchar(200)    null                comment '鏃ョ▼琛ㄥ悕绉?,
    misfire_instr        smallint(2)     null                comment '琛ュ伩鎵ц鐨勭瓥鐣?,
    job_data             blob            null                comment '瀛樻斁鎸佷箙鍖杍ob瀵硅薄',
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, job_name, job_group) references QRTZ_JOB_DETAILS(sched_name, job_name, job_group)
) engine=innodb comment = '瑙﹀彂鍣ㄨ缁嗕俊鎭〃';

create table QRTZ_SIMPLE_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_name         varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_name鐨勫閿?,
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    repeat_count         bigint(7)       not null            comment '閲嶅鐨勬鏁扮粺璁?,
    repeat_interval      bigint(12)      not null            comment '閲嶅鐨勯棿闅旀椂闂?,
    times_triggered      bigint(10)      not null            comment '宸茬粡瑙﹀彂鐨勬鏁?,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
) engine=innodb comment = '绠€鍗曡Е鍙戝櫒鐨勪俊鎭〃';

create table QRTZ_CRON_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_name         varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_name鐨勫閿?,
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    cron_expression      varchar(200)    not null            comment 'cron琛ㄨ揪寮?,
    time_zone_id         varchar(80)                         comment '鏃跺尯',
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
) engine=innodb comment = 'Cron绫诲瀷鐨勮Е鍙戝櫒琛?;

create table QRTZ_BLOB_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_name         varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_name鐨勫閿?,
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    blob_data            blob            null                comment '瀛樻斁鎸佷箙鍖朤rigger瀵硅薄',
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
) engine=innodb comment = 'Blob绫诲瀷鐨勮Е鍙戝櫒琛?;

create table QRTZ_CALENDARS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    calendar_name        varchar(200)    not null            comment '鏃ュ巻鍚嶇О',
    calendar             blob            not null            comment '瀛樻斁鎸佷箙鍖朿alendar瀵硅薄',
    primary key (sched_name, calendar_name)
) engine=innodb comment = '鏃ュ巻淇℃伅琛?;

create table QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    primary key (sched_name, trigger_group)
) engine=innodb comment = '鏆傚仠鐨勮Е鍙戝櫒琛?;

create table QRTZ_FIRED_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    entry_id             varchar(95)     not null            comment '璋冨害鍣ㄥ疄渚媔d',
    trigger_name         varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_name鐨勫閿?,
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    instance_name        varchar(200)    not null            comment '璋冨害鍣ㄥ疄渚嬪悕',
    fired_time           bigint(13)      not null            comment '瑙﹀彂鐨勬椂闂?,
    sched_time           bigint(13)      not null            comment '瀹氭椂鍣ㄥ埗瀹氱殑鏃堕棿',
    priority             integer         not null            comment '浼樺厛绾?,
    state                varchar(16)     not null            comment '鐘舵€?,
    job_name             varchar(200)    null                comment '浠诲姟鍚嶇О',
    job_group            varchar(200)    null                comment '浠诲姟缁勫悕',
    is_nonconcurrent     varchar(1)      null                comment '鏄惁骞跺彂',
    requests_recovery    varchar(1)      null                comment '鏄惁鎺ュ彈鎭㈠鎵ц',
    primary key (sched_name, entry_id)
) engine=innodb comment = '宸茶Е鍙戠殑瑙﹀彂鍣ㄨ〃';

create table QRTZ_SCHEDULER_STATE (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    instance_name        varchar(200)    not null            comment '瀹炰緥鍚嶇О',
    last_checkin_time    bigint(13)      not null            comment '涓婃妫€鏌ユ椂闂?,
    checkin_interval     bigint(13)      not null            comment '妫€鏌ラ棿闅旀椂闂?,
    primary key (sched_name, instance_name)
) engine=innodb comment = '璋冨害鍣ㄧ姸鎬佽〃';

create table QRTZ_LOCKS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    lock_name            varchar(40)     not null            comment '鎮茶閿佸悕绉?,
    primary key (sched_name, lock_name)
) engine=innodb comment = '瀛樺偍鐨勬偛瑙傞攣淇℃伅琛?;

create table QRTZ_SIMPROP_TRIGGERS (
    sched_name           varchar(120)    not null            comment '璋冨害鍚嶇О',
    trigger_name         varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_name鐨勫閿?,
    trigger_group        varchar(200)    not null            comment 'qrtz_triggers琛╰rigger_group鐨勫閿?,
    str_prop_1           varchar(512)    null                comment 'String绫诲瀷鐨則rigger鐨勭涓€涓弬鏁?,
    str_prop_2           varchar(512)    null                comment 'String绫诲瀷鐨則rigger鐨勭浜屼釜鍙傛暟',
    str_prop_3           varchar(512)    null                comment 'String绫诲瀷鐨則rigger鐨勭涓変釜鍙傛暟',
    int_prop_1           int             null                comment 'int绫诲瀷鐨則rigger鐨勭涓€涓弬鏁?,
    int_prop_2           int             null                comment 'int绫诲瀷鐨則rigger鐨勭浜屼釜鍙傛暟',
    long_prop_1          bigint          null                comment 'long绫诲瀷鐨則rigger鐨勭涓€涓弬鏁?,
    long_prop_2          bigint          null                comment 'long绫诲瀷鐨則rigger鐨勭浜屼釜鍙傛暟',
    dec_prop_1           numeric(13,4)   null                comment 'decimal绫诲瀷鐨則rigger鐨勭涓€涓弬鏁?,
    dec_prop_2           numeric(13,4)   null                comment 'decimal绫诲瀷鐨則rigger鐨勭浜屼釜鍙傛暟',
    bool_prop_1          varchar(1)      null                comment 'Boolean绫诲瀷鐨則rigger鐨勭涓€涓弬鏁?,
    bool_prop_2          varchar(1)      null                comment 'Boolean绫诲瀷鐨則rigger鐨勭浜屼釜鍙傛暟',
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
) engine=innodb comment = '鍚屾鏈哄埗鐨勮閿佽〃';

-- ------------------------------------------------------------
-- Source: ruanjiansheji\HUIZHI-nongyeOS-cloud\sql\v2-zhny-4.27.sql
-- Tables: 28
-- ------------------------------------------------------------
CREATE TABLE `t_control_device_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `compay_id` int(11) NOT NULL COMMENT '鍏徃id',
  `gf_num` int(11) NOT NULL COMMENT '澶ф/楸煎缂栧彿',
  `device_num` int(11) NOT NULL COMMENT '鎺у埗鍣ㄧ紪鍙?,
  `device_switch_num` int(11) NOT NULL DEFAULT '1' COMMENT '鎺у埗鍣ㄥ紑鍏崇紪鍙?,
  `control_type` int(11) NOT NULL COMMENT '鎺у埗绫诲瀷 0瀹氭椂 1寰幆 2鏅鸿兘',
  `open_device` varchar(100) DEFAULT NULL COMMENT '鏅鸿兘鎺у埗-鎵撳紑璁惧',
  `close_device` varchar(100) DEFAULT NULL COMMENT '鏅鸿兘鎺у埗-鍏抽棴璁惧',
  `open_type` int(11) DEFAULT NULL COMMENT '鎵撳紑璁惧鏉′欢 0澶т簬 1灏忎簬',
  `close_type` int(11) DEFAULT NULL COMMENT '鍏抽棴璁惧鏉′欢 0澶т簬 1灏忎簬',
  `open_val` decimal(4,1) DEFAULT NULL COMMENT '鎵撳紑璁惧鏁板€?,
  `close_val` decimal(4,1) DEFAULT NULL COMMENT '鍏抽棴璁惧鏁板€?,
  `start_time` time NOT NULL COMMENT '寮€濮嬫椂闂?,
  `end_time` time DEFAULT NULL COMMENT '缁撴潫鏃堕棿',
  `loop_type` int(11) DEFAULT NULL COMMENT '寰幆缁撴潫绫诲瀷 0鏃堕棿 1娆℃暟',
  `loop_cnt` int(11) DEFAULT NULL COMMENT '寰幆娆℃暟',
  `duration_time` int(11) DEFAULT NULL COMMENT '鎸佺画鏃堕暱',
  `interval_time` int(11) DEFAULT NULL COMMENT '闂撮殧鏃堕暱',
  `use_state` int(11) NOT NULL DEFAULT '1' COMMENT '0绂佺敤 1寮€鍚?,
  `loop_week` varchar(100) DEFAULT NULL COMMENT '寰幆鏄熸湡',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=292 DEFAULT CHARSET=utf8 COMMENT='鎺у埗鍣ㄨ澶囪缃?;

CREATE TABLE `t_data_sum` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL DEFAULT '0' COMMENT '鍏徃id',
  `gf_num` int(11) NOT NULL DEFAULT '0' COMMENT '缁勭粐鍙?,
  `device_id` int(11) NOT NULL DEFAULT '0' COMMENT '璁惧id',
  `year` year(4) NOT NULL DEFAULT '2000' COMMENT '骞村害',
  `sum_data` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '绱鏁板€?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 COMMENT='鏁版嵁姹囨€昏〃(鐩墠缁熻骞村害绉俯涓庡厜鐓?';

CREATE TABLE `t_device` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type` int(10) NOT NULL DEFAULT '0' COMMENT '0鍐滀笟锛?娓斾笟,2澶х敯,3浠撳簱',
  `device_num` varchar(10) NOT NULL COMMENT '璁惧缂栧彿',
  `name` varchar(50) NOT NULL COMMENT '璁惧鍚嶇О',
  `device_type` int(11) NOT NULL COMMENT '0鎺у埗璁惧(鍔ㄦ€?  1閲囬泦璁惧(闈欐€?',
  `need_tatistics` int(11) NOT NULL COMMENT '鏄惁闇€瑕佺粺璁?,
  `park_device` int(11) NOT NULL COMMENT '鏄惁涓哄洯鍖鸿澶?0:鍚? 1:鏄?,
  `img` varchar(100) NOT NULL DEFAULT '' COMMENT '璁惧鍥炬爣',
  `can_modify` int(11) NOT NULL DEFAULT '0' COMMENT '鏄惁鏀寔鎵嬪姩淇敼鏁板€?瑙ｅ喅浼犳劅鍣ㄦ暟鍊间笉鍑嗛棶棰?',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=222 DEFAULT CHARSET=utf8 COMMENT='璁惧琛?;

CREATE TABLE `t_device_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `divice_flag` varchar(50) NOT NULL COMMENT '浼佷笟id&澶ф缂栧彿&璁惧缂栧彿鍞竴閿?,
  `device_id` int(11) NOT NULL COMMENT '璁惧缂栧彿(娓╁害銆佹箍搴︾瓑)',
  `company_id` int(11) NOT NULL COMMENT '鍏徃ID',
  `ground_fishpond_num` int(11) NOT NULL COMMENT '缁勭粐ID锛氬洯鍖哄唴缁勭粐鐨勫敮涓€鎬с€傦紙澶фID鎴栨笖涓欼D鎴栧啘涓欼D锛?,
  `device_switch_num` int(11) NOT NULL DEFAULT '1' COMMENT '璁惧寮€鍏充釜鏁?,
  `device_type` int(11) NOT NULL DEFAULT '0' COMMENT '璁惧绫诲瀷 0鍐滀笟 1娓斾笟 2澶х敯 3 浠撳簱',
  `device_state` int(11) NOT NULL DEFAULT '0' COMMENT '璁惧鐘舵€?0姝ｅ湪杩愯  1棰勮 2鏂嚎',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  `is_del` int(11) NOT NULL DEFAULT '0' COMMENT '鏄惁鍒犻櫎锛?鍚?1鏄?,
  `is_modify` int(11) NOT NULL DEFAULT '0' COMMENT '鏄惁鎵嬪姩淇敼锛?鍚?1鏄紙瑙ｅ喅浼犳劅鍣ㄦ暟鍊间笉鍑嗛棶棰橈級',
  `modify_val` varchar(50) NOT NULL DEFAULT '' COMMENT '淇敼鍊?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `divice_flag` (`divice_flag`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=60025 DEFAULT CHARSET=utf8 COMMENT='璁惧鍏宠仈琛?;

CREATE TABLE `t_device_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '鍏徃id',
  `gf_num` int(11) NOT NULL COMMENT '澶ф/楸煎缂栧彿',
  `device_num` int(11) NOT NULL COMMENT '璁惧缂栧彿',
  `start_time` time DEFAULT NULL COMMENT '璁惧鍚敤鏃堕棿',
  `end_time` time DEFAULT NULL COMMENT '璁惧鍏抽棴鏃堕棿',
  `left_range` decimal(4,1) DEFAULT NULL COMMENT '宸︿复鐣屽€?棰勮)',
  `right_range` decimal(4,1) DEFAULT NULL COMMENT '鍙充复鐣屽€?棰勮)',
  `use_state` int(11) NOT NULL DEFAULT '0' COMMENT '鍚敤鐘舵€?0鏈惎鍔? 1宸插惎鍔?,
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL COMMENT '淇敼鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8 COMMENT='閲囬泦璁惧璁剧疆琛紙棰勮鑼冨洿锛?;

CREATE TABLE `t_early_waring` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `ground_fishpond_num` int(11) NOT NULL COMMENT '澶ф楸煎缂栧彿',
  `device_id` int(11) NOT NULL COMMENT '璁惧缂栧彿',
  `waring_info` varchar(200) NOT NULL COMMENT '棰勮淇℃伅',
  `warning_type` int(11) NOT NULL DEFAULT '0' COMMENT '绫诲瀷 0棰勮 1鏂嚎 2....',
  `source_type` int(11) NOT NULL DEFAULT '0' COMMENT '棰勮鏉ユ簮0鍐滀笟 1娓斾笟 3澶х敯 4浠撳簱',
  `is_read` int(11) NOT NULL DEFAULT '0' COMMENT '0鏈  1宸茶',
  `create_time` datetime NOT NULL COMMENT '棰勮鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95789 DEFAULT CHARSET=utf8 COMMENT='璁惧棰勮琛?;

CREATE TABLE `t_employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `name` varchar(50) NOT NULL COMMENT '鍛樺伐濮撳悕',
  `sex` int(11) NOT NULL DEFAULT '1' COMMENT '0濂? 1鐢?,
  `icon` varchar(50) DEFAULT NULL COMMENT '澶村儚',
  `mobile` varchar(50) NOT NULL COMMENT '鎵嬫満鍙?,
  `position` varchar(50) NOT NULL COMMENT '鑱屼綅',
  `age` int(11) NOT NULL COMMENT '骞撮緞',
  `certificates` varchar(50) DEFAULT NULL COMMENT '璧勬牸璇侀檮浠?,
  `certificates_valid` date DEFAULT NULL COMMENT '璧勬牸璇佹湁鏁堟湡',
  `healthy_state` int(11) NOT NULL COMMENT '鍋ュ悍鐘舵€?,
  `address` varchar(200) DEFAULT NULL COMMENT '鎴风睄鍦?,
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COMMENT='鍛樺伐琛?;

CREATE TABLE `t_expert` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '涓撳濮撳悕',
  `type` int(11) NOT NULL COMMENT '0鍐滀笟 1娓斾笟',
  `sex` int(11) NOT NULL DEFAULT '1' COMMENT '0濂?1鐢?,
  `icon` varchar(50) DEFAULT NULL COMMENT '澶村儚',
  `industry` varchar(100) NOT NULL COMMENT '琛屼笟',
  `major` varchar(100) NOT NULL COMMENT '涓撻暱',
  `mobile` varchar(50) NOT NULL COMMENT '鎵嬫満鍙?,
  `wechat` varchar(50) DEFAULT NULL COMMENT '寰俊',
  `qq` varchar(50) DEFAULT NULL COMMENT 'QQ',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='涓撳琛?;

CREATE TABLE `t_f_data_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `fishpond_num` int(11) NOT NULL COMMENT '楸煎缂栧彿',
  `device_id` int(11) NOT NULL COMMENT '璁惧id',
  `data` decimal(10,2) DEFAULT NULL COMMENT '鏁版嵁',
  `year` int(11) NOT NULL,
  `year_date` int(11) DEFAULT NULL,
  `year_date_hour` int(11) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1773603 DEFAULT CHARSET=utf8 COMMENT='楸煎-鏁版嵁閲囬泦琛?;

CREATE TABLE `t_f_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `fishpond_num` int(11) NOT NULL COMMENT '楸煎缂栧彿',
  `product_name` varchar(200) NOT NULL COMMENT '浜у搧鍚嶇О',
  `product_icon` varchar(200) NOT NULL COMMENT '浜у搧鍥剧墖',
  `product_detail` varchar(200) NOT NULL COMMENT '绉嶅吇浜у搧',
  `product_type` varchar(200) NOT NULL COMMENT '鍝佺',
  `plant_time` date NOT NULL COMMENT '绉嶅吇鏃堕棿',
  `season` char(1) DEFAULT NULL COMMENT '瀛ｆ',
  `employee_id` int(11) NOT NULL COMMENT '璐熻矗浜?,
  `remarks` varchar(200) DEFAULT NULL COMMENT '澶囨敞',
  `product_information` varchar(2000) NOT NULL COMMENT '浜у搧绠€浠?,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0绉嶆 1褰掓。',
  `batch_count` int(11) NOT NULL DEFAULT '0' COMMENT '鎵规',
  `create_time` datetime NOT NULL COMMENT '妗ｆ鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT NULL COMMENT '妗ｆ淇敼鏃堕棿',
  `growth_pic` text COMMENT '鐢熼暱鏈熷浘鐗?,
  `fish_source` text COMMENT '楸艰嫍鏉ユ簮',
  `auth_info` text COMMENT '璁よ瘉淇℃伅',
  `grow_info` text COMMENT '鎴愰暱璁板綍',
  `feed_info` text COMMENT '楗叉枡璁板綍',
  `vaccine_info` text COMMENT '鐤嫍璁板綍',
  `drug_info` text COMMENT '鐢ㄨ嵂璁板綍',
  `web_links` text COMMENT '缃戝簵閾炬帴',
  `shop_address` text COMMENT '瀹炰綋搴楀湴鍧€',
  `env_data` text COMMENT '鐜鏁版嵁',
  `saler` varchar(200) DEFAULT NULL COMMENT '閿€鍞汉鍛?,
  `custom` text COMMENT '鑷畾涔夋ā鍧?,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='楸煎-婧簮琛?;

CREATE TABLE `t_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(3000) DEFAULT NULL COMMENT '鍐呭',
  `img` varchar(1000) DEFAULT NULL COMMENT '鍥剧墖',
  `contacts` varchar(500) DEFAULT NULL COMMENT '鑱旂郴鏂瑰紡',
  `user_id` int(11) DEFAULT NULL COMMENT '鍙嶉璐﹀彿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='鐢ㄦ埛鍙嶉琛?;

CREATE TABLE `t_g_data_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `ground_num` int(11) NOT NULL COMMENT '澶ф缂栧彿',
  `device_id` int(11) NOT NULL COMMENT '璁惧id',
  `data` decimal(11,2) DEFAULT NULL COMMENT '鏁版嵁',
  `year` int(11) NOT NULL COMMENT '骞?,
  `year_date` int(11) NOT NULL COMMENT '鏃ユ湡',
  `year_date_hour` int(11) NOT NULL COMMENT '鏃ユ湡灏忔椂(鏂逛究缁熻)',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `ground_num` (`ground_num`)
) ENGINE=InnoDB AUTO_INCREMENT=1416914 DEFAULT CHARSET=utf8 COMMENT='澶ф-鏁版嵁閲囬泦琛?;

CREATE TABLE `t_g_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `ground_num` int(11) NOT NULL COMMENT '澶ф缂栧彿',
  `product_name` varchar(200) NOT NULL COMMENT '浜у搧鍚嶇О',
  `product_icon` varchar(200) DEFAULT NULL COMMENT '浜у搧鍥剧墖',
  `product_detail` varchar(200) NOT NULL COMMENT '绉嶅吇浜у搧',
  `product_type` varchar(200) NOT NULL COMMENT '鍝佺',
  `plant_time` date NOT NULL COMMENT '绉嶅吇鏃堕棿',
  `season` char(1) NOT NULL DEFAULT '1' COMMENT '绉嶅吇瀛ｆ锛?,2,3,4',
  `employee_id` int(11) DEFAULT NULL COMMENT '璐熻矗浜?,
  `remarks` varchar(200) DEFAULT NULL COMMENT '澶囨敞',
  `product_information` varchar(2000) DEFAULT NULL COMMENT '浜у搧绠€浠?,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0绉嶆 1褰掓。  2鍒犻櫎',
  `batch_count` int(11) NOT NULL DEFAULT '0' COMMENT '鎵规',
  `create_time` datetime NOT NULL COMMENT '妗ｆ鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT NULL COMMENT '妗ｆ淇敼鏃堕棿',
  `growth_pic` text COMMENT '鐢熼暱鏈熷浘鐗?,
  `fertilization_rec` text COMMENT '鏂借偉璁板綍',
  `pesticide_rec` text COMMENT '鐢ㄨ嵂璁板綍',
  `farming_rec` text COMMENT '鍐滀簨璁板綍',
  `auth_info` text COMMENT '璁よ瘉淇℃伅',
  `growing_cycles` text COMMENT '绉嶆鍛ㄦ湡',
  `seed_source` text COMMENT '绉嶅瓙鏉ユ簮/绉嶈嫍鏉ユ簮',
  `web_links` text COMMENT '缃戝簵閾炬帴',
  `shop_address` text COMMENT '瀹炰綋搴楀湴鍧€',
  `fertilizer` text COMMENT '鍦熷￥澹ゆ儏',
  `env_data` text COMMENT '鐜鏁版嵁',
  `drug` text COMMENT '鐢ㄨ嵂鍘熷洜',
  `saler` varchar(200) DEFAULT NULL COMMENT '鍞崠浜篿d',
  `custom` text COMMENT '鑷畾涔夋ā鍧?,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='澶ф-婧簮琛?;

CREATE TABLE `t_ground_fishpond` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `number` int(11) NOT NULL COMMENT '澶ф楸煎缂栧彿',
  `type` int(11) NOT NULL COMMENT '0澶ф 1楸煎 2澶х敯 3浠撳簱',
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `display_name` varchar(100) NOT NULL DEFAULT '' COMMENT '鏄剧ず鍚嶇О',
  `camera_pass_num` varchar(50) NOT NULL DEFAULT '1' COMMENT '鎽勫儚澶撮€氶亾鍙?,
  `is_del` int(11) NOT NULL DEFAULT '0' COMMENT '鏄惁鍒犻櫎',
  `warehouse_name` varchar(200) DEFAULT NULL COMMENT '浠撳簱鍚嶇О锛堝彧瀵逛粨搴撴湁鐢級',
  `warehouse_num` varchar(50) DEFAULT NULL COMMENT '浠撳簱鏁伴噺锛堝彧瀵逛粨搴撴湁鐢級',
  `warehouse_location` varchar(200) DEFAULT NULL COMMENT '浠撳簱浣嶇疆锛堝彧瀵逛粨搴撴湁鐢級',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=224329 DEFAULT CHARSET=utf8 COMMENT='澶ф-楸煎琛?;

CREATE TABLE `t_s_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '鍏徃id',
  `user_type` int(11) NOT NULL COMMENT '绫诲瀷0鍐滀笟 1娓斾笟 2澶х敯',
  `product_id` int(11) NOT NULL COMMENT '浜у搧id',
  `listed_time` date DEFAULT NULL COMMENT '涓婂競鏃堕棿',
  `quota_desc` varchar(2000) DEFAULT NULL COMMENT '鎸囨爣鎻忚堪',
  `testing_rec` text COMMENT '妫€娴嬭褰曪紝json',
  `machining_rec` text COMMENT '鍔犲伐璁板綍锛宩son',
  `distribute_rec` text COMMENT '鍒嗛厤璁板綍锛宩son',
  `barcode` varchar(50) DEFAULT NULL COMMENT '鏉″舰鐮?,
  `barcode_url` varchar(300) DEFAULT NULL COMMENT '鏉″舰鐮佸浘鐗?,
  `qrcode_url` varchar(300) DEFAULT NULL COMMENT '浜岀淮鐮佸浘鐗?,
  `is_review` char(1) DEFAULT '0' COMMENT '鏄惁瀹℃牳,1閫氳繃锛?涓嶉€氳繃',
  `is_shelve` char(1) DEFAULT '1' COMMENT '鏄惁涓婃灦锛?涓婃灦锛?涓嬫灦',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_del` int(11) NOT NULL DEFAULT '0' COMMENT '鏄惁鍒犻櫎',
  PRIMARY KEY (`id`),
  KEY `barcode` (`barcode`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8 COMMENT='婧簮-鎵规琛?;

CREATE TABLE `t_scan_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '鍏徃id',
  `type` int(11) NOT NULL COMMENT '0鍐滀笟 1娓斾笟',
  `source_id` int(11) NOT NULL COMMENT '婧簮id',
  `area_id` varchar(200) NOT NULL COMMENT '鍦板尯',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8 COMMENT='鎵爜缁熻';

CREATE TABLE `t_t_data_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `datian_num` int(11) NOT NULL COMMENT '澶х敯缂栧彿',
  `device_id` int(11) NOT NULL COMMENT '璁惧id',
  `data` decimal(10,2) DEFAULT NULL COMMENT '鏁版嵁',
  `year` int(11) NOT NULL,
  `year_date` int(11) DEFAULT NULL,
  `year_date_hour` int(11) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='澶х敯-鏁版嵁閲囬泦琛?;

CREATE TABLE `t_t_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `datian_num` int(11) NOT NULL COMMENT '澶ф缂栧彿',
  `product_name` varchar(200) NOT NULL COMMENT '浜у搧鍚嶇О',
  `product_icon` varchar(200) DEFAULT NULL COMMENT '浜у搧鍥剧墖',
  `product_detail` varchar(200) NOT NULL COMMENT '绉嶅吇浜у搧',
  `product_type` varchar(200) NOT NULL COMMENT '鍝佺',
  `plant_time` date NOT NULL COMMENT '绉嶅吇鏃堕棿',
  `season` char(1) NOT NULL DEFAULT '1' COMMENT '绉嶅吇瀛ｆ锛?,2,3,4',
  `employee_id` int(11) DEFAULT NULL COMMENT '璐熻矗浜?,
  `remarks` varchar(200) DEFAULT NULL COMMENT '澶囨敞',
  `product_information` varchar(2000) DEFAULT NULL COMMENT '浜у搧绠€浠?,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0绉嶆 1褰掓。  2鍒犻櫎',
  `batch_count` int(11) NOT NULL DEFAULT '0' COMMENT '鎵规',
  `create_time` datetime NOT NULL COMMENT '妗ｆ鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT NULL COMMENT '妗ｆ淇敼鏃堕棿',
  `growth_pic` text COMMENT '鐢熼暱鏈熷浘鐗?,
  `fertilization_rec` text COMMENT '鏂借偉璁板綍',
  `pesticide_rec` text COMMENT '鐢ㄨ嵂璁板綍',
  `web_links` varchar(500) DEFAULT NULL COMMENT '缃戝簵閾炬帴',
  `shop_address` varchar(500) DEFAULT NULL COMMENT '瀹炰綋搴楀湴鍧€',
  `env_data` text COMMENT '鐜鏁版嵁',
  `saler` varchar(200) DEFAULT NULL COMMENT '鍞崠浜篿d',
  `custom` text COMMENT '鑷畾涔夋ā鍧?,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='澶х敯-婧簮琛?;

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(50) NOT NULL COMMENT '鎵嬫満鍙?,
  `name` varchar(50) NOT NULL COMMENT '浼佷笟鍚嶇О',
  `type` int(11) NOT NULL COMMENT '1:瓒呯骇绠＄悊鍛?2绠＄悊鍛?3:鍐滀笟 4:娓斾笟',
  `max_cnt` int(11) NOT NULL COMMENT '澶ф/楸煎鏁?,
  `icon` varchar(200) NOT NULL DEFAULT 'icon_default.jpg' COMMENT '浼佷笟鐓х墖',
  `describe` varchar(2000) DEFAULT NULL COMMENT '浼佷笟绠€浠?,
  `lng` varchar(100) DEFAULT NULL COMMENT '缁忓害浣嶇疆',
  `lat` varchar(100) DEFAULT NULL COMMENT '绾害浣嶇疆',
  `contacts` varchar(50) DEFAULT NULL COMMENT '鑱旂郴鏂瑰紡',
  `address` varchar(500) DEFAULT NULL COMMENT '鍦板潃',
  `camera_url` varchar(200) DEFAULT NULL COMMENT '鎽勫儚鏈篒P鍦板潃',
  `camera_port` varchar(50) DEFAULT NULL COMMENT '鎽勫儚鏈虹鍙?,
  `phone_port` varchar(50) DEFAULT NULL COMMENT '鎵嬫満绔鍙?,
  `camera_uname` varchar(100) DEFAULT NULL COMMENT '鎽勫儚鏈虹敤鎴峰悕',
  `camera_pwd` varchar(100) DEFAULT NULL COMMENT '鎽勫儚鏈哄瘑鐮?,
  `state` int(11) NOT NULL DEFAULT '1' COMMENT '0绂佺敤 1鍙敤',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL COMMENT '淇敼鏃堕棿',
  `contacts_user` varchar(200) DEFAULT NULL COMMENT '鑱旂郴浜?,
  `push_state` int(11) NOT NULL DEFAULT '1' COMMENT '鏄惁寮€鍚帹閫?,
  `default_pwd` varchar(50) NOT NULL COMMENT '榛樿瀵嗙爜',
  `exception_mobile` varchar(50) DEFAULT NULL COMMENT '寮傚父鎶ヨ鐢佃瘽',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=900002 DEFAULT CHARSET=utf8 COMMENT='鐢ㄦ埛璐﹀彿琛?;

CREATE TABLE `w_inventory` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '浜у搧ID',
  `name` varchar(200) NOT NULL COMMENT '浜у搧鍚嶇О',
  `type` varchar(200) NOT NULL COMMENT '浜у搧绉嶇被',
  `size` varchar(200) NOT NULL COMMENT '鍏ュ簱|鍑哄簱 閲?,
  `stock` varchar(200) DEFAULT NULL COMMENT '搴撳瓨锛堝綋鍏ュ簱鏃讹紝璇ュ瓧娈垫爣璇嗘湁澶氬皯鍓╀綑搴撳瓨锛?,
  `qualified` varchar(20) DEFAULT NULL COMMENT '鍚堟牸鐜?,
  `operator` varchar(200) NOT NULL COMMENT '鎿嶄綔浜哄憳',
  `io_type` varchar(2) DEFAULT NULL COMMENT '绫诲瀷 1 鍏ュ簱 2 鍑哄簱',
  `inventory_id` varchar(50) DEFAULT NULL COMMENT '鍏ュ簱ID(褰撶被鍨嬫槸鍑哄簱绫诲瀷鏃舵墠鏈夊叆搴撶被鍨婭D)',
  `warehouse_id` int(11) NOT NULL COMMENT '浠撳簱id',
  `company_id` varchar(200) DEFAULT NULL COMMENT '鍏徃ID',
  `io_time` varchar(200) DEFAULT NULL COMMENT '鍑哄叆搴撴椂闂?,
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='鍏ュ簱琛?搴撳瓨';

CREATE TABLE `w_inventory_data_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL COMMENT '浼佷笟id',
  `ground_num` int(11) NOT NULL COMMENT '浠撳簱缂栧彿',
  `device_id` int(11) NOT NULL COMMENT '璁惧id',
  `data` decimal(11,2) DEFAULT NULL COMMENT '鏁版嵁',
  `year` int(11) NOT NULL COMMENT '骞?,
  `year_date` int(11) NOT NULL COMMENT '鏃ユ湡',
  `year_date_hour` int(11) NOT NULL COMMENT '鏃ユ湡灏忔椂(鏂逛究缁熻)',
  `create_time` datetime NOT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `ground_num` (`ground_num`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='浠撳簱-鏁版嵁閲囬泦琛?;

CREATE TABLE `w_inventory_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `warehouse_id` int(11) NOT NULL COMMENT '浠撳簱id',
  `name` varchar(200) NOT NULL COMMENT '浜у搧鍚嶇О',
  `type` varchar(200) NOT NULL COMMENT '浜у搧绉嶇被',
  `operator` varchar(200) NOT NULL COMMENT '鎿嶄綔浜哄憳',
  `create_time` datetime NOT NULL COMMENT '鍏ュ簱鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='鍏ュ簱琛?;

CREATE TABLE `xt_func` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '鏂规硶鍚?,
  `url` varchar(200) NOT NULL COMMENT '鏂规硶url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8 COMMENT='鏂规硶琛?;

CREATE TABLE `xt_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '鑿滃崟鍚嶇О',
  `parent` varchar(10) NOT NULL COMMENT '鐖剁骇鑿滃崟',
  `menu_flag` varchar(10) NOT NULL COMMENT '鑿滃崟鏍囪瘑',
  `img` varchar(20) DEFAULT NULL COMMENT '鑿滃崟鍥剧墖',
  `menu_order` int(11) DEFAULT NULL COMMENT '鑿滃崟椤哄簭',
  `url` varchar(200) NOT NULL COMMENT '鑿滃崟url',
  `visible` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `menu_flag` (`menu_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COMMENT='鑿滃崟琛?;

CREATE TABLE `xt_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '鏉冮檺鍚嶇О',
  `level` int(11) NOT NULL COMMENT '鏉冮檺绛夌骇',
  `user_type` int(11) NOT NULL DEFAULT '0' COMMENT '鐢ㄦ埛绫诲瀷',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='鏉冮檺琛?;

CREATE TABLE `xt_role_func` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `func_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1852 DEFAULT CHARSET=utf8 COMMENT='鏂规硶鏉冮檺琛?;

CREATE TABLE `xt_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=438 DEFAULT CHARSET=utf8 COMMENT='鑿滃崟鏉冮檺琛?;

CREATE TABLE `xt_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '鐢ㄦ埛Id',
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '鏉冮檺id',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='鐢ㄦ埛鏉冮檺鍏宠仈琛?;

-- ------------------------------------------------------------
-- Source: ruanjiansheji\smart-farm-platform\src\main\resources\schema.sql
-- Tables: 12
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
    `username` VARCHAR(50) NOT NULL COMMENT '鐢ㄦ埛鍚?,
    `password` VARCHAR(255) NOT NULL COMMENT '瀵嗙爜锛圔Crypt鍔犲瘑锛?,
    `role` VARCHAR(20) NOT NULL DEFAULT 'VIEWER' COMMENT '瑙掕壊锛欰DMIN/TECHNICIAN/OPERATOR/VIEWER',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛?;

CREATE TABLE `device` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '璁惧ID',
    `device_code` VARCHAR(50) NOT NULL COMMENT '璁惧缂栧彿',
    `device_name` VARCHAR(100) NOT NULL COMMENT '璁惧鍚嶇О',
    `device_type` VARCHAR(50) NOT NULL COMMENT '璁惧绫诲瀷',
    `state` VARCHAR(20) NOT NULL DEFAULT 'STANDBY' COMMENT '璁惧鐘舵€侊細STANDBY/RUNNING/FAULT/MAINTENANCE/CALIBRATION',
    `area` VARCHAR(100) DEFAULT NULL COMMENT '瀹夎鍖哄煙',
    `online` TINYINT(1) DEFAULT 1 COMMENT '鍦ㄧ嚎鐘舵€侊細0-绂荤嚎 1-鍦ㄧ嚎',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_code` (`device_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璁惧琛?;

CREATE TABLE `environment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `record_time` DATETIME NOT NULL COMMENT '璁板綍鏃堕棿',
    `air_temperature` DECIMAL(5,2) DEFAULT NULL COMMENT '绌烘皵娓╁害(掳C)',
    `air_humidity` DECIMAL(5,2) DEFAULT NULL COMMENT '绌烘皵婀垮害(%)',
    `soil_temperature` DECIMAL(5,2) DEFAULT NULL COMMENT '鍦熷￥娓╁害(掳C)',
    `soil_humidity` DECIMAL(5,2) DEFAULT NULL COMMENT '鍦熷￥婀垮害(%)',
    `light_intensity` DECIMAL(10,2) DEFAULT NULL COMMENT '鍏夌収寮哄害(lux)',
    `co2_level` DECIMAL(10,2) DEFAULT NULL COMMENT 'CO2娴撳害(ppm)',
    `ph_value` DECIMAL(4,2) DEFAULT NULL COMMENT 'pH鍊?,
    `ec_value` DECIMAL(6,2) DEFAULT NULL COMMENT 'EC鍊?mS/cm)',
    `wind_speed` DECIMAL(5,2) DEFAULT NULL COMMENT '椋庨€?m/s)',
    `rainfall` DECIMAL(5,2) DEFAULT NULL COMMENT '闄嶉洦閲?mm)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐜璁板綍琛?;

CREATE TABLE `device_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    `device_code` VARCHAR(50) NOT NULL COMMENT '璁惧缂栧彿',
    `device_name` VARCHAR(100) DEFAULT NULL COMMENT '璁惧鍚嶇О',
    `operation` VARCHAR(50) NOT NULL COMMENT '鎿嶄綔绫诲瀷',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '鎿嶄綔浜?,
    `result` VARCHAR(20) DEFAULT NULL COMMENT '鎿嶄綔缁撴灉锛歋UCCESS/FAIL',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '澶囨敞',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_device_code` (`device_code`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璁惧鎿嶄綔鏃ュ織琛?;

CREATE TABLE `alert_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '棰勮ID',
    `alert_type` VARCHAR(50) NOT NULL COMMENT '棰勮绫诲瀷',
    `alert_level` VARCHAR(20) NOT NULL COMMENT '棰勮绾у埆锛歀OW/MEDIUM/HIGH/CRITICAL',
    `title` VARCHAR(200) NOT NULL COMMENT '棰勮鏍囬',
    `content` TEXT DEFAULT NULL COMMENT '棰勮鍐呭',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '鐘舵€侊細ACTIVE/RESOLVED/IGNORED',
    `resolved_at` DATETIME DEFAULT NULL COMMENT '瑙ｅ喅鏃堕棿',
    `resolved_by` VARCHAR(50) DEFAULT NULL COMMENT '瑙ｅ喅浜?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_alert_type` (`alert_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='棰勮璁板綍琛?;

CREATE TABLE `farm_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '浠诲姟ID',
    `task_name` VARCHAR(200) NOT NULL COMMENT '浠诲姟鍚嶇О',
    `task_type` VARCHAR(50) NOT NULL COMMENT '浠诲姟绫诲瀷',
    `description` TEXT DEFAULT NULL COMMENT '浠诲姟鎻忚堪',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '鐘舵€侊細PENDING/IN_PROGRESS/COMPLETED/CANCELLED',
    `priority` VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '浼樺厛绾э細LOW/MEDIUM/HIGH',
    `assignee` VARCHAR(50) DEFAULT NULL COMMENT '璐熻矗浜?,
    `due_date` DATETIME DEFAULT NULL COMMENT '鎴鏃ユ湡',
    `completed_at` DATETIME DEFAULT NULL COMMENT '瀹屾垚鏃堕棿',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_assignee` (`assignee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍐滀簨浠诲姟琛?;

CREATE TABLE `yield_prediction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '棰勬祴ID',
    `crop_type` VARCHAR(50) NOT NULL COMMENT '浣滅墿绫诲瀷',
    `prediction_date` DATE NOT NULL COMMENT '棰勬祴鏃ユ湡',
    `predicted_yield` DECIMAL(10,2) DEFAULT NULL COMMENT '棰勬祴浜ч噺(kg)',
    `actual_yield` DECIMAL(10,2) DEFAULT NULL COMMENT '瀹為檯浜ч噺(kg)',
    `confidence` DECIMAL(5,2) DEFAULT NULL COMMENT '缃俊搴?%)',
    `factors` TEXT DEFAULT NULL COMMENT '褰卞搷鍥犵礌',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_crop_type` (`crop_type`),
    KEY `idx_prediction_date` (`prediction_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜ч噺棰勬祴琛?;

CREATE TABLE `crop_recommendation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `n` DECIMAL(10,2) DEFAULT NULL COMMENT '姘惈閲?,
    `p` DECIMAL(10,2) DEFAULT NULL COMMENT '纾峰惈閲?,
    `k` DECIMAL(10,2) DEFAULT NULL COMMENT '閽惧惈閲?,
    `temperature` DECIMAL(5,2) DEFAULT NULL COMMENT '娓╁害',
    `humidity` DECIMAL(5,2) DEFAULT NULL COMMENT '婀垮害',
    `ph` DECIMAL(4,2) DEFAULT NULL COMMENT 'pH鍊?,
    `rainfall` DECIMAL(10,2) DEFAULT NULL COMMENT '闄嶉洦閲?,
    `label` VARCHAR(50) DEFAULT NULL COMMENT '鎺ㄨ崘浣滅墿',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浣滅墿鎺ㄨ崘鏁版嵁琛?;

CREATE TABLE `fertilizer_advice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `crop_type` VARCHAR(50) DEFAULT NULL COMMENT '浣滅墿绫诲瀷',
    `soil_type` VARCHAR(50) DEFAULT NULL COMMENT '鍦熷￥绫诲瀷',
    `nitrogen` DECIMAL(10,2) DEFAULT NULL COMMENT '姘渶姹傞噺',
    `phosphorus` DECIMAL(10,2) DEFAULT NULL COMMENT '纾烽渶姹傞噺',
    `potassium` DECIMAL(10,2) DEFAULT NULL COMMENT '閽鹃渶姹傞噺',
    `fertilizer_type` VARCHAR(100) DEFAULT NULL COMMENT '鎺ㄨ崘鑲ユ枡绫诲瀷',
    `dosage` DECIMAL(10,2) DEFAULT NULL COMMENT '鐢ㄩ噺(kg/浜?',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏂借偉寤鸿琛?;

CREATE TABLE `pest_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `pest_name` VARCHAR(100) NOT NULL COMMENT '瀹宠櫕鍚嶇О',
    `pest_category` VARCHAR(50) DEFAULT NULL COMMENT '瀹宠櫕鍒嗙被',
    `description` TEXT DEFAULT NULL COMMENT '鎻忚堪',
    `harmful_part` VARCHAR(100) DEFAULT NULL COMMENT '鍗卞閮ㄤ綅',
    `occurrence_season` VARCHAR(100) DEFAULT NULL COMMENT '鍙戠敓瀛ｈ妭',
    `prevention_method` TEXT DEFAULT NULL COMMENT '闃叉不鏂规硶',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='瀹宠櫕绫诲瀷琛?;

CREATE TABLE IF NOT EXISTS weather_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    farm_id BIGINT,
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    record_time DATETIME,
    temperature DOUBLE,
    humidity DOUBLE,
    precipitation DOUBLE,
    wind_speed DOUBLE,
    soil_temperature DOUBLE,
    soil_moisture DOUBLE,
    source VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `city_location` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `province` VARCHAR(50) NOT NULL COMMENT '鐪佷唤',
    `city_name` VARCHAR(50) NOT NULL COMMENT '鍩庡競鍚嶇О',
    `latitude` DOUBLE NOT NULL COMMENT '绾害',
    `longitude` DOUBLE NOT NULL COMMENT '缁忓害',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_name` (`city_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍩庡競缁忕含搴﹁〃';

-- ------------------------------------------------------------
-- Source: texunsuyuan\tedingshixun\sql\init_databases.sql
-- Tables: 19
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '鐢ㄦ埛鍚?,
    `password` VARCHAR(255) NOT NULL COMMENT '瀵嗙爜(鍔犲瘑)',
    `real_name` VARCHAR(50) COMMENT '鐪熷疄濮撳悕',
    `phone` VARCHAR(20) UNIQUE COMMENT '鎵嬫満鍙?,
    `email` VARCHAR(100) UNIQUE COMMENT '閭',
    `role` VARCHAR(20) NOT NULL COMMENT '瑙掕壊: ADMIN/FARMER/TRADER/RETAILER/CONSUMER',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '鐘舵€? ACTIVE/INACTIVE',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑: 0-鏈垹闄? 1-宸插垹闄?,
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛?;

CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '瑙掕壊ID',
    `role_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '瑙掕壊缂栫爜',
    `role_name` VARCHAR(50) NOT NULL COMMENT '瑙掕壊鍚嶇О',
    `description` VARCHAR(200) COMMENT '瑙掕壊鎻忚堪',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='瑙掕壊琛?;

CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏉冮檺ID',
    `perm_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '鏉冮檺缂栫爜',
    `perm_name` VARCHAR(100) NOT NULL COMMENT '鏉冮檺鍚嶇О',
    `menu_path` VARCHAR(200) COMMENT '鑿滃崟璺緞',
    `parent_id` BIGINT DEFAULT 0 COMMENT '鐖舵潈闄怚D',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏉冮檺琛?;

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` BIGINT NOT NULL COMMENT '瑙掕壊ID',
    `perm_id` BIGINT NOT NULL COMMENT '鏉冮檺ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='瑙掕壊鏉冮檺鍏宠仈琛?;

CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '浜у搧ID',
    `product_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '浜у搧缂栫爜',
    `product_name` VARCHAR(100) NOT NULL COMMENT '浜у搧鍚嶇О',
    `category` VARCHAR(50) COMMENT '浜у搧绫诲埆',
    `specification` VARCHAR(200) COMMENT '瑙勬牸',
    `unit` VARCHAR(20) COMMENT '鍗曚綅',
    `origin` VARCHAR(100) COMMENT '浜у湴',
    `producer_id` BIGINT COMMENT '鐢熶骇鑰匢D',
    `producer_name` VARCHAR(100) COMMENT '鐢熶骇鑰呭悕绉?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜у搧琛?;

CREATE TABLE IF NOT EXISTS `product_batch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鎵规ID',
    `batch_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '鎵规鍙?,
    `product_code` VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    `product_name` VARCHAR(100) COMMENT '浜у搧鍚嶇О',
    `quantity` DECIMAL(18,4) NOT NULL COMMENT '鏁伴噺',
    `unit` VARCHAR(20) COMMENT '鍗曚綅',
    `production_date` DATE COMMENT '鐢熶骇鏃ユ湡',
    `expiry_date` DATE COMMENT '淇濊川鏈熻嚦',
    `status` VARCHAR(20) DEFAULT 'VALID' COMMENT '鐘舵€? VALID/INVALID',
    `producer_id` BIGINT COMMENT '鐢熶骇鑰匢D',
    `producer_name` VARCHAR(100) COMMENT '鐢熶骇鑰呭悕绉?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜у搧鎵规琛?;

CREATE TABLE IF NOT EXISTS `trace_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '婧簮璁板綍ID',
    `trace_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '婧簮ID',
    `product_code` VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规鍙?,
    `operation_type` VARCHAR(50) NOT NULL COMMENT '鎿嶄綔绫诲瀷: 绉嶆/鏀惰幏/杩愯緭/浠撳偍/鍔犲伐/閿€鍞?,
    `operator_id` BIGINT COMMENT '鎿嶄綔鑰匢D',
    `operator_name` VARCHAR(100) COMMENT '鎿嶄綔鑰呭悕绉?,
    `operator_role` VARCHAR(20) COMMENT '鎿嶄綔鑰呰鑹?,
    `location` VARCHAR(200) COMMENT '鎿嶄綔鍦扮偣',
    `latitude` DECIMAL(10,6) COMMENT '绾害',
    `longitude` DECIMAL(10,6) COMMENT '缁忓害',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `chain_hash` VARCHAR(64) COMMENT '閾句笂鍝堝笇',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_trace_id` (`trace_id`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='婧簮璁板綍琛?;

CREATE TABLE IF NOT EXISTS `qrcode_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `qr_code` VARCHAR(255) NOT NULL UNIQUE COMMENT '浜岀淮鐮佸唴瀹?,
    `product_code` VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规鍙?,
    `trace_id` VARCHAR(64) COMMENT '婧簮ID',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '鐘舵€? ACTIVE/INACTIVE',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_qr_code` (`qr_code`),
    INDEX `idx_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜岀淮鐮佷俊鎭〃';

CREATE TABLE IF NOT EXISTS `planting_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `sow_time` DATETIME COMMENT '鎾鏃堕棿',
    `fertilizer_record` VARCHAR(500) COMMENT '鏂借偉璁板綍',
    `pesticide_record` VARCHAR(500) COMMENT '鍐滆嵂浣跨敤璁板綍',
    `irrigation_record` VARCHAR(500) COMMENT '鐏屾簤璁板綍',
    `soil_temperature` VARCHAR(50) COMMENT '鍦熷￥娓╁害',
    `soil_humidity` VARCHAR(50) COMMENT '鍦熷￥婀垮害',
    `harvest_time` DATETIME COMMENT '閲囨敹鏃堕棿',
    `responsible_person` VARCHAR(100) COMMENT '绉嶆璐熻矗浜?,
    `image_url` VARCHAR(500) COMMENT '鍥剧墖鍦板潃',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绉嶆璁板綍琛?;

CREATE TABLE IF NOT EXISTS `processing_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `processing_enterprise` VARCHAR(200) COMMENT '鍔犲伐浼佷笟',
    `processing_time` DATETIME COMMENT '鍔犲伐鏃堕棿',
    `processing_method` VARCHAR(200) COMMENT '鍔犲伐鏂瑰紡',
    `quality_result` VARCHAR(50) COMMENT '璐ㄦ缁撴灉',
    `inspector` VARCHAR(100) COMMENT '妫€娴嬩汉鍛?,
    `report_url` VARCHAR(500) COMMENT '妫€娴嬫姤鍛婂湴鍧€',
    `processing_temperature` VARCHAR(50) COMMENT '鍔犲伐娓╁害',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍔犲伐璁板綍琛?;

CREATE TABLE IF NOT EXISTS `logistics_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `logistics_company` VARCHAR(200) COMMENT '鐗╂祦鍏徃',
    `transport_vehicle` VARCHAR(100) COMMENT '杩愯緭杞﹁締',
    `driver_name` VARCHAR(50) COMMENT '鍙告満濮撳悕',
    `driver_phone` VARCHAR(20) COMMENT '鍙告満鐢佃瘽',
    `from_address` VARCHAR(500) COMMENT '鍙戣揣鍦板潃',
    `to_address` VARCHAR(500) COMMENT '鏀惰揣鍦板潃',
    `ship_time` DATETIME COMMENT '鍙戣揣鏃堕棿',
    `arrival_time` DATETIME COMMENT '鍒拌揣鏃堕棿',
    `transport_temperature` VARCHAR(50) COMMENT '杩愯緭娓╁害',
    `transport_humidity` VARCHAR(50) COMMENT '杩愯緭婀垮害',
    `logistics_status` VARCHAR(50) COMMENT '鐗╂祦鐘舵€?,
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐗╂祦璁板綍琛?;

CREATE TABLE IF NOT EXISTS `sales_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `seller` VARCHAR(200) COMMENT '閿€鍞晢',
    `sales_region` VARCHAR(200) COMMENT '閿€鍞湴鍖?,
    `listing_time` DATETIME COMMENT '涓婃灦鏃堕棿',
    `sales_price` DECIMAL(18,2) COMMENT '閿€鍞环鏍?,
    `stock_quantity` INT COMMENT '搴撳瓨鏁伴噺',
    `sales_status` VARCHAR(50) COMMENT '閿€鍞姸鎬?,
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='閿€鍞褰曡〃';

CREATE TABLE IF NOT EXISTS `quality_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `processing_record_id` BIGINT COMMENT '鍏宠仈鍔犲伐璁板綍ID',
    `quality_date` DATETIME COMMENT '璐ㄦ鏃ユ湡',
    `quality_result` VARCHAR(50) COMMENT '璐ㄦ缁撴灉',
    `inspector` VARCHAR(100) COMMENT '妫€娴嬩汉鍛?,
    `report_url` VARCHAR(500) COMMENT '鎶ュ憡鏂囦欢鍦板潃',
    `inspection_items` VARCHAR(500) COMMENT '妫€娴嬮」鐩?,
    `inspection_standard` VARCHAR(200) COMMENT '妫€娴嬫爣鍑?,
    `qualified` TINYINT(1) DEFAULT 1 COMMENT '鏄惁鍚堟牸: 0-涓嶅悎鏍? 1-鍚堟牸',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_processing_record_id` (`processing_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璐ㄦ鎶ュ憡琛?;

CREATE TABLE IF NOT EXISTS `stock_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `quantity` INT COMMENT '搴撳瓨鏁伴噺',
    `change_type` VARCHAR(20) COMMENT '搴撳瓨鍙樺姩绫诲瀷: IN-鍏ュ簱, OUT-鍑哄簱',
    `change_quantity` INT COMMENT '鍙樺姩鏁伴噺',
    `after_quantity` INT COMMENT '鍙樺姩鍚庡簱瀛?,
    `change_time` DATETIME COMMENT '鍙樺姩鏃堕棿',
    `operator_name` VARCHAR(100) COMMENT '鎿嶄綔浜?,
    `related_record_id` BIGINT COMMENT '鍏宠仈涓氬姟ID',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搴撳瓨璁板綍琛?;

CREATE TABLE IF NOT EXISTS `chain_header` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `chain_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '閾綢D',
    `chain_name` VARCHAR(100) NOT NULL COMMENT '閾惧悕绉?,
    `description` VARCHAR(500) COMMENT '鎻忚堪',
    `current_height` BIGINT DEFAULT 0 COMMENT '褰撳墠楂樺害',
    `latest_hash` VARCHAR(64) COMMENT '鏈€鏂板尯鍧楀搱甯?,
    `status` VARCHAR(20) DEFAULT 'VALID' COMMENT '鐘舵€? VALID/INVALID',
    `creator_id` VARCHAR(64) COMMENT '鍒涘缓浜篒D',
    `creator_name` VARCHAR(100) COMMENT '鍒涘缓浜哄鍚?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_chain_name` (`chain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='閾惧ご淇℃伅琛?;

CREATE TABLE IF NOT EXISTS `chain_block` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `chain_id` VARCHAR(64) NOT NULL COMMENT '閾綢D',
    `block_hash` VARCHAR(64) NOT NULL UNIQUE COMMENT '鍖哄潡鍝堝笇',
    `previous_hash` VARCHAR(64) NOT NULL COMMENT '鍓嶄竴鍖哄潡鍝堝笇',
    `data_hash` VARCHAR(64) NOT NULL COMMENT '鏁版嵁鍝堝笇',
    `data_content` TEXT COMMENT '鏁版嵁鍐呭(JSON)',
    `nonce` BIGINT DEFAULT 0 COMMENT '闅忔満鏁?,
    `timestamp` BIGINT COMMENT '鏃堕棿鎴?,
    `operator_id` VARCHAR(64) COMMENT '鎿嶄綔鑰匢D',
    `operator_name` VARCHAR(100) COMMENT '鎿嶄綔鑰呭鍚?,
    `operation_type` VARCHAR(50) COMMENT '鎿嶄綔绫诲瀷',
    `product_code` VARCHAR(50) COMMENT '浜у搧缂栫爜',
    `batch_no` VARCHAR(50) COMMENT '鎵规鍙?,
    `block_height` INT DEFAULT 0 COMMENT '鍖哄潡楂樺害',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_block_hash` (`block_hash`),
    INDEX `idx_product_code` (`product_code`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_block_height` (`block_height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍖哄潡鏁版嵁琛?;

CREATE TABLE IF NOT EXISTS `chain_verification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `chain_id` VARCHAR(64) NOT NULL COMMENT '閾綢D',
    `block_hash` VARCHAR(64) NOT NULL COMMENT '鍖哄潡鍝堝笇',
    `data_hash` VARCHAR(64) COMMENT '鏁版嵁鍝堝笇',
    `original_data` TEXT COMMENT '鍘熷鏁版嵁',
    `verified` TINYINT(1) DEFAULT 0 COMMENT '楠岃瘉缁撴灉: 0-鏈€氳繃, 1-閫氳繃',
    `verification_result` VARCHAR(500) COMMENT '楠岃瘉缁撴灉鎻忚堪',
    `operator_id` VARCHAR(64) COMMENT '鎿嶄綔鑰匢D',
    `operator_name` VARCHAR(100) COMMENT '鎿嶄綔鑰呭鍚?,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_chain_id` (`chain_id`),
    INDEX `idx_block_hash` (`block_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楠岃瘉璁板綍琛?;

CREATE TABLE IF NOT EXISTS `admin_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    `log_type` VARCHAR(50) COMMENT '鏃ュ織绫诲瀷: LOGIN/OPERATION/EXCEPTION',
    `user_id` BIGINT COMMENT '鐢ㄦ埛ID',
    `username` VARCHAR(50) COMMENT '鐢ㄦ埛鍚?,
    `operation` VARCHAR(200) COMMENT '鎿嶄綔鎻忚堪',
    `module` VARCHAR(50) COMMENT '鎿嶄綔妯″潡',
    `method` VARCHAR(10) COMMENT '璇锋眰鏂规硶: GET/POST/PUT/DELETE',
    `request_url` VARCHAR(500) COMMENT '璇锋眰URL',
    `request_param` TEXT COMMENT '璇锋眰鍙傛暟',
    `response_result` TEXT COMMENT '鍝嶅簲缁撴灉',
    `ip_address` VARCHAR(50) COMMENT 'IP鍦板潃',
    `user_agent` VARCHAR(500) COMMENT '鐢ㄦ埛浠ｇ悊',
    `execution_time` BIGINT COMMENT '鎵ц鏃堕棿(ms)',
    `success` TINYINT(1) DEFAULT 1 COMMENT '鏄惁鎴愬姛: 0-澶辫触, 1-鎴愬姛',
    `error_message` TEXT COMMENT '閿欒淇℃伅',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_log_type` (`log_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鎿嶄綔鏃ュ織琛?;

CREATE TABLE IF NOT EXISTS `statistics_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `stat_type` VARCHAR(50) NOT NULL COMMENT '缁熻绫诲瀷',
    `stat_date` DATE NOT NULL COMMENT '缁熻鏃ユ湡',
    `product_count` BIGINT DEFAULT 0 COMMENT '浜у搧鏁伴噺',
    `batch_count` BIGINT DEFAULT 0 COMMENT '鎵规鏁伴噺',
    `trace_count` BIGINT DEFAULT 0 COMMENT '婧簮璁板綍鏁伴噺',
    `block_count` BIGINT DEFAULT 0 COMMENT '鍖哄潡鏁伴噺',
    `user_count` BIGINT DEFAULT 0 COMMENT '鐢ㄦ埛鏁伴噺',
    `data_json` JSON COMMENT '缁熻鏁版嵁(JSON)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stat_date_type` (`stat_type`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缁熻鏁版嵁琛?;

-- ------------------------------------------------------------
-- Source: texunsuyuan\tedingshixun-dev\sql\init_databases.sql
-- Tables: 19
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '鐢ㄦ埛鍚?,
    password VARCHAR(255) NOT NULL COMMENT '瀵嗙爜(鍔犲瘑)',
    real_name VARCHAR(50) COMMENT '鐪熷疄濮撳悕',
    phone VARCHAR(20) UNIQUE COMMENT '鎵嬫満鍙?,
    email VARCHAR(100) UNIQUE COMMENT '閭',
    role VARCHAR(20) NOT NULL COMMENT '瑙掕壊: ADMIN/FARMER/TRADER/RETAILER/CONSUMER',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '鐘舵€? ACTIVE/INACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑: 0-鏈垹闄? 1-宸插垹闄?,
    PRIMARY KEY (id),
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛?;

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '瑙掕壊ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '瑙掕壊缂栫爜',
    role_name VARCHAR(50) NOT NULL COMMENT '瑙掕壊鍚嶇О',
    description VARCHAR(200) COMMENT '瑙掕壊鎻忚堪',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='瑙掕壊琛?;

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏉冮檺ID',
    perm_code VARCHAR(100) NOT NULL UNIQUE COMMENT '鏉冮檺缂栫爜',
    perm_name VARCHAR(100) NOT NULL COMMENT '鏉冮檺鍚嶇О',
    menu_path VARCHAR(200) COMMENT '鑿滃崟璺緞',
    parent_id BIGINT DEFAULT 0 COMMENT '鐖舵潈闄怚D',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏉冮檺琛?;

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '瑙掕壊ID',
    perm_id BIGINT NOT NULL COMMENT '鏉冮檺ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='瑙掕壊鏉冮檺鍏宠仈琛?;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '浜у搧ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '浜у搧缂栫爜',
    product_name VARCHAR(100) NOT NULL COMMENT '浜у搧鍚嶇О',
    category VARCHAR(50) COMMENT '浜у搧绫诲埆',
    specification VARCHAR(200) COMMENT '瑙勬牸',
    unit VARCHAR(20) COMMENT '鍗曚綅',
    origin VARCHAR(100) COMMENT '浜у湴',
    producer_id BIGINT COMMENT '鐢熶骇鑰匢D',
    producer_name VARCHAR(100) COMMENT '鐢熶骇鑰呭悕绉?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_product_code (product_code),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜у搧琛?;

CREATE TABLE IF NOT EXISTS product_batch (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '鎵规ID',
    batch_no VARCHAR(50) NOT NULL UNIQUE COMMENT '鎵规鍙?,
    product_code VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    product_name VARCHAR(100) COMMENT '浜у搧鍚嶇О',
    quantity DECIMAL(18,4) NOT NULL COMMENT '鏁伴噺',
    unit VARCHAR(20) COMMENT '鍗曚綅',
    production_date DATE COMMENT '鐢熶骇鏃ユ湡',
    expiry_date DATE COMMENT '淇濊川鏈熻嚦',
    status VARCHAR(20) DEFAULT 'VALID' COMMENT '鐘舵€? VALID/INVALID',
    producer_id BIGINT COMMENT '鐢熶骇鑰匢D',
    producer_name VARCHAR(100) COMMENT '鐢熶骇鑰呭悕绉?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜у搧鎵规琛?;

CREATE TABLE IF NOT EXISTS trace_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '婧簮璁板綍ID',
    trace_id VARCHAR(64) NOT NULL UNIQUE COMMENT '婧簮ID',
    product_code VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    batch_no VARCHAR(50) NOT NULL COMMENT '鎵规鍙?,
    operation_type VARCHAR(50) NOT NULL COMMENT '鎿嶄綔绫诲瀷',
    operator_id BIGINT COMMENT '鎿嶄綔鑰匢D',
    operator_name VARCHAR(100) COMMENT '鎿嶄綔鑰呭悕绉?,
    operator_role VARCHAR(20) COMMENT '鎿嶄綔鑰呰鑹?,
    location VARCHAR(200) COMMENT '鎿嶄綔鍦扮偣',
    latitude DECIMAL(10,6) COMMENT '绾害',
    longitude DECIMAL(10,6) COMMENT '缁忓害',
    remark VARCHAR(500) COMMENT '澶囨敞',
    chain_hash VARCHAR(64) COMMENT '閾句笂鍝堝笇',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_trace_id (trace_id),
    INDEX idx_product_code (product_code),
    INDEX idx_batch_no (batch_no),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='婧簮璁板綍琛?;

CREATE TABLE IF NOT EXISTS qrcode_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    qr_code VARCHAR(255) NOT NULL UNIQUE COMMENT '浜岀淮鐮佸唴瀹?,
    product_code VARCHAR(50) NOT NULL COMMENT '浜у搧缂栫爜',
    batch_no VARCHAR(50) NOT NULL COMMENT '鎵规鍙?,
    trace_id VARCHAR(64) COMMENT '婧簮ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '鐘舵€? ACTIVE/INACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_qr_code (qr_code),
    INDEX idx_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜岀淮鐮佷俊鎭〃';

CREATE TABLE IF NOT EXISTS `planting_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `sow_time` DATETIME COMMENT '鎾鏃堕棿',
    `fertilizer_record` VARCHAR(500) COMMENT '鏂借偉璁板綍',
    `pesticide_record` VARCHAR(500) COMMENT '鍐滆嵂浣跨敤璁板綍',
    `irrigation_record` VARCHAR(500) COMMENT '鐏屾簤璁板綍',
    `soil_temperature` VARCHAR(50) COMMENT '鍦熷￥娓╁害',
    `soil_humidity` VARCHAR(50) COMMENT '鍦熷￥婀垮害',
    `harvest_time` DATETIME COMMENT '閲囨敹鏃堕棿',
    `responsible_person` VARCHAR(100) COMMENT '绉嶆璐熻矗浜?,
    `image_url` VARCHAR(500) COMMENT '鍥剧墖鍦板潃',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绉嶆璁板綍琛?;

CREATE TABLE IF NOT EXISTS `processing_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `processing_enterprise` VARCHAR(200) COMMENT '鍔犲伐浼佷笟',
    `processing_time` DATETIME COMMENT '鍔犲伐鏃堕棿',
    `processing_method` VARCHAR(200) COMMENT '鍔犲伐鏂瑰紡',
    `quality_result` VARCHAR(50) COMMENT '璐ㄦ缁撴灉',
    `inspector` VARCHAR(100) COMMENT '妫€娴嬩汉鍛?,
    `report_url` VARCHAR(500) COMMENT '妫€娴嬫姤鍛婂湴鍧€',
    `processing_temperature` VARCHAR(50) COMMENT '鍔犲伐娓╁害',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍔犲伐璁板綍琛?;

CREATE TABLE IF NOT EXISTS `logistics_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `logistics_company` VARCHAR(200) COMMENT '鐗╂祦鍏徃',
    `transport_vehicle` VARCHAR(100) COMMENT '杩愯緭杞﹁締',
    `driver_name` VARCHAR(50) COMMENT '鍙告満濮撳悕',
    `driver_phone` VARCHAR(20) COMMENT '鍙告満鐢佃瘽',
    `from_address` VARCHAR(500) COMMENT '鍙戣揣鍦板潃',
    `to_address` VARCHAR(500) COMMENT '鏀惰揣鍦板潃',
    `ship_time` DATETIME COMMENT '鍙戣揣鏃堕棿',
    `arrival_time` DATETIME COMMENT '鍒拌揣鏃堕棿',
    `transport_temperature` VARCHAR(50) COMMENT '杩愯緭娓╁害',
    `transport_humidity` VARCHAR(50) COMMENT '杩愯緭婀垮害',
    `logistics_status` VARCHAR(50) COMMENT '鐗╂祦鐘舵€?,
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐗╂祦璁板綍琛?;

CREATE TABLE IF NOT EXISTS `sales_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `seller` VARCHAR(200) COMMENT '閿€鍞晢',
    `sales_region` VARCHAR(200) COMMENT '閿€鍞湴鍖?,
    `listing_time` DATETIME COMMENT '涓婃灦鏃堕棿',
    `sales_price` DECIMAL(18,2) COMMENT '閿€鍞环鏍?,
    `stock_quantity` INT COMMENT '搴撳瓨鏁伴噺',
    `sales_status` VARCHAR(50) COMMENT '閿€鍞姸鎬?,
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='閿€鍞褰曡〃';

CREATE TABLE IF NOT EXISTS `quality_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `processing_record_id` BIGINT COMMENT '鍏宠仈鍔犲伐璁板綍ID',
    `quality_date` DATETIME COMMENT '璐ㄦ鏃ユ湡',
    `quality_result` VARCHAR(50) COMMENT '璐ㄦ缁撴灉',
    `inspector` VARCHAR(100) COMMENT '妫€娴嬩汉鍛?,
    `report_url` VARCHAR(500) COMMENT '鎶ュ憡鏂囦欢鍦板潃',
    `inspection_items` VARCHAR(500) COMMENT '妫€娴嬮」鐩?,
    `inspection_standard` VARCHAR(200) COMMENT '妫€娴嬫爣鍑?,
    `qualified` TINYINT(1) DEFAULT 1 COMMENT '鏄惁鍚堟牸: 0-涓嶅悎鏍? 1-鍚堟牸',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_processing_record_id` (`processing_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璐ㄦ鎶ュ憡琛?;

CREATE TABLE IF NOT EXISTS `stock_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '鎵规缂栧彿',
    `quantity` INT COMMENT '搴撳瓨鏁伴噺',
    `change_type` VARCHAR(20) COMMENT '搴撳瓨鍙樺姩绫诲瀷: IN-鍏ュ簱, OUT-鍑哄簱',
    `change_quantity` INT COMMENT '鍙樺姩鏁伴噺',
    `after_quantity` INT COMMENT '鍙樺姩鍚庡簱瀛?,
    `change_time` DATETIME COMMENT '鍙樺姩鏃堕棿',
    `operator_name` VARCHAR(100) COMMENT '鎿嶄綔浜?,
    `related_record_id` BIGINT COMMENT '鍏宠仈涓氬姟ID',
    `remark` VARCHAR(500) COMMENT '澶囨敞',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `version` INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    `deleted` INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (`id`),
    INDEX `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搴撳瓨璁板綍琛?;

CREATE TABLE IF NOT EXISTS chain_header (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    chain_id VARCHAR(64) NOT NULL UNIQUE COMMENT '閾綢D',
    chain_name VARCHAR(100) NOT NULL COMMENT '閾惧悕绉?,
    description VARCHAR(500) COMMENT '鎻忚堪',
    current_height BIGINT DEFAULT 0 COMMENT '褰撳墠楂樺害',
    latest_hash VARCHAR(64) COMMENT '鏈€鏂板尯鍧楀搱甯?,
    status VARCHAR(20) DEFAULT 'VALID' COMMENT '鐘舵€? VALID/INVALID',
    creator_id VARCHAR(64) COMMENT '鍒涘缓浜篒D',
    creator_name VARCHAR(100) COMMENT '鍒涘缓浜哄鍚?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_chain_id (chain_id),
    INDEX idx_chain_name (chain_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='閾惧ご淇℃伅琛?;

CREATE TABLE IF NOT EXISTS chain_block (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    chain_id VARCHAR(64) NOT NULL COMMENT '閾綢D',
    block_hash VARCHAR(64) NOT NULL UNIQUE COMMENT '鍖哄潡鍝堝笇',
    previous_hash VARCHAR(64) NOT NULL COMMENT '鍓嶄竴鍖哄潡鍝堝笇',
    data_hash VARCHAR(64) NOT NULL COMMENT '鏁版嵁鍝堝笇',
    data_content TEXT COMMENT '鏁版嵁鍐呭(JSON)',
    nonce BIGINT DEFAULT 0 COMMENT '闅忔満鏁?,
    timestamp BIGINT COMMENT '鏃堕棿鎴?,
    operator_id VARCHAR(64) COMMENT '鎿嶄綔鑰匢D',
    operator_name VARCHAR(100) COMMENT '鎿嶄綔鑰呭鍚?,
    operation_type VARCHAR(50) COMMENT '鎿嶄綔绫诲瀷',
    product_code VARCHAR(50) COMMENT '浜у搧缂栫爜',
    batch_no VARCHAR(50) COMMENT '鎵规鍙?,
    block_height INT DEFAULT 0 COMMENT '鍖哄潡楂樺害',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_chain_id (chain_id),
    INDEX idx_block_hash (block_hash),
    INDEX idx_product_code (product_code),
    INDEX idx_batch_no (batch_no),
    INDEX idx_block_height (block_height)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍖哄潡鏁版嵁琛?;

CREATE TABLE IF NOT EXISTS chain_verification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    chain_id VARCHAR(64) NOT NULL COMMENT '閾綢D',
    block_hash VARCHAR(64) NOT NULL COMMENT '鍖哄潡鍝堝笇',
    data_hash VARCHAR(64) COMMENT '鏁版嵁鍝堝笇',
    original_data TEXT COMMENT '鍘熷鏁版嵁',
    verified TINYINT(1) DEFAULT 0 COMMENT '楠岃瘉缁撴灉: 0-鏈€氳繃, 1-閫氳繃',
    verification_result VARCHAR(500) COMMENT '楠岃瘉缁撴灉鎻忚堪',
    operator_id VARCHAR(64) COMMENT '鎿嶄綔鑰匢D',
    operator_name VARCHAR(100) COMMENT '鎿嶄綔鑰呭鍚?,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    version INT DEFAULT 1 COMMENT '鐗堟湰鍙?,
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_chain_id (chain_id),
    INDEX idx_block_hash (block_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楠岃瘉璁板綍琛?;

CREATE TABLE IF NOT EXISTS admin_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏃ュ織ID',
    log_type VARCHAR(50) COMMENT '鏃ュ織绫诲瀷: LOGIN/OPERATION/EXCEPTION',
    user_id BIGINT COMMENT '鐢ㄦ埛ID',
    username VARCHAR(50) COMMENT '鐢ㄦ埛鍚?,
    operation VARCHAR(200) COMMENT '鎿嶄綔鎻忚堪',
    module VARCHAR(50) COMMENT '鎿嶄綔妯″潡',
    method VARCHAR(10) COMMENT '璇锋眰鏂规硶',
    request_url VARCHAR(500) COMMENT '璇锋眰URL',
    request_param TEXT COMMENT '璇锋眰鍙傛暟',
    response_result TEXT COMMENT '鍝嶅簲缁撴灉',
    ip_address VARCHAR(50) COMMENT 'IP鍦板潃',
    user_agent VARCHAR(500) COMMENT '鐢ㄦ埛浠ｇ悊',
    execution_time BIGINT COMMENT '鎵ц鏃堕棿(ms)',
    success TINYINT(1) DEFAULT 1 COMMENT '鏄惁鎴愬姛',
    error_message TEXT COMMENT '閿欒淇℃伅',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_log_type (log_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鎿嶄綔鏃ュ織琛?;

CREATE TABLE IF NOT EXISTS statistics_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    stat_type VARCHAR(50) NOT NULL COMMENT '缁熻绫诲瀷',
    stat_date DATE NOT NULL COMMENT '缁熻鏃ユ湡',
    product_count BIGINT DEFAULT 0 COMMENT '浜у搧鏁伴噺',
    batch_count BIGINT DEFAULT 0 COMMENT '鎵规鏁伴噺',
    trace_count BIGINT DEFAULT 0 COMMENT '婧簮璁板綍鏁伴噺',
    block_count BIGINT DEFAULT 0 COMMENT '鍖哄潡鏁伴噺',
    user_count BIGINT DEFAULT 0 COMMENT '鐢ㄦ埛鏁伴噺',
    data_json JSON COMMENT '缁熻鏁版嵁(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    deleted INT DEFAULT 0 COMMENT '鍒犻櫎鏍囪瘑',
    PRIMARY KEY (id),
    UNIQUE KEY uk_stat_date_type (stat_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缁熻鏁版嵁琛?;

