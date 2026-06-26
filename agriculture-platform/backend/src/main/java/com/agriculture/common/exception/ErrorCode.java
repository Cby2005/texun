package com.agriculture.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(10001, "用户不存在"),
    PASSWORD_ERROR(10002, "密码错误"),
    TOKEN_EXPIRED(10003, "Token已过期"),
    TOKEN_INVALID(10005, "无效的Token"),
    USER_DISABLED(10006, "账号已被禁用"),
    USERNAME_EXISTS(10007, "用户名已存在"),
    PHONE_EXISTS(10008, "手机号已注册"),
    ROLE_NOT_FOUND(10009, "角色不存在"),
    MENU_NOT_FOUND(10010, "菜单不存在"),

    ARTICLE_NOT_FOUND(20001, "文章不存在"),
    QUESTION_NOT_FOUND(20002, "问题不存在"),
    LECTURE_FULL(20003, "讲座名额已满"),
    LECTURE_EXPIRED(20004, "讲座报名已截止"),

    PRODUCT_NOT_FOUND(30001, "产品不存在"),
    BATCH_NOT_FOUND(30002, "批次不存在"),
    QRCODE_INVALID(30003, "溯源码无效"),

    FARM_NOT_FOUND(40001, "农场不存在"),
    LAND_NOT_FOUND(40002, "地块不存在"),
    CROP_NOT_FOUND(40003, "作物不存在"),
    DEVICE_NOT_FOUND(40004, "设备不存在");

    private final int code;
    private final String message;
}
