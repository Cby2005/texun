package com.agri.telemetry;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 遥测数据服务启动类
 *
 * 功能：
 * 1. Kafka消费原始遥测数据（vehicle.telemetry.raw）
 * 2. Kafka消费设备告警（vehicle.alarm）
 * 3. 数据写入MySQL + Redis缓存（5分钟过期）
 * 4. WebSocket实时推送遥测数据
 * 5. WebSocket广播告警数据
 * 6. 历史轨迹查询
 * 7. 告警管理（确认/解决）
 * 8. 遥测统计
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.agri")
@MapperScan("com.agri.telemetry.mapper")
public class AgriTelemetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriTelemetryApplication.class, args);
        log.info("========================================");
        log.info("  智农链控 遥测数据服务启动成功 - 端口: 8300  ");
        log.info("  WebSocket: ws://localhost:8300/ws/telemetry  ");
        log.info("========================================");
    }
}
