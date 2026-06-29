package com.agriculture.drone.route.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drone_route_plan")
public class DroneRoutePlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String routeCode;
    private String routeName;
    private Long greenhouseId;
    private String routeType;
    /** 起点坐标JSON: {"longitude":113.8,"latitude":34.1,"altitude":1.5} */
    private String startPoint;
    /** 终点坐标JSON */
    private String endPoint;
    /** 航点JSON: [{"longitude":113.8,"latitude":34.1,"altitude":1.5,"pointName":"A","orderIndex":0}] */
    private String waypoints;
    private String algorithmType;
    private BigDecimal totalDistance;
    private BigDecimal estimatedTime;   // 预计耗时(秒)
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
