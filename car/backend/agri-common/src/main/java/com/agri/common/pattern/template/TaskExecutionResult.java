package com.agri.common.pattern.template;

import lombok.Builder;
import lombok.Data;

/**
 * 模板方法模式：任务执行结果
 */
@Data
@Builder
public class TaskExecutionResult {
    private boolean success;
    private String message;
    private Object data;

    public static TaskExecutionResult success(Object data) {
        return TaskExecutionResult.builder().success(true).message("执行成功").data(data).build();
    }

    public static TaskExecutionResult fail(String message) {
        return TaskExecutionResult.builder().success(false).message(message).build();
    }
}
