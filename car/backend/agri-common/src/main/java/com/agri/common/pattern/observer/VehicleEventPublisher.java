package com.agri.common.pattern.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式：车辆事件发布者（被观察者）
 * 管理所有监听器，发布事件时通知所有监听器
 */
@Slf4j
@Component
public class VehicleEventPublisher {

    private final List<VehicleEventListener> listeners = new ArrayList<>();
    private final List<VehicleEventListener> autoListeners;

    public VehicleEventPublisher(List<VehicleEventListener> autoListeners) {
        this.autoListeners = autoListeners;
    }

    @PostConstruct
    public void init() {
        listeners.addAll(autoListeners);
        log.info("已注册 {} 个车辆事件监听器", listeners.size());
    }

    /**
     * 发布车辆事件，通知所有监听器
     */
    public void publish(VehicleEvent event) {
        for (VehicleEventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                log.error("监听器 {} 处理事件失败: {}", listener.name(), e.getMessage(), e);
            }
        }
    }
}
