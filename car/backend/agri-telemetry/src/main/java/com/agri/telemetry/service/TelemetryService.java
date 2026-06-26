package com.agri.telemetry.service;

import com.agri.telemetry.entity.TelemetryAlarm;
import com.agri.telemetry.entity.TelemetryData;
import com.agri.common.vo.PageResult;
import com.agri.telemetry.vo.TelemetryStatsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 遥测数据服务接口
 */
public interface TelemetryService {

    /**
     * 获取设备最新遥测数据
     */
    Map<String, Object> getLatestTelemetry(Long vehicleId);

    /**
     * 查询历史轨迹
     */
    List<TelemetryData> getTrajectory(Long vehicleId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询告警
     */
    PageResult<TelemetryAlarm> listAlarms(int pageNum, int pageSize, Long vehicleId, String alarmType, String status);

    /**
     * 确认告警
     */
    void acknowledgeAlarm(Long alarmId, Long userId);

    /**
     * 解决告警
     */
    void resolveAlarm(Long alarmId);

    /**
     * 获取在线客户端数
     */
    int getOnlineClientCount();

    /**
     * 获取遥测统计
     */
    TelemetryStatsVO getTelemetryStats();
}
