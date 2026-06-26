package com.agri.telemetry.mapper;

import com.agri.telemetry.entity.TelemetryAlarm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警Mapper
 */
@Mapper
public interface TelemetryAlarmMapper extends BaseMapper<TelemetryAlarm> {
}
