package com.agri.task.command;

import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.task.entity.TaskCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令调用者 - 管理和执行命令
 */
@Slf4j
@Component
public class CommandInvoker {

    private final Map<String, Command> commandMap = new HashMap<>();
    private final List<Command> commands;

    public CommandInvoker(List<Command> commands) {
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        for (Command command : commands) {
            commandMap.put(command.getType(), command);
            log.info("注册命令: {}", command.getType());
        }
    }

    /**
     * 执行命令
     */
    public void executeCommand(TaskCommand taskCommand) {
        Command command = commandMap.get(taskCommand.getCommandType());
        if (command == null) {
            throw new BusinessException(ResultCode.COMMAND_SEND_FAILED.getCode(),
                    "不支持的命令类型: " + taskCommand.getCommandType());
        }

        if (!command.validate(taskCommand)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "命令参数校验失败: " + taskCommand.getCommandType());
        }

        command.execute(taskCommand);
    }

    /**
     * 撤销命令
     */
    public void undoCommand(TaskCommand taskCommand) {
        Command command = commandMap.get(taskCommand.getCommandType());
        if (command != null) {
            command.undo(taskCommand);
        }
    }
}
