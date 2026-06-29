package com.agriculture.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_phone_verify_code")
public class SysPhoneVerifyCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private Integer used;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
}
