package com.agriculture.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deptId;
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    /** 真实姓名 */
    private String realName;
    /** 0管理员 1农场管理员 2溯源企业 3专家 4农户 5消费者 */
    private Integer userType;
    /** 所属基地ID */
    private Long baseId;
    /** 默认城市名称 */
    private String defaultCity;
    /** 默认城市编码(高德adcode) */
    private String defaultAdcode;
    private String phone;
    private String email;
    private String avatar;
    private String profile;
    /** 0正常 1禁用 */
    private Integer status;
    private LocalDateTime passwordUpdateTime;
    private LocalDateTime phoneBindTime;
    private LocalDateTime lastLoginTime;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    private String remark;
}
