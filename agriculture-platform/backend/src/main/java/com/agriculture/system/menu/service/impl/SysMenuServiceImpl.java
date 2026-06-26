package com.agriculture.system.menu.service.impl;

import com.agriculture.system.menu.entity.SysMenu;
import com.agriculture.system.menu.dto.RouterTree;
import com.agriculture.system.menu.mapper.SysMenuMapper;
import com.agriculture.system.menu.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenusByUserId(Long userId) {
        return this.baseMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<String> getPermsByUserId(Long userId) {
        return this.baseMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> all = this.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getMenuType, "M", "C")
                .eq(SysMenu::getStatus, 0)
                .orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum));
        return buildTree(all, 0L);
    }

    @Override
    public List<RouterTree> getRouterTree(Long userId) {
        List<SysMenu> menus = getMenusByUserId(userId);
        List<RouterTree> routers = menus.stream().map(m -> {
            RouterTree r = new RouterTree();
            r.setId(m.getMenuId());
            r.setParentId(m.getParentId());
            r.setName(m.getPath() != null ? m.getPath().replace("/", "") : "");
            r.setPath(m.getPath());
            r.setComponent(m.getComponent());
            r.setIcon(m.getIcon());
            r.setOrderNum(m.getOrderNum());
            RouterTree.Meta meta = new RouterTree.Meta();
            meta.setTitle(m.getMenuName());
            meta.setIcon(m.getIcon());
            meta.setHidden(m.getVisible() != null && m.getVisible() == 1);
            r.setMeta(meta);
            return r;
        }).collect(Collectors.toList());
        return buildRouterTree(routers, 0L);
    }

    private List<SysMenu> buildTree(List<SysMenu> list, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : list) {
            if (menu.getParentId().equals(parentId)) {
                menu.setChildren(buildTree(list, menu.getMenuId()));
                tree.add(menu);
            }
        }
        return tree;
    }

    private List<RouterTree> buildRouterTree(List<RouterTree> list, Long parentId) {
        List<RouterTree> tree = new ArrayList<>();
        for (RouterTree r : list) {
            if (r.getParentId().equals(parentId)) {
                r.setChildren(buildRouterTree(list, r.getId()));
                tree.add(r);
            }
        }
        return tree;
    }
}
