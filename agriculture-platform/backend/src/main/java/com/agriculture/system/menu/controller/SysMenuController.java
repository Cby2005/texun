package com.agriculture.system.menu.controller;

import com.agriculture.common.result.Result;
import com.agriculture.system.menu.entity.SysMenu;
import com.agriculture.system.menu.service.SysMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "菜单管理")
public class SysMenuController {

    private final SysMenuService menuService;

    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<SysMenu>> getTree() {
        return Result.ok(menuService.getMenuTree());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<SysMenu>> list() {
        return Result.ok(menuService.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setMenuId(id);
        menuService.updateById(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.ok();
    }
}
