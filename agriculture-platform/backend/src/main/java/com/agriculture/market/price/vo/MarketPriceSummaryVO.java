package com.agriculture.market.price.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MarketPriceSummaryVO {
    private BigDecimal avgPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private String unit;
    private BigDecimal changeRate;
    private String priceStatus;
    private String updateTime;
    private String suggestion;
}
