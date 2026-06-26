package com.agri.common.pattern.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 责任链模式：低级告警处理器（记录日志）
 */
@Slf4j
@Component
public class LowLevelAlarmHandler extends AbstractAlarmHandler {

    @Override
    public String name() {
        return "LowLevelAlarmHandler";
    }

    @Override
    public boolean support(String level) {
        return "LOW".equals(level) || "INFO".equals(level);
    }

    @Override
    protected AlarmResult doHandle(AlarmContext context) {
        log.info("[低级告警] 设备 {} - {}", context.getDeviceCode(), context.getAlarmMessage());
        return AlarmResult.builder().handled(true).handlerName(name()).message("已记录低级告警日志").build();
    }
}
