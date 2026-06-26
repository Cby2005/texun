package com.agri.vehicle;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 无人车设备服务启动类
 *
 * 功能：
 * 1. 设备注册/查询/更新/删除（CRUD）
 * 2. 设备状态管理（状态模式：7种状态 x 10种操作）
 * 3. 设备型号管理（CRUD）
 * 4. 故障记录管理（记录/查询/解决）
 * 5. 设备心跳检测（Redis 60秒过期）
 * 6. 设备统计信息（总数/在线/作业/离线/故障/维护/空闲/在线率）
 * 7. 远程指令下发（上线/下线/作业/返航/紧急停车/故障/维护/报废）
 * 8. Kafka 事件发送（状态变更/故障告警）
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.agri")
@ComponentScan(basePackages = "com.agri")
@MapperScan("com.agri.vehicle.mapper")
public class AgriVehicleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriVehicleApplication.class, args);
        log.info("========================================");
        log.info("  智农链控 无人车设备服务启动成功 - 端口: 8200  ");
        log.info("========================================");
    }
}
