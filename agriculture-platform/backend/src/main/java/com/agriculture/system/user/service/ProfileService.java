package com.agriculture.system.user.service;

import com.agriculture.common.result.PageResult;
import com.agriculture.system.user.dto.*;
import com.agriculture.system.user.vo.LoginLogVO;
import com.agriculture.system.user.vo.SecurityInfoVO;
import com.agriculture.system.user.vo.UserProfileVO;

import java.util.List;
import java.util.Map;

public interface ProfileService {
    UserProfileVO getProfile(Long userId);
    UserProfileVO updateProfile(Long userId, UpdateProfileDTO dto);
    void updatePassword(Long userId, UpdatePasswordDTO dto);
    String uploadAvatar(Long userId, byte[] fileBytes, String originalName);
    /** 返回验证码（开发环境） */
    String sendPhoneCode(String phone);
    void bindPhone(Long userId, BindPhoneDTO dto);
    void changePhone(Long userId, ChangePhoneDTO dto);
    void unbindPhone(Long userId, UnbindPhoneDTO dto);
    SecurityInfoVO getSecurityInfo(Long userId);
    PageResult<LoginLogVO> getLoginLogs(Long userId, int pageNum, int pageSize);

    /** 我的收藏 */
    PageResult<Map<String, Object>> getFavorites(Long userId, int pageNum, int pageSize);
    void removeFavorite(Long userId, Long articleId);

    /** 我的评论 */
    PageResult<Map<String, Object>> getComments(Long userId, int pageNum, int pageSize);
    void deleteComment(Long userId, Long commentId);

    /** 行为统计 */
    Map<String, Object> getBehaviorStats(Long userId);

    /** 兴趣画像 */
    List<Map<String, Object>> getInterestTags(Long userId);
}
