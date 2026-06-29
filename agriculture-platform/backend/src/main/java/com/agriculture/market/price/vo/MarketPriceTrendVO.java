package com.agriculture.market.price.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MarketPriceTrendVO {
    private String date;
    private BigDecimal avgPrice;
}
