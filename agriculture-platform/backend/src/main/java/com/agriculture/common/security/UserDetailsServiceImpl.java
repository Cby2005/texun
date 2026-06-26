package com.agriculture.common.security;

import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.mapper.SysUserMapper;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getCode).collect(Collectors.toList());
        return new UserDetailsImpl(user, roleCodes);
    }
}
