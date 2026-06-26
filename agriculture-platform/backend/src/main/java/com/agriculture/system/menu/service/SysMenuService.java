package com.agriculture.system.menu.service;

import com.agriculture.system.menu.entity.SysMenu;
import com.agriculture.system.menu.dto.RouterTree;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getMenusByUserId(Long userId);
    List<String> getPermsByUserId(Long userId);
    List<SysMenu> getMenuTree();
    List<RouterTree> getRouterTree(Long userId);
}
