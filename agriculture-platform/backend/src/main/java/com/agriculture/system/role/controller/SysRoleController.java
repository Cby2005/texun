package com.agriculture.system.role.controller;

import com.agriculture.common.result.Result;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.service.SysRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理")
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<SysRole>> list() {
        return Result.ok(roleService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok();
    }
}
