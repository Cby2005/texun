package com.agriculture.greenhouse.service.impl;

import com.agriculture.greenhouse.entity.StrawberryPlantingRecord;
import com.agriculture.greenhouse.mapper.StrawberryPlantingRecordMapper;
import com.agriculture.greenhouse.service.StrawberryPlantingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StrawberryPlantingServiceImpl extends ServiceImpl<StrawberryPlantingRecordMapper, StrawberryPlantingRecord>
        implements StrawberryPlantingService {

    @Override
    public Map<String, Object> getStatistics() {
        List<StrawberryPlantingRecord> all = list(
                new LambdaQueryWrapper<StrawberryPlantingRecord>().eq(StrawberryPlantingRecord::getDeleted, 0));
        int recordCount = all.size();
        int totalPlants = 0;
        double totalCost = 0;
        int abnormalPlants = 0;
        Map<String, Integer> statusCount = new LinkedHashMap<>();
        for (StrawberryPlantingRecord r : all) {
            totalPlants += (r.getPlantCount() != null ? r.getPlantCount() : 0);
            totalCost += (r.getTotalCost() != null ? r.getTotalCost().doubleValue() : 0);
            String status = r.getGrowthStatus() != null ? r.getGrowthStatus() : "未知";
            statusCount.merge(status, r.getPlantCount() != null ? r.getPlantCount() : 0, Integer::sum);
            if ("异常".equals(status)) abnormalPlants += (r.getPlantCount() != null ? r.getPlantCount() : 0);
        }
        double avgUnitCost = totalPlants > 0
                ? BigDecimal.valueOf(totalCost).divide(BigDecimal.valueOf(totalPlants), 2, RoundingMode.HALF_UP).doubleValue()
                : 0;
        String mainGrowthStatus = statusCount.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("未知");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordCount", recordCount);
        result.put("totalPlants", totalPlants);
        result.put("totalCost", BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP));
        result.put("averageUnitCost", BigDecimal.valueOf(avgUnitCost).setScale(2, RoundingMode.HALF_UP));
        result.put("mainGrowthStatus", mainGrowthStatus);
        result.put("abnormalPlants", abnormalPlants);
        result.put("areaStats", getAreaStatistics());
        result.put("growthStatusStats", statusCount);
        return result;
    }

    @Override
    public List<Map<String, Object>> getAreaStatistics() {
        List<StrawberryPlantingRecord> all = list(
                new LambdaQueryWrapper<StrawberryPlantingRecord>().eq(StrawberryPlantingRecord::getDeleted, 0));
        Map<String, Integer> areaPlants = new LinkedHashMap<>();
        Map<String, Set<String>> areaStatuses = new LinkedHashMap<>();
        for (StrawberryPlantingRecord r : all) {
            String key = (r.getAreaCode() != null ? r.getAreaCode() : "") + "|" + (r.getAreaName() != null ? r.getAreaName() : "");
            areaPlants.merge(key, r.getPlantCount() != null ? r.getPlantCount() : 0, Integer::sum);
            areaStatuses.computeIfAbsent(key, k -> new HashSet<>()).add(r.getGrowthStatus() != null ? r.getGrowthStatus() : "");
        }
        return areaPlants.entrySet().stream().map(e -> {
            String[] parts = e.getKey().split("\\|", 2);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("areaCode", parts[0]);
            m.put("areaName", parts.length > 1 ? parts[1] : "");
            m.put("plantCount", e.getValue());
            Set<String> statuses = areaStatuses.getOrDefault(e.getKey(), Collections.emptySet());
            String areaStatus;
            if (statuses.contains("异常")) areaStatus = "ABNORMAL";
            else if (e.getValue() == 0) areaStatus = "EMPTY";
            else areaStatus = "NORMAL";
            m.put("status", areaStatus);
            return m;
        }).collect(Collectors.toList());
    }
}
