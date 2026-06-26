package com.agri.telemetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 遥测数据实体
 */
@Data
@TableName("telemetry_data")
public class TelemetryData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;
    private Double direction;
    private Double batteryLevel;
    private Double batteryVoltage;
    private Double cpuUsage;
    private Double memoryUsage;
    private Integer networkSignal;
    private String sensorStatus;
    private Double taskProgress;
    private String extraData;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
