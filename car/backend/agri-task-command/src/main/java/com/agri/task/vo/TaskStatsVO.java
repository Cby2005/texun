package com.agri.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务统计响应VO
 */
@Data
@Schema(description = "任务统计信息")
public class TaskStatsVO {

    @Schema(description = "任务总数")
    private Long totalCount;

    @Schema(description = "待执行数量")
    private Long pendingCount;

    @Schema(description = "已分配数量")
    private Long assignedCount;

    @Schema(description = "执行中数量")
    private Long executingCount;

    @Schema(description = "暂停数量")
    private Long pausedCount;

    @Schema(description = "已完成数量")
    private Long completedCount;

    @Schema(description = "已取消数量")
    private Long cancelledCount;

    @Schema(description = "失败数量")
    private Long failedCount;

    @Schema(description = "任务完成率(%)")
    private Double completionRate;
}
