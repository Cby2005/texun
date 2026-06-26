package com.agri.task;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 作业任务与指令下发服务启动类
 *
 * 功能：
 * 1. 任务管理（CRUD、设备分配、路线绑定、状态控制）
 * 2. 命令模式（5种命令：START/STOP/PAUSE/RESUME/EMERGENCY_STOP）
 * 3. 指令下发（Kafka发送+重试+取消）
 * 4. 指令ACK消费（状态自动更新+任务进度更新）
 * 5. 任务统计（7项指标+完成率）
 * 6. 分布式锁防重复发送
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.agri")
@ComponentScan(basePackages = "com.agri")
@MapperScan("com.agri.task.mapper")
public class AgriTaskCommandApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriTaskCommandApplication.class, args);
        log.info("========================================");
        log.info("  智农链控 作业任务服务启动成功 - 端口: 8600  ");
        log.info("========================================");
    }
}
