package com.agri.common.utils;

import com.agri.common.dto.LoginUser;

/**
 * 用户上下文 - 使用 ThreadLocal 存储当前登录用户信息
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {}

    public static void set(LoginUser user) {
        USER_HOLDER.set(user);
    }

    public static LoginUser get() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser user = get();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        LoginUser user = get();
        return user != null ? user.getUsername() : null;
    }

    public static String getRole() {
        LoginUser user = get();
        return user != null ? user.getRole() : null;
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
