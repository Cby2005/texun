package com.agriculture.system.service.impl;

import com.agriculture.common.security.LoginUser;
import com.agriculture.common.security.UserDetailsImpl;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.menu.service.SysMenuService;
import com.agriculture.system.role.service.SysRoleService;
import com.agriculture.system.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final SysRoleService roleService;
    private final SysMenuService menuService;

    @Override
    public LoginUser authenticate(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            return new LoginUser(userDetails.getId(), userDetails.getUsername(), userDetails.getRoles());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        List<SysRole> roles = roleService.getRolesByUserId(userId);
        return roles.stream().map(SysRole::getCode).collect(Collectors.toList());
    }
}
