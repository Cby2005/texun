package com.agriculture.common.security;

import com.agriculture.common.exception.ErrorCode;
import com.agriculture.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        new ObjectMapper().writeValue(response.getOutputStream(),
                Result.fail(ErrorCode.UNAUTHORIZED.getCode(), "请先登录"));
    }
}
