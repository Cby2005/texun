package com.agri.common.annotation;

import java.lang.annotation.*;

/**
 * 角色注解 - 标记接口需要的角色
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {
    /**
     * 角色编码列表
     */
    String[] value();
}
