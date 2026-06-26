package com.agri.task.command;

import com.agri.task.entity.TaskCommand;

/**
 * 命令接口 - 命令模式
 */
public interface Command {

    /**
     * 获取命令类型
     */
    String getType();

    /**
     * 执行命令
     */
    void execute(TaskCommand command);

    /**
     * 撤销命令
     */
    void undo(TaskCommand command);

    /**
     * 验证命令参数
     */
    boolean validate(TaskCommand command);
}
