package com.agriculture.drone.point.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 无人机巡检点 - 支持经纬度 + Three.js 场景坐标
 */
@Data
@TableName("drone_inspection_point")
public class DroneInspectionPoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pointCode;
    private Long baseId;
    private String pointName;
    private Long greenhouseId;
    private Long areaId;
    private String areaName;
    /** 经度 */
    private BigDecimal longitude;
    /** 纬度 */
    private BigDecimal latitude;
    /** 飞行高度(米) */
    private BigDecimal altitude;
    /** Three.js 场景X坐标 */
    private BigDecimal sceneX;
    /** Three.js 场景Y坐标（飞行高度） */
    private BigDecimal sceneY;
    /** Three.js 场景Z坐标 */
    private BigDecimal sceneZ;
    /** 所属种植行(旧) */
    private Integer rowIndex;
    /** 行内巡检点序号(旧) */
    private Integer pointIndex;
    /** 所在种植行号 */
    private Integer rowNo;
    /** 点位位置: 行首/行中/行尾/设备旁/异常复查点 */
    private String pointPosition;
    /** 风险等级: 普通/重点/高风险 */
    private String riskLevel;
    /** 是否启用 */
    private Integer enabled;
    /** 排序号 */
    private Integer sortOrder;
    /** 巡检点类型: 环境采集点/病害识别点/生长监测点/设备检查点/异常复查点/START/NORMAL/ABNORMAL/END */
    private String pointType;
    /** NORMAL/WARNING/ABNORMAL */
    private String status;
    /** AUTO/MANUAL */
    private String sourceType;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
