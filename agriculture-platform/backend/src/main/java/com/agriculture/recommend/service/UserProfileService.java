package com.agriculture.recommend.service;

import com.agriculture.recommend.entity.UserProfile;
import com.agriculture.recommend.vo.UserProfileVO;
import java.util.Map;

public interface UserProfileService {
    UserProfile refreshProfile(Long userId);
    UserProfile getOrBuildProfile(Long userId);
    UserProfileVO getProfileVO(Long userId);
    Map<String, Double> parseInterestTags(String interestTags);
    void refreshAllUserProfiles();
}
