package com.agri.simulator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 无人车模拟器启动类
 *
 * 功能：
 * 1. 创建N台虚拟无人车（默认10台）
 * 2. 每5秒上报遥测数据到Kafka（vehicle.telemetry.raw）
 * 3. 每5秒上报位置信息到Kafka（vehicle.location）
 * 4. 消费指令消息（vehicle.command.ack）
 * 5. 模拟设备状态：IDLE/ONLINE/OFFLINE/WORKING/FAULT
 * 6. 模拟作业行为：移动/电量消耗/任务进度
 * 7. 随机故障模拟（0.1%概率）
 * 8. REST API控制：启动/停止/查看状态
 */
@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(basePackages = "com.agri")
public class AgriSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriSimulatorApplication.class, args);
        log.info("========================================");
        log.info("  智农链控 无人车模拟器启动成功 - 端口: 8800  ");
        log.info("========================================");
    }
}
