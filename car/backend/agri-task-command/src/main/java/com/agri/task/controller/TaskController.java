package com.agri.task.controller;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.task.dto.TaskCreateDTO;
import com.agri.task.entity.TaskOrder;
import com.agri.task.service.TaskService;
import com.agri.task.vo.TaskStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 作业任务控制器
 */
@Tag(name = "作业任务管理", description = "任务CRUD、设备分配、路线绑定、状态控制、统计")
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "创建任务")
    @PostMapping
    public R<Void> createTask(@Valid @RequestBody TaskCreateDTO dto) {
        taskService.createTask(dto);
        return R.ok();
    }

    @Operation(summary = "分页查询任务")
    @GetMapping("/list")
    public R<PageResult<TaskOrder>> listTasks(
            @Valid PageQuery query,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Long plotId) {
        return R.ok(taskService.listTasks(query, taskType, status, vehicleId, plotId));
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    public R<TaskOrder> getTask(@PathVariable Long id) {
        return R.ok(taskService.getTask(id));
    }

    @Operation(summary = "更新任务")
    @PutMapping
    public R<Void> updateTask(@RequestBody TaskOrder task) {
        taskService.updateTask(task);
        return R.ok();
    }

    @Operation(summary = "取消任务")
    @PostMapping("/{id}/cancel")
    public R<Void> cancelTask(@PathVariable Long id) {
        taskService.cancelTask(id);
        return R.ok();
    }

    @Operation(summary = "分配设备")
    @PostMapping("/{taskId}/assign/{vehicleId}")
    public R<Void> assignVehicle(@PathVariable Long taskId, @PathVariable Long vehicleId) {
        taskService.assignVehicle(taskId, vehicleId);
        return R.ok();
    }

    @Operation(summary = "绑定路线")
    @PostMapping("/{taskId}/route/{routeId}")
    public R<Void> assignRoute(@PathVariable Long taskId, @PathVariable Long routeId) {
        taskService.assignRoute(taskId, routeId);
        return R.ok();
    }

    @Operation(summary = "暂停任务")
    @PostMapping("/{id}/pause")
    public R<Void> pauseTask(@PathVariable Long id) {
        taskService.pauseTask(id);
        return R.ok();
    }

    @Operation(summary = "恢复任务")
    @PostMapping("/{id}/resume")
    public R<Void> resumeTask(@PathVariable Long id) {
        taskService.resumeTask(id);
        return R.ok();
    }

    @Operation(summary = "获取任务统计")
    @GetMapping("/stats")
    public R<TaskStatsVO> getTaskStats() {
        return R.ok(taskService.getTaskStats());
    }
}
