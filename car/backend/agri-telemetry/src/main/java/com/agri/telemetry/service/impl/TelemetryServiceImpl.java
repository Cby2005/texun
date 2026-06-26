package com.agri.telemetry.service.impl;

import com.agri.common.constant.Constants;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.common.vo.PageResult;
import com.agri.telemetry.entity.TelemetryAlarm;
import com.agri.telemetry.entity.TelemetryData;
import com.agri.telemetry.mapper.TelemetryAlarmMapper;
import com.agri.telemetry.mapper.TelemetryDataMapper;
import com.agri.telemetry.service.TelemetryService;
import com.agri.telemetry.vo.TelemetryStatsVO;
import com.agri.telemetry.websocket.TelemetryWebSocketHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 遥测数据服务实现
 *
 * Redis Key 设计：
 *   agri:vehicle:telemetry:{vehicleId}  ->  最新遥测数据Map  过期时间: 5分钟
 *   agri:vehicle:location:{vehicleId}   ->  位置JSON         过期时间: 5分钟
 *
 * 索引设计：
 *   telemetry_data.vehicle_id    ->  INDEX（设备数据查询）
 *   telemetry_data.report_time   ->  INDEX（时间范围查询）
 *   telemetry_alarm.vehicle_id   ->  INDEX（设备告警查询）
 *   telemetry_alarm.status       ->  INDEX（告警状态筛选）
 *   telemetry_alarm.alarm_type   ->  INDEX（告警类型筛选）
 *
 * Kafka Topic 说明：
 *   vehicle.telemetry.raw   -> 原始遥测数据（Consumer: TelemetryKafkaConsumer）
 *   vehicle.alarm           -> 设备告警（Consumer: TelemetryKafkaConsumer）
 *   vehicle.location        -> 位置信息（由simulator发送）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryDataMapper telemetryDataMapper;
    private final TelemetryAlarmMapper alarmMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TelemetryWebSocketHandler webSocketHandler;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLatestTelemetry(Long vehicleId) {
        // 先从Redis获取（Key: agri:vehicle:telemetry:{vehicleId}，过期5分钟）
        String redisKey = Constants.REDIS_VEHICLE_TELEMETRY + vehicleId;
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached instanceof Map) {
            return (Map<String, Object>) cached;
        }

        // Redis没有则从数据库获取最新一条
        TelemetryData latest = telemetryDataMapper.selectOne(
                new LambdaQueryWrapper<TelemetryData>()
                        .eq(TelemetryData::getVehicleId, vehicleId)
                        .orderByDesc(TelemetryData::getReportTime)
                        .last("LIMIT 1"));

        if (latest == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        return Map.of(
                "vehicleId", latest.getVehicleId(),
                "latitude", latest.getLatitude() != null ? latest.getLatitude() : 0,
                "longitude", latest.getLongitude() != null ? latest.getLongitude() : 0,
                "speed", latest.getSpeed() != null ? latest.getSpeed() : 0,
                "batteryLevel", latest.getBatteryLevel() != null ? latest.getBatteryLevel() : 0,
                "taskProgress", latest.getTaskProgress() != null ? latest.getTaskProgress() : 0,
                "reportTime", latest.getReportTime().toString()
        );
    }

    @Override
    public List<TelemetryData> getTrajectory(Long vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        return telemetryDataMapper.selectByVehicleAndTimeRange(vehicleId, startTime, endTime);
    }

    @Override
    public PageResult<TelemetryAlarm> listAlarms(int pageNum, int pageSize, Long vehicleId,
                                                   String alarmType, String status) {
        LambdaQueryWrapper<TelemetryAlarm> wrapper = new LambdaQueryWrapper<>();
        if (vehicleId != null) {
            wrapper.eq(TelemetryAlarm::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(alarmType)) {
            wrapper.eq(TelemetryAlarm::getAlarmType, alarmType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(TelemetryAlarm::getStatus, status);
        }
        wrapper.orderByDesc(TelemetryAlarm::getCreateTime);

        Page<TelemetryAlarm> page = alarmMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getTotal(), pageNum, pageSize, page.getRecords());
    }

    @Override
    public void acknowledgeAlarm(Long alarmId, Long userId) {
        TelemetryAlarm alarm = alarmMapper.selectById(alarmId);
        if (alarm == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        alarm.setStatus("ACKNOWLEDGED");
        alarm.setAckUserId(userId);
        alarm.setAckTime(LocalDateTime.now());
        alarmMapper.updateById(alarm);
        log.info("告警已确认: alarmId={}, userId={}", alarmId, userId);
    }

    @Override
    public void resolveAlarm(Long alarmId) {
        TelemetryAlarm alarm = alarmMapper.selectById(alarmId);
        if (alarm == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        alarm.setStatus("RESOLVED");
        alarm.setResolvedTime(LocalDateTime.now());
        alarmMapper.updateById(alarm);
        log.info("告警已解决: alarmId={}", alarmId);
    }

    @Override
    public int getOnlineClientCount() {
        return webSocketHandler.getOnlineCount();
    }

    @Override
    public TelemetryStatsVO getTelemetryStats() {
        TelemetryStatsVO stats = new TelemetryStatsVO();

        // 今日数据条数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayCount = telemetryDataMapper.selectCount(
                new LambdaQueryWrapper<TelemetryData>()
                        .ge(TelemetryData::getCreateTime, todayStart));
        stats.setTodayDataCount(todayCount);

        // 今日告警数
        Long todayAlarmCount = alarmMapper.selectCount(
                new LambdaQueryWrapper<TelemetryAlarm>()
                        .ge(TelemetryAlarm::getCreateTime, todayStart));
        stats.setTodayAlarmCount(todayAlarmCount);

        // 未处理告警数
        Long unhandledCount = alarmMapper.selectCount(
                new LambdaQueryWrapper<TelemetryAlarm>()
                        .eq(TelemetryAlarm::getStatus, "OPEN"));
        stats.setUnhandledAlarmCount(unhandledCount);

        // WebSocket在线客户端数
        stats.setWsOnlineCount(webSocketHandler.getOnlineCount());

        // 活跃设备数（今日有数据上报的设备）
        stats.setActiveVehicleCount(todayCount); // 简化：用数据条数近似

        return stats;
    }
}
