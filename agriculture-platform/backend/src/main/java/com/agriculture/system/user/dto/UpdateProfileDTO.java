package com.agriculture.system.user.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String nickname;
    private String realName;
    private String email;
    private String profile;
    private String greenhouseName;
}
