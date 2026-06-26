package com.agriculture.system.role.service.impl;

import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.mapper.SysRoleMapper;
import com.agriculture.system.role.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Primary
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return this.baseMapper.selectRolesByUserId(userId);
    }
}
