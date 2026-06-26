package com.agriculture.farm.enterprise.mapper;

import com.agriculture.farm.enterprise.entity.FarmEnterprise;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmEnterpriseMapper extends BaseMapper<FarmEnterprise> {
    @Select("SELECT COUNT(*) FROM farm_enterprise WHERE deleted = 0")
    long countActive();
}
