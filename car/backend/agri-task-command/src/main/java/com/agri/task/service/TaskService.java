package com.agri.task.service;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.task.dto.CommandSendDTO;
import com.agri.task.dto.TaskCreateDTO;
import com.agri.task.entity.TaskCommand;
import com.agri.task.entity.TaskOrder;
import com.agri.task.vo.TaskStatsVO;

import java.util.List;

/**
 * 作业任务服务接口
 */
public interface TaskService {

    // ========== 任务管理 ==========
    void createTask(TaskCreateDTO dto);
    PageResult<TaskOrder> listTasks(PageQuery query, String taskType, String status, Long vehicleId, Long plotId);
    TaskOrder getTask(Long id);
    void updateTask(TaskOrder task);
    void cancelTask(Long id);
    void assignVehicle(Long taskId, Long vehicleId);
    void assignRoute(Long taskId, Long routeId);
    void pauseTask(Long id);
    void resumeTask(Long id);

    // ========== 指令管理 ==========
    void sendCommand(CommandSendDTO dto);
    PageResult<TaskCommand> listCommands(PageQuery query, Long taskId, String status);
    TaskCommand getCommand(Long id);
    void retryCommand(Long id);
    void cancelCommand(Long id);
    List<TaskCommand> getCommandsByTask(Long taskId);

    // ========== 统计 ==========
    TaskStatsVO getTaskStats();
}
