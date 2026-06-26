package com.agri.common.pattern.chain;

import lombok.Builder;
import lombok.Data;

/**
 * 责任链模式：告警处理结果
 */
@Data
@Builder
public class AlarmResult {
    private boolean handled;
    private String handlerName;
    private String message;
}
