package com.agri.task.command;

import com.agri.common.constant.Constants;
import com.agri.task.entity.TaskCommand;
import com.agri.task.mapper.TaskCommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 暂停作业命令
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PauseCommand implements Command {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TaskCommandMapper commandMapper;

    @Override
    public String getType() {
        return "PAUSE";
    }

    @Override
    public void execute(TaskCommand command) {
        log.info("执行暂停命令: commandCode={}, vehicleId={}", command.getCommandCode(), command.getVehicleId());
        command.setStatus(Constants.CMD_SENT);
        command.setSentTime(LocalDateTime.now());
        commandMapper.updateById(command);

        String message = String.format(
                "{\"commandCode\":\"%s\",\"vehicleId\":%d,\"commandType\":\"PAUSE\",\"timestamp\":%d}",
                command.getCommandCode(), command.getVehicleId(), System.currentTimeMillis()
        );
        kafkaTemplate.send(Constants.TOPIC_COMMAND_ACK, message);
    }

    @Override
    public void undo(TaskCommand command) {
        log.info("撤销暂停命令: commandCode={}", command.getCommandCode());
        command.setStatus(Constants.CMD_CANCELLED);
        commandMapper.updateById(command);
    }

    @Override
    public boolean validate(TaskCommand command) {
        return command.getVehicleId() != null && command.getTaskId() != null;
    }
}
