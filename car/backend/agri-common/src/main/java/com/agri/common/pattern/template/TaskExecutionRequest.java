package com.agri.common.pattern.template;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 模板方法模式：任务执行请求
 */
@Data
@Builder
public class TaskExecutionRequest {
    private Long taskId;
    private String taskType;
    private Long vehicleId;
    private Long plotId;
    private Map<String, Object> params;
}
