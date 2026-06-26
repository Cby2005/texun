package com.agri.telemetry.controller;

import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.telemetry.entity.TelemetryAlarm;
import com.agri.telemetry.entity.TelemetryData;
import com.agri.telemetry.service.TelemetryService;
import com.agri.telemetry.vo.TelemetryStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 遥测数据控制器
 */
@Tag(name = "遥测数据管理", description = "实时遥测数据查询、历史轨迹、告警管理、WebSocket在线统计")
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @Operation(summary = "获取设备最新遥测数据", description = "优先从Redis缓存获取，缓存未命中则查数据库")
    @GetMapping("/latest/{vehicleId}")
    public R<Map<String, Object>> getLatestTelemetry(@PathVariable Long vehicleId) {
        return R.ok(telemetryService.getLatestTelemetry(vehicleId));
    }

    @Operation(summary = "查询历史轨迹")
    @GetMapping("/trajectory/{vehicleId}")
    public R<List<TelemetryData>> getTrajectory(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(telemetryService.getTrajectory(vehicleId, startTime, endTime));
    }

    @Operation(summary = "分页查询告警")
    @GetMapping("/alarms")
    public R<PageResult<TelemetryAlarm>> listAlarms(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String status) {
        return R.ok(telemetryService.listAlarms(pageNum, pageSize, vehicleId, alarmType, status));
    }

    @Operation(summary = "确认告警")
    @PostMapping("/alarms/{id}/acknowledge")
    public R<Void> acknowledgeAlarm(@PathVariable Long id, @RequestParam Long userId) {
        telemetryService.acknowledgeAlarm(id, userId);
        return R.ok();
    }

    @Operation(summary = "解决告警")
    @PostMapping("/alarms/{id}/resolve")
    public R<Void> resolveAlarm(@PathVariable Long id) {
        telemetryService.resolveAlarm(id);
        return R.ok();
    }

    @Operation(summary = "获取WebSocket在线客户端数")
    @GetMapping("/ws/online-count")
    public R<Integer> getOnlineCount() {
        return R.ok(telemetryService.getOnlineClientCount());
    }

    @Operation(summary = "获取遥测统计信息")
    @GetMapping("/stats")
    public R<TelemetryStatsVO> getTelemetryStats() {
        return R.ok(telemetryService.getTelemetryStats());
    }
}
