package com.agri.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 任务创建请求DTO
 */
@Data
@Schema(description = "任务创建请求")
public class TaskCreateDTO {

    @NotBlank(message = "任务名称不能为空")
    @Schema(description = "任务名称", example = "A-1号地块喷洒作业")
    private String taskName;

    @NotBlank(message = "任务类型不能为空")
    @Schema(description = "任务类型", example = "SPRAYING")
    private String taskType;

    @Schema(description = "优先级(1-5)", example = "3")
    private Integer priority;

    @NotNull(message = "农场ID不能为空")
    @Schema(description = "农场ID")
    private Long farmId;

    @NotNull(message = "地块ID不能为空")
    @Schema(description = "地块ID")
    private Long plotId;

    @Schema(description = "设备ID")
    private Long vehicleId;

    @Schema(description = "路线ID")
    private Long routeId;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledStart;

    @Schema(description = "计划结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledEnd;

    @Schema(description = "任务参数JSON")
    private String parameters;
}
