package com.agri.telemetry.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遥测数据 WebSocket 处理器
 * 支持按设备ID订阅实时数据
 */
@Slf4j
@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler {

    /**
     * 所有连接的客户端
     */
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 设备订阅关系: sessionId -> subscribedVehicleId
     */
    private final Map<String, Long> subscriptions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket连接建立: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            // 协议: subscribe:vehicleId 或 unsubscribe
            if (payload.startsWith("subscribe:")) {
                Long vehicleId = Long.parseLong(payload.substring(10));
                subscriptions.put(session.getId(), vehicleId);
                log.info("客户端 {} 认阅设备 {}", session.getId(), vehicleId);
                session.sendMessage(new TextMessage("{\"type\":\"subscribed\",\"vehicleId\":" + vehicleId + "}"));
            } else if ("unsubscribe".equals(payload)) {
                subscriptions.remove(session.getId());
                session.sendMessage(new TextMessage("{\"type\":\"unsubscribed\"}"));
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        subscriptions.remove(session.getId());
        log.info("WebSocket连接关闭: {}", session.getId());
    }

    /**
     * 推送遥测数据到订阅了该设备的客户端
     */
    public void pushTelemetryData(Long vehicleId, String data) {
        String message = "{\"type\":\"telemetry\",\"vehicleId\":" + vehicleId + ",\"data\":" + data + "}";
        subscriptions.forEach((sessionId, subscribedVehicleId) -> {
            if (vehicleId.equals(subscribedVehicleId)) {
                WebSocketSession session = sessions.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("推送遥测数据失败: sessionId={}", sessionId, e);
                    }
                }
            }
        });
    }

    /**
     * 广播告警数据到所有客户端
     */
    public void broadcastAlarm(String alarmData) {
        String message = "{\"type\":\"alarm\",\"data\":" + alarmData + "}";
        sessions.forEach((sessionId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("广播告警失败: sessionId={}", sessionId, e);
                }
            }
        });
    }

    /**
     * 获取当前在线客户端数
     */
    public int getOnlineCount() {
        return sessions.size();
    }
}
