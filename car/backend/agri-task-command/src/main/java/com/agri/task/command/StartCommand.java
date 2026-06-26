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
 * 启动作业命令
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TaskCommandMapper commandMapper;

    @Override
    public String getType() {
        return "START";
    }

    @Override
    public void execute(TaskCommand command) {
        log.info("执行启动命令: commandCode={}, vehicleId={}", command.getCommandCode(), command.getVehicleId());

        // 更新指令状态为已发送
        command.setStatus(Constants.CMD_SENT);
        command.setSentTime(LocalDateTime.now());
        commandMapper.updateById(command);

        // 发送到Kafka
        String message = String.format(
                "{\"commandCode\":\"%s\",\"taskId\":%d,\"vehicleId\":%d,\"commandType\":\"START\",\"commandData\":%s,\"timestamp\":%d}",
                command.getCommandCode(), command.getTaskId(), command.getVehicleId(),
                command.getCommandData(), System.currentTimeMillis()
        );
        kafkaTemplate.send(Constants.TOPIC_COMMAND_ACK, message);
    }

    @Override
    public void undo(TaskCommand command) {
        log.info("撤销启动命令: commandCode={}", command.getCommandCode());
        command.setStatus(Constants.CMD_CANCELLED);
        commandMapper.updateById(command);
    }

    @Override
    public boolean validate(TaskCommand command) {
        return command.getVehicleId() != null && command.getTaskId() != null;
    }
}
