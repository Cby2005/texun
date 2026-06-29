package com.agriculture.market.price.service.impl;

import com.agriculture.market.price.service.MarketPriceService;
import com.agriculture.market.price.vo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MarketPriceServiceImpl implements MarketPriceService {

    /**
     * 获取草莓价格概览（模拟数据，后续对接真实接口）
     */
    @Override
    public MarketPriceSummaryVO getSummary() {
        MarketPriceSummaryVO vo = new MarketPriceSummaryVO();
        List<MarketPriceTrendVO> trend = getTrend(7);
        BigDecimal todayAvg = trend.get(trend.size() - 1).getAvgPrice();
        BigDecimal yesterdayAvg = trend.get(trend.size() - 2).getAvgPrice();
        BigDecimal maxPrice = new BigDecimal("22.00");
        BigDecimal minPrice = new BigDecimal("15.00");

        vo.setAvgPrice(todayAvg);
        vo.setMaxPrice(maxPrice);
        vo.setMinPrice(minPrice);
        vo.setUnit("元/公斤");

        // 涨跌幅
        BigDecimal changeRate = todayAvg.subtract(yesterdayAvg)
                .divide(yesterdayAvg, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        vo.setChangeRate(changeRate);

        // 价格状态
        BigDecimal avg7 = trend.stream()
                .map(MarketPriceTrendVO::getAvgPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal("7"), 2, RoundingMode.HALF_UP);
        if (todayAvg.compareTo(avg7.multiply(new BigDecimal("1.05"))) > 0) vo.setPriceStatus("HIGH");
        else if (todayAvg.compareTo(avg7.multiply(new BigDecimal("0.95"))) < 0) vo.setPriceStatus("LOW");
        else vo.setPriceStatus("STABLE");

        vo.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        vo.setSuggestion(generateSuggestion(vo, trend));
        return vo;
    }

    @Override
    public List<MarketPriceTrendVO> getTrend(int days) {
        // 模拟近7天数据
        double[] prices = {16.8, 17.2, 17.5, 18.1, 18.3, 18.0, 18.6};
        if (days > prices.length) days = prices.length;
        LocalDate today = LocalDate.now();
        List<MarketPriceTrendVO> list = new ArrayList<>();
        for (int i = prices.length - days; i < prices.length; i++) {
            MarketPriceTrendVO vo = new MarketPriceTrendVO();
            vo.setDate(today.minusDays(prices.length - 1 - i).format(DateTimeFormatter.ofPattern("MM-dd")));
            vo.setAvgPrice(BigDecimal.valueOf(prices[i]));
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<MarketPriceCompareVO> getCompare() {
        return Arrays.asList(
            new MarketPriceCompareVO("郑州万邦市场", new BigDecimal("18.60")),
            new MarketPriceCompareVO("北京新发地", new BigDecimal("22.00")),
            new MarketPriceCompareVO("许昌批发市场", new BigDecimal("17.20")),
            new MarketPriceCompareVO("洛阳宏进市场", new BigDecimal("18.00"))
        );
    }

    @Override
    public List<MarketPriceAlertVO> getAlerts() {
        List<MarketPriceAlertVO> list = new ArrayList<>();
        MarketPriceSummaryVO summary = getSummary();
        BigDecimal avg = summary.getAvgPrice();

        // 价格高于20元/公斤
        if (avg.compareTo(new BigDecimal("20")) > 0) {
            list.add(buildAlert("草莓价格高于20元/公斤，建议关注采摘销售", "IMPORTANT"));
        }
        // 价格低于15元/公斤
        if (avg.compareTo(new BigDecimal("15")) < 0) {
            list.add(buildAlert("草莓价格低于15元/公斤，建议暂缓大批量销售", "IMPORTANT"));
        }
        // 连续下跌判断
        List<MarketPriceTrendVO> trend = getTrend(4);
        if (isConsecutiveFall(trend)) {
            list.add(buildAlert("草莓价格连续下跌，请关注市场行情变化", "NORMAL"));
        }

        if (list.isEmpty()) {
            list.add(buildAlert("当前草莓价格平稳，无异常预警", "NORMAL"));
        }
        return list;
    }

    @Override
    public String generateSuggestion(MarketPriceSummaryVO summary, List<MarketPriceTrendVO> trend) {
        BigDecimal today = summary.getAvgPrice();
        BigDecimal avg7 = trend.stream()
                .map(MarketPriceTrendVO::getAvgPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(trend.size()), 2, RoundingMode.HALF_UP);

        // 连续上涨判断
        List<MarketPriceTrendVO> last4 = trend.size() >= 4
                ? trend.subList(trend.size() - 4, trend.size()) : trend;
        boolean up3 = isConsecutiveRise(last4);
        boolean down3 = isConsecutiveFall(last4);

        if (today.compareTo(avg7.multiply(new BigDecimal("1.05"))) > 0 || up3) {
            return "当前草莓价格高于近7日均价，建议安排成熟草莓采摘销售。";
        }
        if (today.compareTo(avg7.multiply(new BigDecimal("0.95"))) < 0 || down3) {
            return "当前草莓价格低于近7日均价，建议暂缓大批量销售，关注后续行情变化。";
        }
        if (up3) {
            return "草莓价格连续上涨，建议关注市场需求，适当增加采摘和销售计划。";
        }
        if (down3) {
            return "草莓价格连续下跌，建议谨慎销售，避免集中上市造成收益下降。";
        }
        return "当前草莓价格整体平稳，可按原计划安排采摘和销售。";
    }

    private boolean isConsecutiveRise(List<MarketPriceTrendVO> list) {
        if (list.size() < 4) return false;
        int n = list.size();
        for (int i = n - 3; i < n; i++) {
            if (list.get(i).getAvgPrice().compareTo(list.get(i - 1).getAvgPrice()) <= 0) return false;
        }
        return true;
    }

    private boolean isConsecutiveFall(List<MarketPriceTrendVO> list) {
        if (list.size() < 4) return false;
        int n = list.size();
        for (int i = n - 3; i < n; i++) {
            if (list.get(i).getAvgPrice().compareTo(list.get(i - 1).getAvgPrice()) >= 0) return false;
        }
        return true;
    }

    private MarketPriceAlertVO buildAlert(String content, String level) {
        MarketPriceAlertVO vo = new MarketPriceAlertVO();
        vo.setAlertContent(content);
        vo.setAlertLevel(level);
        vo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return vo;
    }
}
