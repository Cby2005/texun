package com.agriculture.system.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    private Long id;
    private String username;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private String profile;
    private String greenhouseName;
    private String userType;
    private String roleName;
    private Boolean phoneBound;
    private String securityLevel;
    private LocalDateTime lastLoginTime;
    private LocalDateTime passwordUpdateTime;
}
