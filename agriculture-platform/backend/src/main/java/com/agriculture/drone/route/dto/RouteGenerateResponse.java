package com.agriculture.drone.route.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RouteGenerateResponse {
    private Long routeId;
    private String routeCode;
    private String routeName;
    private String routeType;
    private String algorithmType;
    private BigDecimal totalDistance;
    private BigDecimal estimatedTime;   // 秒
    private String waypoints;           // JSON
}
