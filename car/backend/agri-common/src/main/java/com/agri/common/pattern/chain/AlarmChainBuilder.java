package com.agri.common.pattern.chain;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 责任链模式：告警责任链构建器
 * 自动组装告警处理链：低级 → 高级
 */
@Component
public class AlarmChainBuilder {

    private final AlarmHandler chain;

    public AlarmChainBuilder(List<AlarmHandler> handlers) {
        if (handlers.isEmpty()) {
            chain = null;
            return;
        }
        // 构建责任链
        AlarmHandler head = handlers.get(0);
        AlarmHandler current = head;
        for (int i = 1; i < handlers.size(); i++) {
            current.setNext(handlers.get(i));
            current = handlers.get(i);
        }
        chain = head;
    }

    /**
     * 处理告警
     */
    public AlarmResult process(AlarmContext context) {
        if (chain == null) {
            return AlarmResult.builder().handled(false).message("无告警处理器").build();
        }
        return chain.handle(context);
    }
}
