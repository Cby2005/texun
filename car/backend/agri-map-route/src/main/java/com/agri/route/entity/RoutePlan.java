package com.agri.route.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 作业路线实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("route_plan")
public class RoutePlan extends BaseEntity {

    private String routeName;
    private Long plotId;
    private Long farmId;
    private String routeType;
    private String algorithm;
    private String waypoints;
    private Double totalLength;
    private Integer estimatedDuration;
    private Double coverageRate;
    private Double workWidth;
    private Double turnRadius;
    private Double overlapRate;
    private Integer avoidObstacles;
    private String status;
}
