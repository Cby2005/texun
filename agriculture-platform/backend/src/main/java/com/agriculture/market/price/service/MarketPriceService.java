package com.agriculture.market.price.service;

import com.agriculture.market.price.vo.*;
import java.util.List;

public interface MarketPriceService {
    MarketPriceSummaryVO getSummary();
    List<MarketPriceTrendVO> getTrend(int days);
    List<MarketPriceCompareVO> getCompare();
    List<MarketPriceAlertVO> getAlerts();
    /** 生成销售建议 */
    String generateSuggestion(MarketPriceSummaryVO summary, List<MarketPriceTrendVO> trend);
}
