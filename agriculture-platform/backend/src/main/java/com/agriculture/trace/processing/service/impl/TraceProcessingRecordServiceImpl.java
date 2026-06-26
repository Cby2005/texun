package com.agriculture.trace.processing.service.impl;

import com.agriculture.trace.processing.entity.TraceProcessingRecord;
import com.agriculture.trace.processing.mapper.TraceProcessingRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("processingRecordService")
public class TraceProcessingRecordServiceImpl extends ServiceImpl<TraceProcessingRecordMapper, TraceProcessingRecord> {
}
