package com.agriculture.common.security;

import com.agriculture.system.user.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Integer status;
    private Integer userType;
    private List<String> roles;

    public UserDetailsImpl(SysUser user, List<String> roles) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.userType = user.getUserType();
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return status == 0; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return status == 0; }
}
