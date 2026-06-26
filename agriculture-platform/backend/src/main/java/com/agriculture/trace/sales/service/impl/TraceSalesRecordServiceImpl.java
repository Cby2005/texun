package com.agriculture.trace.sales.service.impl;

import com.agriculture.trace.sales.entity.TraceSalesRecord;
import com.agriculture.trace.sales.mapper.TraceSalesRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("salesRecordService")
public class TraceSalesRecordServiceImpl extends ServiceImpl<TraceSalesRecordMapper, TraceSalesRecord> {
}
