package com.agriculture.trace.logistics.service.impl;

import com.agriculture.trace.logistics.entity.TraceLogisticsRecord;
import com.agriculture.trace.logistics.mapper.TraceLogisticsRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("logisticsRecordService")
public class TraceLogisticsRecordServiceImpl extends ServiceImpl<TraceLogisticsRecordMapper, TraceLogisticsRecord> {
}
