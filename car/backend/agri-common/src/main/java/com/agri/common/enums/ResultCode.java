package com.agri.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILURE(500, "操作失败"),

    // 认证相关 1001-1099
    UNAUTHORIZED(1001, "未登录或Token已过期"),
    FORBIDDEN(1002, "无权限访问"),
    TOKEN_INVALID(1003, "Token无效"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    ACCOUNT_LOCKED(1005, "账号已被锁定"),
    ACCOUNT_DISABLED(1006, "账号已被禁用"),
    LOGIN_FAILED(1007, "用户名或密码错误"),
    CAPTCHA_ERROR(1008, "验证码错误"),
    ACCOUNT_EXISTS(1009, "账号已存在"),

    // 参数校验 2001-2099
    PARAM_ERROR(2001, "参数校验失败"),
    PARAM_MISSING(2002, "缺少必要参数"),
    PARAM_TYPE_ERROR(2003, "参数类型错误"),

    // 业务异常 3001-3999
    DATA_NOT_FOUND(3001, "数据不存在"),
    DATA_ALREADY_EXISTS(3002, "数据已存在"),
    DATA_SAVE_FAILED(3003, "数据保存失败"),
    DATA_UPDATE_FAILED(3004, "数据更新失败"),
    DATA_DELETE_FAILED(3005, "数据删除失败"),

    // 设备相关 4001-4099
    VEHICLE_NOT_FOUND(4001, "无人车不存在"),
    VEHICLE_OFFLINE(4002, "无人车离线"),
    VEHICLE_BUSY(4003, "无人车忙碌中"),
    VEHICLE_FAULT(4004, "无人车故障"),
    VEHICLE_STATE_ERROR(4005, "设备状态不允许此操作"),
    COMMAND_SEND_FAILED(4006, "指令发送失败"),
    COMMAND_TIMEOUT(4007, "指令执行超时"),

    // 任务相关 5001-5099
    TASK_NOT_FOUND(5001, "任务不存在"),
    TASK_CONFLICT(5002, "任务冲突"),
    TASK_ALREADY_ASSIGNED(5003, "任务已分配"),
    ROUTE_NOT_FOUND(5004, "路线不存在"),

    // 溯源相关 6001-6099
    BATCH_NOT_FOUND(6001, "批次不存在"),
    CHAIN_VERIFY_FAILED(6002, "哈希链校验失败"),
    CHAIN_TAMPERED(6003, "数据疑似被篡改"),
    QR_CODE_GENERATE_FAILED(6004, "二维码生成失败"),

    // 系统异常 9001-9099
    SYSTEM_ERROR(9001, "系统内部错误"),
    SERVICE_UNAVAILABLE(9002, "服务不可用"),
    REQUEST_TOO_FREQUENT(9003, "请求过于频繁"),
    EXTERNAL_API_ERROR(9004, "外部API调用失败");

    private final int code;
    private final String message;
}
