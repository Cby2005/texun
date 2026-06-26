package com.agriculture.system.role.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String roleKey;

    private Integer roleSort;

    /** 0正常 1停用 */
    private Integer status;

    private String description;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    private String remark;

    @TableField(exist = false)
    private Integer userCount;
}
