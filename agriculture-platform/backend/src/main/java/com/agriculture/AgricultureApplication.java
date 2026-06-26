package com.agriculture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgricultureApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgricultureApplication.class, args);
        System.out.println("========================================");
        System.out.println("  智慧农业综合服务平台 启动成功!");
        System.out.println("  Swagger: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
