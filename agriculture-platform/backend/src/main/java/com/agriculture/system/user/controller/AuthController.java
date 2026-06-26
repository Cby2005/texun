package com.agriculture.system.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.security.JwtUtil;
import com.agriculture.common.security.LoginUser;
import com.agriculture.system.menu.dto.RouterTree;
import com.agriculture.system.menu.service.SysMenuService;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.service.SysRoleService;
import com.agriculture.system.service.LoginService;
import com.agriculture.system.user.dto.LoginRequest;
import com.agriculture.system.user.dto.RegisterRequest;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录注册相关")
public class AuthController {

    private final LoginService loginService;
    private final SysUserService userService;
    private final SysRoleService roleService;
    private final SysMenuService menuService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        LoginUser loginUser = loginService.authenticate(req.getUsername(), req.getPassword());
        List<String> roles = loginUser.getRoles();
        String token = jwtUtil.generateAccessToken(loginUser.getId(), loginUser.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(loginUser.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("userId", loginUser.getId());
        result.put("username", loginUser.getUsername());
        result.put("roles", roles);

        SysUser user = userService.getById(loginUser.getId());
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatar", user.getAvatar());
            result.put("userType", user.getUserType());
        }
        return Result.ok("登录成功", result);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest req) {
        userService.checkUsernameUnique(req.getUsername());
        if (req.getPhone() != null) {
            userService.checkPhoneUnique(req.getPhone());
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getUsername());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setUserType(req.getUserType() != null ? req.getUserType() : 4);
        user.setStatus(0);
        userService.save(user);
        return Result.ok();
    }

    @GetMapping("/userinfo")
    public Result<Map<String, Object>> userInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);
        List<String> roles = jwtUtil.getRoles(token);
        SysUser user = userService.getById(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", jwtUtil.getUsername(token));
        result.put("roles", roles);
        if (user != null) {
            result.put("nickname", user.getNickname());
            result.put("avatar", user.getAvatar());
            result.put("userType", user.getUserType());
            result.put("phone", user.getPhone());
            result.put("email", user.getEmail());
        }
        List<String> perms = menuService.getPermsByUserId(userId);
        result.put("permissions", perms);
        return Result.ok(result);
    }

    @GetMapping("/routers")
    public Result<List<RouterTree>> getRouters(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);
        List<RouterTree> routers = menuService.getRouterTree(userId);
        return Result.ok(routers);
    }
}
