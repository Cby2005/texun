package com.agriculture.market.price.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPriceCompareVO {
    private String marketName;
    private BigDecimal avgPrice;
}
