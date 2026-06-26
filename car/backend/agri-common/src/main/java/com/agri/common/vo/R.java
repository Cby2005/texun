package com.agri.common.vo;

import com.agri.common.constant.Constants;
import com.agri.common.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一返回结果封装
 * 包含 traceId 用于请求追踪
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;
    private String traceId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private R() {
        this.timestamp = LocalDateTime.now();
        this.traceId = MDC.get(Constants.TRACE_ID_KEY);
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(ResultCode code) {
        return fail(code.getCode(), code.getMessage());
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(ResultCode code, String message) {
        return fail(code.getCode(), message);
    }
}
