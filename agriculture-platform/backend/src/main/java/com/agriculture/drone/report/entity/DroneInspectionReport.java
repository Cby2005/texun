package com.agriculture.drone.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("drone_inspection_report")
public class DroneInspectionReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String reportCode;
    private String taskName;
    private String droneName;
    private String routeName;
    private String greenhouseName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalImages;
    private Integer abnormalImages;
    private String diseaseTypes;
    private String suggestion;
    private LocalDateTime createTime;
}
