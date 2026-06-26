package com.agriculture.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.agriculture.knowledge.article.entity.KnowledgeArticle;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper;
import com.agriculture.knowledge.article_tag.entity.KnowledgeArticleTag;
import com.agriculture.knowledge.article_tag.mapper.KnowledgeArticleTagMapper;
import com.agriculture.knowledge.tag.entity.KnowledgeTag;
import com.agriculture.knowledge.tag.mapper.KnowledgeTagMapper;
import com.agriculture.recommend.entity.UserBehavior;
import com.agriculture.recommend.entity.UserProfile;
import com.agriculture.recommend.mapper.UserBehaviorMapper;
import com.agriculture.recommend.mapper.UserProfileMapper;
import com.agriculture.recommend.service.UserProfileService;
import com.agriculture.recommend.vo.UserProfileVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserBehaviorMapper behaviorMapper;
    private final UserProfileMapper profileMapper;
    private final KnowledgeArticleTagMapper articleTagMapper;
    private final KnowledgeTagMapper tagMapper;
    private final KnowledgeArticleMapper articleMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public UserProfile refreshProfile(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<UserBehavior> behaviors = behaviorMapper.selectList(
            new LambdaQueryWrapper<UserBehavior>().eq(UserBehavior::getUserId, userId)
                .ge(UserBehavior::getCreateTime, since).ne(UserBehavior::getBehaviorType, "dislike"));
        Map<String, Double> tagScores = new LinkedHashMap<>();
        for (UserBehavior b : behaviors) {
            List<KnowledgeArticleTag> relations = articleTagMapper.selectList(
                new LambdaQueryWrapper<KnowledgeArticleTag>().eq(KnowledgeArticleTag::getArticleId, b.getArticleId()));
            if (relations.isEmpty()) continue;
            List<Long> tagIds = relations.stream().map(KnowledgeArticleTag::getTagId).collect(Collectors.toList());
            Map<Long, KnowledgeTag> tagMap = tagMapper.selectBatchIds(tagIds).stream()
                .collect(Collectors.toMap(KnowledgeTag::getId, t -> t));
            for (KnowledgeArticleTag r : relations) {
                KnowledgeTag tag = tagMap.get(r.getTagId());
                if (tag == null) continue;
                double w = r.getWeight() != null ? r.getWeight() : 1.0;
                tagScores.merge(tag.getName(), b.getWeight() != null ? b.getWeight() * w : w, Double::sum);
            }
        }
        Map<String, Double> topTags = tagScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        UserProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null) { profile = new UserProfile(); profile.setUserId(userId); }
        try { profile.setInterestTags(objectMapper.writeValueAsString(topTags)); } catch (Exception e) { profile.setInterestTags("{}"); }
        profile.setCropPreference(joinByType(topTags, "作物"));
        profile.setTechPreference(joinByType(topTags, "技术"));
        profile.setRegionPreference(joinByType(topTags, "地区"));
        profile.setProfileText(buildProfileText(topTags));
        if (profile.getId() == null) profileMapper.insert(profile);
        else profileMapper.updateById(profile);
        return profile;
    }

    @Override
    public UserProfile getOrBuildProfile(Long userId) {
        if (userId == null) return null;
        UserProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null || !StringUtils.hasText(profile.getInterestTags())) return refreshProfile(userId);
        return profile;
    }

    @Override
    public UserProfileVO getProfileVO(Long userId) {
        UserProfile profile = getOrBuildProfile(userId);
        UserProfileVO vo = new UserProfileVO(); vo.setUserId(userId);
        if (profile == null) { vo.setColdStart(true); vo.setInterestTags(Collections.emptyMap()); vo.setProfileText("暂无行为记录，处于冷启动阶段。"); return vo; }
        Map<String, Double> tags = parseInterestTags(profile.getInterestTags());
        vo.setInterestTags(tags);
        vo.setCropPreference(profile.getCropPreference());
        vo.setTechPreference(profile.getTechPreference());
        vo.setRegionPreference(profile.getRegionPreference());
        vo.setProfileText(StringUtils.hasText(profile.getProfileText()) ? profile.getProfileText() : "暂无行为记录。");
        vo.setColdStart(tags.isEmpty());
        return vo;
    }

    @Override
    public Map<String, Double> parseInterestTags(String interestTags) {
        if (!StringUtils.hasText(interestTags)) return Collections.emptyMap();
        try { return objectMapper.readValue(interestTags, new TypeReference<LinkedHashMap<String, Double>>() {}); }
        catch (Exception e) { log.warn("parse tags failed", e); return Collections.emptyMap(); }
    }

    @Override
    public void refreshAllUserProfiles() {
        articleMapper.selectList(null).stream().map(KnowledgeArticle::getUserId).distinct()
            .forEach(uid -> { try { refreshProfile(uid); } catch (Exception e) { log.warn("refresh failed uid={}", uid, e); } });
    }

    private String joinByType(Map<String, Double> tags, String type) {
        if (tags.isEmpty()) return "";
        List<String> names = new ArrayList<>(tags.keySet());
        return tagMapper.selectList(new LambdaQueryWrapper<KnowledgeTag>().in(KnowledgeTag::getName, names).eq(KnowledgeTag::getTagType, type))
            .stream().map(KnowledgeTag::getName).filter(Objects::nonNull).collect(Collectors.joining("、"));
    }

    private String buildProfileText(Map<String, Double> topTags) {
        if (topTags.isEmpty()) return "该用户近期行为较少，暂未形成稳定兴趣画像。";
        List<String> names = topTags.keySet().stream().limit(4).collect(Collectors.toList());
        return "该用户近期主要关注" + String.join("、", names) + "等内容，偏好实用型农技文章。";
    }
}
