package com.agri.common.annotation;

import com.agri.common.enums.SensitiveType;
import java.lang.annotation.*;

/**
 * 敏感数据脱敏注解
 * 标记在字段上，返回前端时自动脱敏
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {
    /** 脱敏类型 */
    SensitiveType value();
}
