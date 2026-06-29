package com.agriculture.system.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SecurityInfoVO {
    private Boolean phoneBound;
    private Boolean emailFilled;
    private LocalDateTime passwordUpdateTime;
    private LocalDateTime lastLoginTime;
    private String securityLevel;
    private String maskedPhone;
}
