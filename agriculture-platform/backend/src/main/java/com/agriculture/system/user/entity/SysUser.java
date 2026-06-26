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

    /** 0超管 1农场管理员 2溯源企业 3农技专家 4普通农户 5内容审核员 */
    private Integer userType;

    private String phone;
    private String email;
    private String avatar;

    /** 0正常 1禁用 */
    private Integer status;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    private String remark;
}
