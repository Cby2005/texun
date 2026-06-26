package com.agri.common.pattern.observer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 观察者模式：车辆事件
 */
@Data
@Builder
public class VehicleEvent {
    private Long vehicleId;
    private String deviceCode;
    private String eventType;
    private String oldStatus;
    private String newStatus;
    private String message;
    private Object data;
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
