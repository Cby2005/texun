package com.agri.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 设备更新请求DTO
 */
@Data
@Schema(description = "设备更新请求")
public class DeviceUpdateDTO {

    @NotNull(message = "设备ID不能为空")
    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "所属农场ID")
    private Long farmId;

    @Schema(description = "固件版本")
    private String firmwareVersion;

    @Schema(description = "SIM卡号")
    private String simCard;

    @Schema(description = "配置参数JSON")
    private String configParams;
}
