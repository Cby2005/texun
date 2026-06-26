package com.agri.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 故障记录请求DTO
 */
@Data
@Schema(description = "故障记录请求")
public class FaultRecordDTO {

    @NotNull(message = "设备ID不能为空")
    @Schema(description = "设备ID")
    private Long vehicleId;

    @NotBlank(message = "故障编码不能为空")
    @Schema(description = "故障编码", example = "F001")
    private String faultCode;

    @NotBlank(message = "故障类型不能为空")
    @Schema(description = "故障类型", example = "MOTOR")
    private String faultType;

    @Schema(description = "故障级别", example = "MEDIUM")
    private String faultLevel;

    @NotBlank(message = "故障描述不能为空")
    @Schema(description = "故障描述")
    private String description;
}
