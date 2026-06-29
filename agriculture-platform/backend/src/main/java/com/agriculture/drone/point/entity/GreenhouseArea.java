package com.agriculture.drone.point.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 温室分区实体 - 用于巡检点生成
 */
@Data
@TableName("greenhouse_area")
public class GreenhouseArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long greenhouseId;
    private String areaName;
    /** 种植行数 */
    private Integer rowCount;
    /** 每行株数 */
    private Integer plantCountPerRow;
    /** Three.js 场景中心X */
    private BigDecimal sceneX;
    /** Three.js 场景中心Z */
    private BigDecimal sceneZ;
    /** 分区宽度 */
    private BigDecimal sceneWidth;
    /** 分区深度 */
    private BigDecimal sceneDepth;
    /** NORMAL/WARNING/ABNORMAL */
    private String status;
    /** LOW/MEDIUM/HIGH */
    private String diseaseRisk;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
