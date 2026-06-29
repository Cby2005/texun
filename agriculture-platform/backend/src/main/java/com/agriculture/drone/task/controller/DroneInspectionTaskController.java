package com.agriculture.drone.task.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.drone.task.dto.TaskCreateRequest;
import com.agriculture.drone.task.entity.DroneInspectionTask;
import com.agriculture.drone.task.service.impl.DroneInspectionTaskServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/drone/task")
@RequiredArgsConstructor
@Tag(name = "巡检任务管理")
public class DroneInspectionTaskController {

    private final DroneInspectionTaskServiceImpl taskService;

    @PostMapping("/create")
    public Result<DroneInspectionTask> create(@RequestBody TaskCreateRequest req) {
        return Result.ok(taskService.createTask(req));
    }

    @PostMapping("/start/{id}")
    public Result<Void> start(@PathVariable Long id) {
        taskService.startTask(id);
        return Result.ok();
    }

    @PostMapping("/finish/{id}")
    public Result<Void> finish(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String result = body != null ? body.getOrDefault("result", "巡检完成") : "巡检完成";
        taskService.finishTask(id, result);
        return Result.ok();
    }

    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        taskService.cancelTask(id);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<PageResult<DroneInspectionTask>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) Long droneId) {
        LambdaQueryWrapper<DroneInspectionTask> qw = new LambdaQueryWrapper<>();
        if (taskType != null && !taskType.isBlank()) qw.eq(DroneInspectionTask::getTaskType, taskType);
        if (taskStatus != null && !taskStatus.isBlank()) qw.eq(DroneInspectionTask::getTaskStatus, taskStatus);
        if (droneId != null) qw.eq(DroneInspectionTask::getDroneId, droneId);
        qw.orderByDesc(DroneInspectionTask::getCreateTime);
        Page<DroneInspectionTask> page = taskService.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DroneInspectionTask> getById(@PathVariable Long id) { return Result.ok(taskService.getById(id)); }
}
