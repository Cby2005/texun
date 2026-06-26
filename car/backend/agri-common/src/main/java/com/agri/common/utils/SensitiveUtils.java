package com.agri.common.utils;

import com.agri.common.enums.SensitiveType;

/**
 * 敏感数据脱敏工具类
 */
public class SensitiveUtils {

    private static final String MASK = "*";

    /**
     * 通用脱敏
     */
    public static String desensitize(String value, SensitiveType type) {
        if (value == null || value.isEmpty()) return value;
        int prefixLen = type.getPrefixLen();
        int suffixLen = type.getSuffixLen();
        int maskLen = value.length() - prefixLen - suffixLen;
        if (maskLen <= 0) return value;
        StringBuilder sb = new StringBuilder();
        sb.append(value, 0, prefixLen);
        for (int i = 0; i < maskLen; i++) sb.append(MASK);
        if (suffixLen > 0) sb.append(value.substring(value.length() - suffixLen));
        return sb.toString();
    }

    public static String phone(String phone) {
        return desensitize(phone, SensitiveType.PHONE);
    }

    public static String idCard(String idCard) {
        return desensitize(idCard, SensitiveType.ID_CARD);
    }

    public static String email(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }

    public static String name(String name) {
        return desensitize(name, SensitiveType.NAME);
    }
}
