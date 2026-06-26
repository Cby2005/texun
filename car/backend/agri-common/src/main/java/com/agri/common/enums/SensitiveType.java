package com.agri.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感数据脱敏类型枚举
 */
@Getter
@AllArgsConstructor
public enum SensitiveType {
    /** 手机号：138****8888 */
    PHONE(3, 4),
    /** 身份证：110***********1234 */
    ID_CARD(3, 4),
    /** 邮箱：z***@example.com */
    EMAIL(1, 0),
    /** 姓名：张*明 */
    NAME(1, 1),
    /** 银行卡：6222 **** **** 1234 */
    BANK_CARD(4, 4),
    /** 地址：北京市**区*** */
    ADDRESS(3, 0);

    /** 保留前缀长度 */
    private final int prefixLen;
    /** 保留后缀长度 */
    private final int suffixLen;
}
