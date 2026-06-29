package com.agriculture.system.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLogVO {
    private Long id;
    private String username;
    private String loginIp;
    private String loginLocation;
    private String browser;
    private String os;
    private String loginStatus;
    private LocalDateTime loginTime;
}
