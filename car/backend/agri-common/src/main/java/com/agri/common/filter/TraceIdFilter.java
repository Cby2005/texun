package com.agri.common.filter;

import com.agri.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * 全局请求追踪 TraceId 过滤器
 * 为每个请求生成唯一 traceId，贯穿整个微服务调用链
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 从请求头获取 traceId，如果没有则生成
        String traceId = httpRequest.getHeader(Constants.TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        // 放入 MDC，日志自动携带
        MDC.put(Constants.TRACE_ID_KEY, traceId);
        // 放入请求属性，后续服务可读取
        request.setAttribute(Constants.TRACE_ID_KEY, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(Constants.TRACE_ID_KEY);
        }
    }
}
