package com.agri.task.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_order")
public class TaskOrder extends BaseEntity {

    private String taskCode;
    private String taskName;
    private String taskType;
    private Integer priority;
    private Long farmId;
    private Long plotId;
    private Long vehicleId;
    private Long routeId;
    private String status;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledEnd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEnd;
    private Double progress;
    private String resultSummary;
    private String parameters;
}
