package com.agriculture.market.price.controller;

import com.agriculture.common.result.Result;
import com.agriculture.market.price.service.MarketPriceService;
import com.agriculture.market.price.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 草莓市场价格接口
 */
@RestController
@RequestMapping("/api/market/price")
@RequiredArgsConstructor
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    @GetMapping("/summary")
    public Result<MarketPriceSummaryVO> getSummary() {
        return Result.ok(marketPriceService.getSummary());
    }

    @GetMapping("/trend")
    public Result<List<MarketPriceTrendVO>> getTrend(@RequestParam(defaultValue = "7") int days) {
        return Result.ok(marketPriceService.getTrend(days));
    }

    @GetMapping("/compare")
    public Result<List<MarketPriceCompareVO>> getCompare() {
        return Result.ok(marketPriceService.getCompare());
    }

    @GetMapping("/alerts")
    public Result<List<MarketPriceAlertVO>> getAlerts() {
        return Result.ok(marketPriceService.getAlerts());
    }
}
