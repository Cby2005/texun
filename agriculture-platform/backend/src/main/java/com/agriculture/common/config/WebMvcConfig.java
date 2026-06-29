package com.agriculture.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件上传目录映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:./runtime/files/");
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:D:/作业/texun/agriculture-platform/videos/");
        registry.addResourceHandler("/videos/covers/**")
                .addResourceLocations("file:D:/作业/texun/agriculture-platform/videos/covers/");
    }
}
