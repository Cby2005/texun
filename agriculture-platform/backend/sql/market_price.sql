-- ========================================
-- 草莓市场价格模块 DDL + 模拟数据
-- ========================================

CREATE TABLE IF NOT EXISTS strawberry_market_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    market_name VARCHAR(100) COMMENT '市场名称',
    product_name VARCHAR(100) DEFAULT '草莓' COMMENT '产品名称',
    variety_name VARCHAR(100) COMMENT '品种名称',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    min_price DECIMAL(10,2) COMMENT '最低价格',
    max_price DECIMAL(10,2) COMMENT '最高价格',
    avg_price DECIMAL(10,2) COMMENT '平均价格',
    unit VARCHAR(20) DEFAULT '元/公斤' COMMENT '单位',
    price_date DATE COMMENT '价格日期',
    data_source VARCHAR(100) COMMENT '数据来源',
    source_url VARCHAR(255) COMMENT '来源地址',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_product_date (product_name, price_date),
    INDEX idx_market_name (market_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='草莓市场价格表';

CREATE TABLE IF NOT EXISTS strawberry_price_alert_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    market_name VARCHAR(100) COMMENT '市场名称',
    product_name VARCHAR(100) DEFAULT '草莓' COMMENT '产品名称',
    current_price DECIMAL(10,2) COMMENT '当前价格',
    alert_content VARCHAR(255) COMMENT '提醒内容',
    alert_level VARCHAR(30) COMMENT '提醒级别：NORMAL/IMPORTANT/URGENT',
    read_status TINYINT DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='草莓价格提醒记录表';

-- 模拟数据插入（郑州万邦市场 近7天价格）
INSERT IGNORE INTO strawberry_market_price (market_name, product_name, province, city, min_price, max_price, avg_price, unit, price_date, data_source, source_url, create_time, update_time) VALUES
('郑州万邦市场', '草莓', '河南', '郑州', 15.00, 18.60, 16.80, '元/公斤', '2026-06-22', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 15.50, 18.90, 17.20, '元/公斤', '2026-06-23', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 15.80, 19.20, 17.50, '元/公斤', '2026-06-24', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 16.00, 20.00, 18.10, '元/公斤', '2026-06-25', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 16.50, 20.50, 18.30, '元/公斤', '2026-06-26', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 16.00, 20.00, 18.00, '元/公斤', '2026-06-27', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('郑州万邦市场', '草莓', '河南', '郑州', 17.00, 21.00, 18.60, '元/公斤', '2026-06-28', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
-- 其他市场当日价格
('北京新发地', '草莓', '北京', '北京', 20.00, 24.00, 22.00, '元/公斤', '2026-06-28', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('许昌批发市场', '草莓', '河南', '许昌', 15.50, 19.00, 17.20, '元/公斤', '2026-06-28', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW()),
('洛阳宏进市场', '草莓', '河南', '洛阳', 16.00, 20.00, 18.00, '元/公斤', '2026-06-28', '全国农产品批发市场价格信息系统', 'https://pfsc.agri.cn/#/priceMarket', NOW(), NOW());
