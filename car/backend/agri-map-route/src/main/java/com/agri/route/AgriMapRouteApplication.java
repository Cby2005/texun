package com.agri.route;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 地图与路线规划服务启动类
 *
 * 功能：
 * 1. Boustrophedon 往复式覆盖路径规划
 * 2. A* 点到点避障路径规划
 * 3. 路线保存/查询/删除
 * 4. 自动计算路线总长度和预估时间
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.agri")
@MapperScan("com.agri.route.mapper")
public class AgriMapRouteApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriMapRouteApplication.class, args);
        log.info("========================================");
        log.info("  智农链控 地图路线规划服务启动成功 - 端口: 8500  ");
        log.info("========================================");
    }
}
