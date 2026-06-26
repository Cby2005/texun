package com.agri.common.pattern.chain;

import lombok.extern.slf4j.Slf4j;

/**
 * 责任链模式：抽象告警处理器（模板方法）
 * 提供责任链的基础实现，子类只需实现 support 和具体处理逻辑
 */
@Slf4j
public abstract class AbstractAlarmHandler implements AlarmHandler {

    private AlarmHandler next;

    @Override
    public AlarmHandler setNext(AlarmHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public AlarmResult handle(AlarmContext context) {
        if (support(context.getAlarmLevel())) {
            log.info("[{}] 处理告警: vehicle={}, level={}, type={}", name(), context.getDeviceCode(), context.getAlarmLevel(), context.getAlarmType());
            return doHandle(context);
        }
        // 传递给下一个处理器
        if (next != null) {
            return next.handle(context);
        }
        return AlarmResult.builder().handled(false).handlerName("none").message("无处理器处理该告警").build();
    }

    /** 子类实现具体处理逻辑 */
    protected abstract AlarmResult doHandle(AlarmContext context);
}
