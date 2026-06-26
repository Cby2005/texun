package com.agriculture.system.user.service;

import com.agriculture.system.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    SysUser getByPhone(String phone);
    void checkUsernameUnique(String username);
    void checkPhoneUnique(String phone);
}
