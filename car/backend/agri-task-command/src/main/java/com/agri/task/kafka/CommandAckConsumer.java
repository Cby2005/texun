package com.agri.task.kafka;

import com.agri.common.constant.Constants;
import com.agri.task.entity.TaskCommand;
import com.agri.task.entity.TaskOrder;
import com.agri.task.mapper.TaskCommandMapper;
import com.agri.task.mapper.TaskOrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 指令ACK消费者
 * 消费无人车模拟器返回的ACK消息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandAckConsumer {

    private final TaskCommandMapper commandMapper;
    private final TaskOrderMapper taskOrderMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = Constants.TOPIC_COMMAND_ACK, groupId = "agri-task-ack")
    public void consumeAck(ConsumerRecord<String, String> record) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(record.value(), Map.class);

            String commandCode = (String) data.get("commandCode");
            String ackStatus = (String) data.get("status");

            if (commandCode == null) return;

            // 更新指令状态
            TaskCommand command = commandMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TaskCommand>()
                            .eq(TaskCommand::getCommandCode, commandCode));
            if (command == null) {
                log.warn("未找到指令: {}", commandCode);
                return;
            }

            switch (ackStatus != null ? ackStatus : "RECEIVED") {
                case "RECEIVED":
                    command.setStatus(Constants.CMD_RECEIVED);
                    command.setReceivedTime(LocalDateTime.now());
                    break;
                case "EXECUTING":
                    command.setStatus(Constants.CMD_EXECUTING);
                    command.setExecutedTime(LocalDateTime.now());
                    break;
                case "SUCCESS":
                    command.setStatus(Constants.CMD_SUCCESS);
                    command.setCompletedTime(LocalDateTime.now());
                    // 更新任务进度
                    updateTaskProgress(command.getTaskId(), data);
                    break;
                case "FAILED":
                    command.setStatus(Constants.CMD_FAILED);
                    command.setCompletedTime(LocalDateTime.now());
                    command.setErrorMessage((String) data.get("errorMessage"));
                    break;
                default:
                    log.warn("未知的ACK状态: {}", ackStatus);
            }

            commandMapper.updateById(command);
            log.info("指令状态更新: {} -> {}", commandCode, command.getStatus());

        } catch (Exception e) {
            log.error("处理ACK消息失败: {}", record.value(), e);
        }
    }

    private void updateTaskProgress(Long taskId, Map<String, Object> data) {
        if (taskId == null) return;
        TaskOrder task = taskOrderMapper.selectById(taskId);
        if (task != null && data.containsKey("progress")) {
            Double progress = Double.parseDouble(data.get("progress").toString());
            task.setProgress(progress);
            if (progress >= 100) {
                task.setStatus(Constants.TASK_COMPLETED);
                task.setActualEnd(LocalDateTime.now());
            }
            taskOrderMapper.updateById(task);
        }
    }
}
