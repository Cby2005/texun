package com.agri.common.pattern.template;

import lombok.extern.slf4j.Slf4j;

/**
 * 模板方法模式：统一任务执行流程
 * 定义任务执行的标准流程：校验 → 准备 → 执行 → 记录 → 清理
 * 子类只需实现具体步骤
 */
@Slf4j
public abstract class AbstractTaskExecutor {

    /**
     * 模板方法：定义任务执行流程
     */
    public final TaskExecutionResult execute(TaskExecutionRequest request) {
        log.info("开始执行任务: type={}, taskId={}", request.getTaskType(), request.getTaskId());

        // 1. 参数校验
        if (!validate(request)) {
            return TaskExecutionResult.fail("参数校验失败");
        }

        // 2. 准备资源
        prepare(request);

        // 3. 执行任务
        Object result;
        try {
            result = doExecute(request);
        } catch (Exception e) {
            log.error("任务执行异常: {}", e.getMessage(), e);
            afterFailure(request, e);
            return TaskExecutionResult.fail("执行异常: " + e.getMessage());
        }

        // 4. 记录结果
        record(request, result);

        // 5. 清理资源
        cleanup(request);

        log.info("任务执行完成: taskId={}", request.getTaskId());
        return TaskExecutionResult.success(result);
    }

    /** 参数校验 */
    protected boolean validate(TaskExecutionRequest request) {
        return request != null && request.getTaskId() != null;
    }

    /** 准备资源 */
    protected void prepare(TaskExecutionRequest request) {
        log.debug("准备资源: taskId={}", request.getTaskId());
    }

    /** 执行任务（子类必须实现） */
    protected abstract Object doExecute(TaskExecutionRequest request);

    /** 记录结果 */
    protected void record(TaskExecutionRequest request, Object result) {
        log.debug("记录结果: taskId={}, result={}", request.getTaskId(), result);
    }

    /** 清理资源 */
    protected void cleanup(TaskExecutionRequest request) {
        log.debug("清理资源: taskId={}", request.getTaskId());
    }

    /** 执行失败回调 */
    protected void afterFailure(TaskExecutionRequest request, Exception e) {
        log.warn("任务失败回调: taskId={}", request.getTaskId());
    }
}
