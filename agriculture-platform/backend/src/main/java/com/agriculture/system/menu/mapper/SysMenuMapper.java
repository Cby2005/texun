package com.agriculture.system.menu.mapper;

import com.agriculture.system.menu.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} " +
            "AND m.status = 0 AND m.menu_type IN ('M','C') ORDER BY m.parent_id, m.order_num")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT m.perms FROM sys_menu m INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} " +
            "AND m.menu_type = 'F' AND m.perms IS NOT NULL AND m.perms != ''")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
