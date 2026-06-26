package com.agriculture.trace.storage.service.impl;

import com.agriculture.trace.storage.entity.TraceStorageRecord;
import com.agriculture.trace.storage.mapper.TraceStorageRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("storageRecordService")
public class TraceStorageRecordServiceImpl extends ServiceImpl<TraceStorageRecordMapper, TraceStorageRecord> {
}
