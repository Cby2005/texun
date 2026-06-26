package com.agriculture.system.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.security.UserContext;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.entity.SysUserRole;
import com.agriculture.system.role.mapper.SysUserRoleMapper;
import com.agriculture.system.role.service.SysRoleService;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class SysUserController {

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SysRoleService roleService;
    private final SysUserRoleMapper userRoleMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (userType != null) {
            qw.eq(SysUser::getUserType, userType);
        }
        qw.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> page = userService.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody SysUser user) {
        userService.checkUsernameUnique(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(0);
        userService.save(user);
        syncUserRole(user.getId(), user.getUserType());
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userService.updateById(user);
        syncUserRole(id, user.getUserType());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        return Result.ok();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody SysUser user) {
        Long userId = UserContext.getCurrentUserId();
        user.setId(userId);
        user.setUsername(null);
        user.setPassword(null);
        user.setUserType(null);
        user.setStatus(null);
        userService.updateById(user);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPwd, @RequestParam String newPwd) {
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userService.getById(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return Result.fail(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        userService.updateById(user);
        return Result.ok();
    }

    private void syncUserRole(Long userId, Integer userType) {
        if (userId == null || userType == null) {
            return;
        }
        String roleCode = roleCodeOf(userType);
        SysRole role = roleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, roleCode).last("LIMIT 1"));
        if (role == null) {
            return;
        }
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRole.setDeleted(0);
        userRoleMapper.insert(userRole);
    }

    private String roleCodeOf(Integer userType) {
        return switch (userType) {
            case 0 -> "ADMIN";
            case 1 -> "FARM_ADMIN";
            case 2 -> "TRACE_ADMIN";
            case 3 -> "EXPERT";
            case 5 -> "CONSUMER";
            default -> "FARMER";
        };
    }
}
