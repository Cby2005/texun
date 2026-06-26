package com.agri.common.annotation;

import java.lang.annotation.*;

/**
 * 接口幂等性注解
 * 防止重复提交，基于 Redis + Token 实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /** 幂等 key 前缀 */
    String prefix() default "";
    /** 过期时间（秒） */
    int expireSeconds() default 10;
    /** 提示信息 */
    String message() default "请勿重复提交";
}
