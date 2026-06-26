package com.agri.task.service.impl;

import com.agri.common.constant.Constants;
import com.agri.common.dto.PageQuery;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.common.vo.PageResult;
import com.agri.task.command.CommandInvoker;
import com.agri.task.dto.CommandSendDTO;
import com.agri.task.dto.TaskCreateDTO;
import com.agri.task.entity.TaskCommand;
import com.agri.task.entity.TaskOrder;
import com.agri.task.mapper.TaskCommandMapper;
import com.agri.task.mapper.TaskOrderMapper;
import com.agri.task.service.TaskService;
import com.agri.task.vo.TaskStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 作业任务服务实现
 *
 * Redis Key 设计：
 *   agri:task:lock:{taskId}    ->  "1"    过期时间: 30秒（任务操作分布式锁）
 *
 * 索引设计：
 *   task_order.task_code    ->  UNIQUE（任务编码查询）
 *   task_order.status       ->  INDEX（状态筛选）
 *   task_order.vehicle_id   ->  INDEX（设备任务查询）
 *   task_order.farm_id      ->  INDEX（农场任务查询）
 *   task_order.plot_id      ->  INDEX（地块任务查询）
 *   task_order.task_type    ->  INDEX（任务类型筛选）
 *   task_command.task_id    ->  INDEX（任务指令查询）
 *   task_command.command_code -> UNIQUE（指令编码查询）
 *   task_command.status     ->  INDEX（指令状态筛选）
 *
 * Kafka Topic 说明：
 *   vehicle.command.ack -> 指令确认回执（Producer: CommandInvoker, Consumer: CommandAckConsumer）
 *   task.event          -> 任务事件（创建/完成/取消）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskOrderMapper taskOrderMapper;
    private final TaskCommandMapper commandMapper;
    private final CommandInvoker commandInvoker;
    private final RedisTemplate<String, Object> redisTemplate;

    // ========== 任务管理 ==========

    @Override
    @Transactional
    public void createTask(TaskCreateDTO dto) {
        TaskOrder task = new TaskOrder();
        task.setTaskCode("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        task.setTaskName(dto.getTaskName());
        task.setTaskType(dto.getTaskType());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : 3);
        task.setFarmId(dto.getFarmId());
        task.setPlotId(dto.getPlotId());
        task.setVehicleId(dto.getVehicleId());
        task.setRouteId(dto.getRouteId());
        task.setDescription(dto.getDescription());
        task.setScheduledStart(dto.getScheduledStart());
        task.setScheduledEnd(dto.getScheduledEnd());
        task.setParameters(dto.getParameters());
        task.setStatus(Constants.TASK_PENDING);
        task.setProgress(0.0);
        taskOrderMapper.insert(task);

        log.info("任务创建成功: code={}, name={}, type={}", task.getTaskCode(), dto.getTaskName(), dto.getTaskType());
    }

    @Override
    public PageResult<TaskOrder> listTasks(PageQuery query, String taskType, String status,
                                             Long vehicleId, Long plotId) {
        LambdaQueryWrapper<TaskOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(taskType)) wrapper.eq(TaskOrder::getTaskType, taskType);
        if (StringUtils.hasText(status)) wrapper.eq(TaskOrder::getStatus, status);
        if (vehicleId != null) wrapper.eq(TaskOrder::getVehicleId, vehicleId);
        if (plotId != null) wrapper.eq(TaskOrder::getPlotId, plotId);
        wrapper.orderByDesc(TaskOrder::getPriority, TaskOrder::getCreateTime);
        Page<TaskOrder> page = taskOrderMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), query.getPageNum(), query.getPageSize(), page.getRecords());
    }

    @Override
    public TaskOrder getTask(Long id) {
        TaskOrder task = taskOrderMapper.selectById(id);
        if (task == null) throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        return task;
    }

    @Override
    public void updateTask(TaskOrder task) {
        TaskOrder existing = getTask(task.getId());
        if (Constants.TASK_COMPLETED.equals(existing.getStatus()) ||
                Constants.TASK_CANCELLED.equals(existing.getStatus())) {
            throw new BusinessException("已完成或已取消的任务不允许修改");
        }
        taskOrderMapper.updateById(task);
    }

    @Override
    @Transactional
    public void cancelTask(Long id) {
        TaskOrder task = getTask(id);
        if (Constants.TASK_COMPLETED.equals(task.getStatus())) {
            throw new BusinessException("已完成的任务无法取消");
        }
        task.setStatus(Constants.TASK_CANCELLED);
        task.setActualEnd(LocalDateTime.now());
        taskOrderMapper.updateById(task);
        log.info("任务取消: code={}", task.getTaskCode());
    }

    @Override
    @Transactional
    public void assignVehicle(Long taskId, Long vehicleId) {
        TaskOrder task = getTask(taskId);
        task.setVehicleId(vehicleId);
        task.setStatus(Constants.TASK_ASSIGNED);
        taskOrderMapper.updateById(task);
        log.info("任务分配设备: taskCode={}, vehicleId={}", task.getTaskCode(), vehicleId);
    }

    @Override
    public void assignRoute(Long taskId, Long routeId) {
        TaskOrder task = getTask(taskId);
        task.setRouteId(routeId);
        taskOrderMapper.updateById(task);
        log.info("任务绑定路线: taskCode={}, routeId={}", task.getTaskCode(), routeId);
    }

    @Override
    @Transactional
    public void pauseTask(Long id) {
        TaskOrder task = getTask(id);
        if (!Constants.TASK_EXECUTING.equals(task.getStatus())) {
            throw new BusinessException("只能暂停执行中的任务");
        }
        task.setStatus(Constants.TASK_PAUSED);
        taskOrderMapper.updateById(task);
        log.info("任务暂停: code={}", task.getTaskCode());
    }

    @Override
    @Transactional
    public void resumeTask(Long id) {
        TaskOrder task = getTask(id);
        if (!Constants.TASK_PAUSED.equals(task.getStatus())) {
            throw new BusinessException("只能恢复暂停的任务");
        }
        task.setStatus(Constants.TASK_EXECUTING);
        taskOrderMapper.updateById(task);
        log.info("任务恢复: code={}", task.getTaskCode());
    }

    // ========== 指令管理 ==========

    @Override
    @Transactional
    public void sendCommand(CommandSendDTO dto) {
        TaskOrder task = getTask(dto.getTaskId());

        // 分布式锁防重复发送（Key: agri:task:lock:{taskId}，过期30秒）
        String lockKey = Constants.REDIS_TASK_LOCK + dto.getTaskId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException("任务正在处理中，请稍后重试");
        }

        try {
            TaskCommand command = new TaskCommand();
            command.setCommandCode("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            command.setTaskId(dto.getTaskId());
            command.setVehicleId(task.getVehicleId());
            command.setCommandType(dto.getCommandType());
            command.setCommandData(dto.getCommandData());
            command.setStatus(Constants.CMD_CREATED);
            command.setRetryCount(0);
            command.setMaxRetries(3);
            command.setTimeoutSeconds(30);
            commandMapper.insert(command);

            // 通过命令模式执行
            commandInvoker.executeCommand(command);

            // 更新任务状态
            if (Constants.TASK_PENDING.equals(task.getStatus()) || Constants.TASK_ASSIGNED.equals(task.getStatus())) {
                if ("START".equals(dto.getCommandType())) {
                    task.setStatus(Constants.TASK_EXECUTING);
                    task.setActualStart(LocalDateTime.now());
                    taskOrderMapper.updateById(task);
                }
            }

            log.info("指令发送成功: commandCode={}, taskCode={}, type={}",
                    command.getCommandCode(), task.getTaskCode(), dto.getCommandType());
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public PageResult<TaskCommand> listCommands(PageQuery query, Long taskId, String status) {
        LambdaQueryWrapper<TaskCommand> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) wrapper.eq(TaskCommand::getTaskId, taskId);
        if (StringUtils.hasText(status)) wrapper.eq(TaskCommand::getStatus, status);
        wrapper.orderByDesc(TaskCommand::getCreateTime);
        Page<TaskCommand> page = commandMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), query.getPageNum(), query.getPageSize(), page.getRecords());
    }

    @Override
    public TaskCommand getCommand(Long id) {
        TaskCommand command = commandMapper.selectById(id);
        if (command == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        return command;
    }

    @Override
    @Transactional
    public void retryCommand(Long id) {
        TaskCommand command = getCommand(id);
        if (command.getRetryCount() >= command.getMaxRetries()) {
            throw new BusinessException("已达最大重试次数(3次)");
        }
        command.setRetryCount(command.getRetryCount() + 1);
        command.setStatus(Constants.CMD_CREATED);
        commandMapper.updateById(command);
        commandInvoker.executeCommand(command);
        log.info("指令重试: commandCode={}, retryCount={}", command.getCommandCode(), command.getRetryCount());
    }

    @Override
    @Transactional
    public void cancelCommand(Long id) {
        TaskCommand command = getCommand(id);
        commandInvoker.undoCommand(command);
        log.info("指令取消: commandCode={}", command.getCommandCode());
    }

    @Override
    public List<TaskCommand> getCommandsByTask(Long taskId) {
        return commandMapper.selectList(new LambdaQueryWrapper<TaskCommand>()
                .eq(TaskCommand::getTaskId, taskId)
                .orderByDesc(TaskCommand::getCreateTime));
    }

    // ========== 统计 ==========

    @Override
    public TaskStatsVO getTaskStats() {
        TaskStatsVO stats = new TaskStatsVO();

        Long total = taskOrderMapper.selectCount(new LambdaQueryWrapper<>());
        stats.setTotalCount(total);

        stats.setPendingCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_PENDING)));
        stats.setAssignedCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_ASSIGNED)));
        stats.setExecutingCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_EXECUTING)));
        stats.setPausedCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_PAUSED)));
        stats.setCompletedCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_COMPLETED)));
        stats.setCancelledCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_CANCELLED)));
        stats.setFailedCount(taskOrderMapper.selectCount(
                new LambdaQueryWrapper<TaskOrder>().eq(TaskOrder::getStatus, Constants.TASK_FAILED)));

        if (total > 0) {
            stats.setCompletionRate(Math.round(stats.getCompletedCount() * 10000.0 / total) / 100.0);
        } else {
            stats.setCompletionRate(0.0);
        }

        return stats;
    }
}
