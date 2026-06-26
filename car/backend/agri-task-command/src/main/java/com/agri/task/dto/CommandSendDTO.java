package com.agri.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 指令发送请求DTO
 */
@Data
@Schema(description = "指令发送请求")
public class CommandSendDTO {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID")
    private Long taskId;

    @NotBlank(message = "指令类型不能为空")
    @Schema(description = "指令类型", example = "START", allowableValues = {"START", "STOP", "PAUSE", "RESUME", "EMERGENCY_STOP"})
    private String commandType;

    @Schema(description = "指令参数JSON")
    private String commandData;
}
