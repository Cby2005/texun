package com.agri.telemetry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 遥测统计VO
 */
@Data
@Schema(description = "遥测统计信息")
public class TelemetryStatsVO {

    @Schema(description = "今日数据条数")
    private Long todayDataCount;

    @Schema(description = "今日告警数")
    private Long todayAlarmCount;

    @Schema(description = "未处理告警数")
    private Long unhandledAlarmCount;

    @Schema(description = "WebSocket在线客户端数")
    private Integer wsOnlineCount;

    @Schema(description = "活跃设备数")
    private Long activeVehicleCount;
}
