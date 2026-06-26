package com.agriculture.system.role.service;

import com.agriculture.system.role.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> getRolesByUserId(Long userId);
}
