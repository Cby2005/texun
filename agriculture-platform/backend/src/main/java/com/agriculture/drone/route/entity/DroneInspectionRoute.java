package com.agriculture.drone.route.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("drone_inspection_route")
public class DroneInspectionRoute {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String routeCode;
    private String routeName;
    private Long baseId;
    private Long greenhouseId;
    private String routeMode;
    private String scope;
    private String routeStrategy;
    private Integer pointCount;
    private Integer estimatedDuration;
    private String status;
    private Integer isCommon;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
