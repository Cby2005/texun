package com.agri.common.pattern.chain;

import lombok.Builder;
import lombok.Data;

/**
 * 责任链模式：告警上下文
 */
@Data
@Builder
public class AlarmContext {
    private Long vehicleId;
    private String deviceCode;
    private String alarmLevel;
    private String alarmType;
    private String alarmMessage;
    private Object alarmData;
}
