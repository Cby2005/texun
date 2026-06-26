package com.agri.vehicle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备统计响应VO
 */
@Data
@Schema(description = "设备统计响应")
public class DeviceStatsVO {

    @Schema(description = "设备总数")
    private Long totalCount;

    @Schema(description = "在线数量")
    private Long onlineCount;

    @Schema(description = "作业中数量")
    private Long workingCount;

    @Schema(description = "离线数量")
    private Long offlineCount;

    @Schema(description = "故障数量")
    private Long faultCount;

    @Schema(description = "维护中数量")
    private Long maintenanceCount;

    @Schema(description = "空闲数量")
    private Long idleCount;

    @Schema(description = "在线率(%)")
    private Double onlineRate;
}
