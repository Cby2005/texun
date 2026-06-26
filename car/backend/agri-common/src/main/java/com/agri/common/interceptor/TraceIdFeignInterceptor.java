package com.agri.common.interceptor;

import com.agri.common.constant.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 请求拦截器
 * 在微服务间调用时自动传递 TraceId
 */
@Configuration
public class TraceIdFeignInterceptor {

    @Bean
    public RequestInterceptor traceIdRequestInterceptor() {
        return (RequestTemplate template) -> {
            String traceId = MDC.get(Constants.TRACE_ID_KEY);
            if (traceId != null) {
                template.header(Constants.TRACE_ID_HEADER, traceId);
            }
        };
    }
}
