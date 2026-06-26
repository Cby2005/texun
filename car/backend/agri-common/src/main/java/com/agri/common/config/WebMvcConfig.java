package com.agri.common.config;

import com.agri.common.constant.Constants;
import com.agri.common.dto.LoginUser;
import com.agri.common.utils.UserContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web MVC 配置 - 用户上下文拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String userId = request.getHeader(Constants.USER_ID_HEADER);
                String username = request.getHeader(Constants.USERNAME_HEADER);
                String role = request.getHeader(Constants.ROLE_HEADER);

                if (userId != null) {
                    LoginUser user = LoginUser.builder()
                            .userId(Long.parseLong(userId))
                            .username(username)
                            .role(role)
                            .build();
                    UserContext.set(user);
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                        Object handler, Exception ex) {
                UserContext.remove();
            }
        }).addPathPatterns("/**")
          .excludePathPatterns("/actuator/**");
    }
}
