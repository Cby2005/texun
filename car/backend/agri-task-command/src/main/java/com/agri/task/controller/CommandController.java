package com.agri.task.controller;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.task.dto.CommandSendDTO;
import com.agri.task.entity.TaskCommand;
import com.agri.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 指令管理控制器
 */
@Tag(name = "指令管理", description = "指令发送/查询/重试/取消，支持5种指令类型：START/STOP/PAUSE/RESUME/EMERGENCY_STOP")
@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CommandController {

    private final TaskService taskService;

    @Operation(summary = "发送指令", description = "通过命令模式发送指令到Kafka，支持分布式锁防重复发送")
    @PostMapping("/send")
    public R<Void> sendCommand(@Valid @RequestBody CommandSendDTO dto) {
        taskService.sendCommand(dto);
        return R.ok();
    }

    @Operation(summary = "分页查询指令")
    @GetMapping("/list")
    public R<PageResult<TaskCommand>> listCommands(
            @Valid PageQuery query,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String status) {
        return R.ok(taskService.listCommands(query, taskId, status));
    }

    @Operation(summary = "获取指令详情")
    @GetMapping("/{id}")
    public R<TaskCommand> getCommand(@PathVariable Long id) {
        return R.ok(taskService.getCommand(id));
    }

    @Operation(summary = "重试指令", description = "最多重试3次")
    @PostMapping("/{id}/retry")
    public R<Void> retryCommand(@PathVariable Long id) {
        taskService.retryCommand(id);
        return R.ok();
    }

    @Operation(summary = "取消指令")
    @PostMapping("/{id}/cancel")
    public R<Void> cancelCommand(@PathVariable Long id) {
        taskService.cancelCommand(id);
        return R.ok();
    }

    @Operation(summary = "获取任务的所有指令")
    @GetMapping("/task/{taskId}")
    public R<List<TaskCommand>> getCommandsByTask(@PathVariable Long taskId) {
        return R.ok(taskService.getCommandsByTask(taskId));
    }
}
