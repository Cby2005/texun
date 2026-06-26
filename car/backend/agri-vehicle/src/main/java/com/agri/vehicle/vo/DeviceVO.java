package com.agri.vehicle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备详情响应VO
 */
@Data
@Schema(description = "设备详情响应")
public class DeviceVO {

    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "型号ID")
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "所属农场ID")
    private Long farmId;

    @Schema(description = "设备状态")
    private String status;

    @Schema(description = "状态中文描述")
    private String statusText;

    @Schema(description = "固件版本")
    private String firmwareVersion;

    @Schema(description = "SIM卡号")
    private String simCard;

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activationTime;

    @Schema(description = "最后上线时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "配置参数")
    private String configParams;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
