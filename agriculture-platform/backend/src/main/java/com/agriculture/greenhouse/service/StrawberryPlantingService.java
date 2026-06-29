package com.agriculture.greenhouse.service;

import com.agriculture.greenhouse.entity.StrawberryPlantingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

public interface StrawberryPlantingService extends IService<StrawberryPlantingRecord> {
    Map<String, Object> getStatistics();
    List<Map<String, Object>> getAreaStatistics();
}
