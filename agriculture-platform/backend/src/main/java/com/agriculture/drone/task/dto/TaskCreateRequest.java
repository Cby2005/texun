package com.agriculture.drone.task.dto;

import lombok.Data;

@Data
public class TaskCreateRequest {
    private String taskName;
    private Long droneId;
    private Long routeId;
    private Long greenhouseId;
    private String taskType;
    private String remark;
}
