package com.agriculture.drone.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("drone_inspection_task")
public class DroneInspectionTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private String taskName;
    private Long droneId;
    private Long routeId;
    private Long greenhouseId;
    private String taskType;
    private String taskStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String result;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
