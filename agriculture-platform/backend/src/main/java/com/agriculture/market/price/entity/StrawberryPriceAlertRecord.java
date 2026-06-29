package com.agriculture.market.price.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_price_alert_record")
public class StrawberryPriceAlertRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String marketName;
    private String productName;
    private BigDecimal currentPrice;
    private String alertContent;
    private String alertLevel;
    private Integer readStatus;
    private LocalDateTime createTime;
}
