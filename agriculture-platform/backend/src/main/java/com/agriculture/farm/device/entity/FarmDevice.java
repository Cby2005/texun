package com.agriculture.farm.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("farm_device")
public class FarmDevice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceCode;
    private String deviceName;
    /** 设备类型: IRRIGATION/LIGHT/FAN/HEATER/SENSOR等 */
    private String deviceType;
    /** STANDBY/RUNNING/MAINTENANCE/FAULT */
    private String state;
    private String area;
    private Integer online;
    private String workMode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
