package com.agriculture.drone.route.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteGenerateRequest {
    private Long greenhouseId;
    private String routeName;
    private String routeType;
    private String algorithmType;
    private List<Long> pointIds;
}
