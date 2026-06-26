package com.agri.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 设备型号请求DTO
 */
@Data
@Schema(description = "设备型号请求")
public class ModelDTO {

    @Schema(description = "型号ID（更新时使用）")
    private Long id;

    @NotBlank(message = "型号编码不能为空")
    @Schema(description = "型号编码", example = "AGV-T200")
    private String modelCode;

    @NotBlank(message = "型号名称不能为空")
    @Schema(description = "型号名称", example = "智能喷洒车T200")
    private String modelName;

    @Schema(description = "制造商")
    private String manufacturer;

    @Schema(description = "车辆类型", example = "SPRAY")
    private String vehicleType;

    @Schema(description = "最大速度(km/h)")
    private Double maxSpeed;

    @Schema(description = "电池容量(kWh)")
    private Double batteryCapacity;

    @Schema(description = "最大载重(kg)")
    private Double maxPayload;

    @Schema(description = "作业宽度(m)")
    private Double workWidth;

    @Schema(description = "转弯半径(m)")
    private Double turnRadius;

    @Schema(description = "规格参数JSON")
    private String specs;
}
