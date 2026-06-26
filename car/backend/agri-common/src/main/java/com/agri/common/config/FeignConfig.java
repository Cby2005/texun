package com.agri.common.config;

import com.agri.common.constant.Constants;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 配置 - 传递用户上下文
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userId = request.getHeader(Constants.USER_ID_HEADER);
                String username = request.getHeader(Constants.USERNAME_HEADER);
                String role = request.getHeader(Constants.ROLE_HEADER);
                if (userId != null) template.header(Constants.USER_ID_HEADER, userId);
                if (username != null) template.header(Constants.USERNAME_HEADER, username);
                if (role != null) template.header(Constants.ROLE_HEADER, role);
            }
        };
    }
}
