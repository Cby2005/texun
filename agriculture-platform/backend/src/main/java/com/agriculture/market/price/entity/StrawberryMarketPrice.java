package com.agriculture.market.price.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_market_price")
public class StrawberryMarketPrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String marketName;
    private String productName;
    private String varietyName;
    private String province;
    private String city;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal avgPrice;
    private String unit;
    private LocalDate priceDate;
    private String dataSource;
    private String sourceUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
