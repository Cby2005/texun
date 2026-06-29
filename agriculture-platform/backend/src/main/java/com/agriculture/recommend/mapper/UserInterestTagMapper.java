package com.agriculture.recommend.mapper;

import com.agriculture.recommend.entity.UserInterestTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserInterestTagMapper extends BaseMapper<UserInterestTag> {

    @Update("INSERT INTO user_interest_tag (user_id, tag_name, tag_type, weight, update_time) "
            + "VALUES (#{userId}, #{tagName}, #{tagType}, #{weight}, NOW()) "
            + "ON DUPLICATE KEY UPDATE weight = weight + #{weight}, update_time = NOW()")
    int increaseWeight(@Param("userId") Long userId, @Param("tagName") String tagName,
                       @Param("tagType") String tagType, @Param("weight") Double weight);
}
