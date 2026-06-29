package com.agriculture.drone.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drone_device")
public class DroneDevice {
    @TableId(type = IdType.AUTO)
    private Long id;

    // ==================== 设备档案 ====================
    /** 无人机编号 */
    private String droneCode;
    /** 无人机名称 */
    private String droneName;
    /** 无人机型号 */
    private String model;
    /** 设备厂商 */
    private String manufacturer;
    /** 序列号 */
    private String serialNumber;
    /** 所属基地ID */
    private Long baseId;
    /** 默认巡检区域 */
    private String defaultArea;
    /** 最大续航时间(分钟) */
    private Integer maxEndurance;
    /** 最大飞行高度(米) */
    private BigDecimal maxFlightHeight;
    /** 是否搭载摄像头 0否 1是 */
    private Integer hasCamera;
    /** 摄像头类型 */
    private String cameraType;
    /** 是否支持病害识别 0否 1是 */
    private Integer supportDiseaseDetect;
    /** 设备状态: IDLE/INSPECTING/CHARGING/MAINTENANCE/OFFLINE */
    private String deviceStatus;
    /** 所属温室ID */
    private Long greenhouseId;
    /** 最大载重(kg) */
    private BigDecimal maxLoad;
    /** 备注 */
    private String remark;

    // ==================== 运行状态（由巡检任务自动维护） ====================
    /** 电量(%) */
    private BigDecimal batteryLevel;
    /** 当前经度 */
    private BigDecimal longitude;
    /** 当前纬度 */
    private BigDecimal latitude;
    /** 当前高度(米) */
    private BigDecimal altitude;
    /** 摄像头运行状态 */
    private String cameraStatus;
    /** 当前巡检任务ID */
    private Long currentTaskId;
    /** 最近巡检时间 */
    private LocalDateTime lastInspectionTime;
    /** 累计巡检次数 */
    private Integer totalInspectionCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
