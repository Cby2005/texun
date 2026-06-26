package com.agri.common.pattern.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 责任链模式：高级告警处理器（通知管理员 + 记录故障）
 */
@Slf4j
@Component
public class HighLevelAlarmHandler extends AbstractAlarmHandler {

    @Override
    public String name() {
        return "HighLevelAlarmHandler";
    }

    @Override
    public boolean support(String level) {
        return "HIGH".equals(level) || "CRITICAL".equals(level);
    }

    @Override
    protected AlarmResult doHandle(AlarmContext context) {
        log.error("[高级告警] 设备 {} - {} - 通知管理员处理", context.getDeviceCode(), context.getAlarmMessage());
        // 实际项目中这里会发送通知（短信、邮件、WebSocket推送等）
        return AlarmResult.builder().handled(true).handlerName(name()).message("已通知管理员并记录故障").build();
    }
}
