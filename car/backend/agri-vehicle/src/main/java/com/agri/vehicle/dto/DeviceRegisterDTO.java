package com.agri.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备注册请求DTO
 */
@Data
@Schema(description = "设备注册请求")
public class DeviceRegisterDTO {

    @NotBlank(message = "设备编码不能为空")
    @Schema(description = "设备编码", example = "AGV-001")
    private String deviceCode;

    @NotBlank(message = "设备名称不能为空")
    @Schema(description = "设备名称", example = "1号无人车")
    private String deviceName;

    @NotNull(message = "设备型号不能为空")
    @Schema(description = "设备型号ID")
    private Long modelId;

    @Schema(description = "所属农场ID")
    private Long farmId;

    @Schema(description = "SIM卡号")
    private String simCard;

    @Schema(description = "配置参数JSON")
    private String configParams;
}
