package com.agri.common.annotation;

import java.lang.annotation.*;

/**
 * 权限注解 - 标记接口需要的权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    /**
     * 权限标识
     */
    String value();

    /**
     * 权限描述
     */
    String description() default "";
}
