package com.agriculture.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class SysLoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String loginIp;
    private String loginLocation;
    private String browser;
    private String os;
    private String loginStatus;
    private LocalDateTime loginTime;
    private String remark;
}
