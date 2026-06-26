package com.agriculture.trace.quality.service.impl;

import com.agriculture.trace.quality.entity.TraceQualityRecord;
import com.agriculture.trace.quality.mapper.TraceQualityRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("qualityRecordService")
public class TraceQualityRecordServiceImpl extends ServiceImpl<TraceQualityRecordMapper, TraceQualityRecord> {
}
