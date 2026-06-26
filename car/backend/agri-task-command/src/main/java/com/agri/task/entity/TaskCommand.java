package com.agri.task.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 指令实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_command")
public class TaskCommand extends BaseEntity {

    private String commandCode;
    private Long taskId;
    private Long vehicleId;
    private String commandType;
    private String commandData;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime;
    private Integer timeoutSeconds;
    private String resultData;
    private String errorMessage;
}
