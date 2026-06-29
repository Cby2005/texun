package com.agriculture.drone.route.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drone_inspection_route_point")
public class DroneInspectionRoutePoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long routeId;
    private Long plotId;
    private Long inspectionPointId;
    private Long plantingRecordId;
    private Integer sequenceNo;
    private String areaCode;
    private String areaName;
    private Integer rowNo;
    private Integer colNo;
    private BigDecimal coordinateX;
    private BigDecimal coordinateY;
    private BigDecimal coordinateZ;
    private String pointType;
    private String actionType;
    private Integer staySeconds;
    private LocalDateTime createTime;
}
