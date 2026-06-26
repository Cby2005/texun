package com.agriculture.trace.production.service.impl;

import com.agriculture.trace.production.entity.TraceProductionRecord;
import com.agriculture.trace.production.mapper.TraceProductionRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("productionRecordService")
public class TraceProductionRecordServiceImpl extends ServiceImpl<TraceProductionRecordMapper, TraceProductionRecord> {
}
