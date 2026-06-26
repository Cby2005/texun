package com.agriculture.system.user.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.common.exception.ErrorCode;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.mapper.SysUserMapper;
import com.agriculture.system.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Primary
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    public SysUser getByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone));
    }

    @Override
    public void checkUsernameUnique(String username) {
        if (getByUsername(username) != null) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }
    }

    @Override
    public void checkPhoneUnique(String phone) {
        if (StringUtils.hasText(phone) && getByPhone(phone) != null) {
            throw new BizException(ErrorCode.PHONE_EXISTS);
        }
    }
}
