package com.agriculture.system.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long menuId;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String query;

    /** M目录 C菜单 F按钮 */
    private String menuType;

    /** 0显示 1隐藏 */
    private Integer visible;

    /** 0正常 1停用 */
    private Integer status;

    private String perms;

    private String icon;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysMenu> children;
}
