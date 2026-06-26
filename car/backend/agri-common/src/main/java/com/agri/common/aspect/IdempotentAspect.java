package com.agri.common.aspect;

import com.agri.common.annotation.Idempotent;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 接口幂等性切面
 * 通过 Redis Token 机制防止重复提交
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Idempotent-Token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("Idempotent-Token");
        }
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "缺少幂等Token");
        }

        String key = "idempotent:" + (idempotent.prefix().isEmpty() ? joinPoint.getSignature().toShortString() : idempotent.prefix()) + ":" + token;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", idempotent.expireSeconds(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), idempotent.message());
        }

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            // 执行失败时删除幂等key，允许重试
            stringRedisTemplate.delete(key);
            throw e;
        }
    }
}
