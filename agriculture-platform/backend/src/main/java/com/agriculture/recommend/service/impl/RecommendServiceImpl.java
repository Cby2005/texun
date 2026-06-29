package com.agriculture.recommend.service.impl;

import com.agriculture.agri.content.entity.AgriContent;
import com.agriculture.agri.content.mapper.AgriContentMapper;
import com.agriculture.common.result.PageResult;
import com.agriculture.recommend.dto.BehaviorReportDTO;
import com.agriculture.recommend.entity.UserBehaviorLog;
import com.agriculture.recommend.entity.UserInterestTag;
import com.agriculture.recommend.entity.UserProfile;
import com.agriculture.recommend.mapper.UserBehaviorLogMapper;
import com.agriculture.recommend.mapper.UserInterestTagMapper;
import com.agriculture.recommend.mapper.UserProfileMapper;
import com.agriculture.recommend.service.RecommendService;
import com.agriculture.recommend.vo.ContentRecommendVO;
import com.agriculture.recommend.vo.RecommendArticleVO;
import com.agriculture.recommend.vo.RecommendLogVO;
import com.agriculture.recommend.vo.UserProfileVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UserBehaviorLogMapper behaviorLogMapper;
    private final UserInterestTagMapper interestTagMapper;
    private final UserProfileMapper userProfileMapper;
    private final AgriContentMapper agriContentMapper;

    // 行为类型 -> 权重 映射
    private static final Map<String, Double> BEHAVIOR_WEIGHTS = new ConcurrentHashMap<>();
    static {
        BEHAVIOR_WEIGHTS.put("view", 1.0);
        BEHAVIOR_WEIGHTS.put("click", 2.0);
        BEHAVIOR_WEIGHTS.put("search", 2.0);
        BEHAVIOR_WEIGHTS.put("comment", 3.0);
        BEHAVIOR_WEIGHTS.put("like", 4.0);
        BEHAVIOR_WEIGHTS.put("collect", 5.0);
        BEHAVIOR_WEIGHTS.put("play_finish", 5.0);
    }

    // ==================== 行为上报 ====================

    @Override
    @Transactional
    public void reportBehavior(BehaviorReportDTO dto) {
        if (dto.getUserId() == null) {
            log.warn("行为上报缺少userId");
            return;
        }
        String behaviorType = dto.getBehaviorType();
        Double weight = BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, 1.0);

        // 1. 保存行为日志
        UserBehaviorLog log = new UserBehaviorLog();
        log.setUserId(dto.getUserId());
        log.setContentId(dto.getContentId());
        log.setBehaviorType(behaviorType);
        log.setKeyword(dto.getKeyword());
        log.setDuration(dto.getDuration() != null ? dto.getDuration() : 0);
        log.setCreateTime(LocalDateTime.now());
        behaviorLogMapper.insert(log);

        // 2. 如果是搜索行为，直接更新兴趣标签
        if ("search".equals(behaviorType) && dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            updateTagsForSearch(dto.getUserId(), dto.getKeyword(), weight);
            updateUserProfileActiveTime(dto.getUserId());
            return;
        }

        // 3. 如果有关联内容，更新内容统计 + 用户兴趣标签
        if (dto.getContentId() != null) {
            updateContentStats(dto.getContentId(), behaviorType);
            updateInterestTagsFromContent(dto.getUserId(), dto.getContentId(), weight);
        }

        updateUserProfileActiveTime(dto.getUserId());
    }

    private void updateContentStats(Long contentId, String behaviorType) {
        AgriContent content = agriContentMapper.selectById(contentId);
        if (content == null) return;

        boolean changed = false;
        switch (behaviorType) {
            case "view":
                content.setViewCount((content.getViewCount() == null ? 0 : content.getViewCount()) + 1);
                changed = true;
                break;
            case "like":
                content.setLikeCount((content.getLikeCount() == null ? 0 : content.getLikeCount()) + 1);
                changed = true;
                break;
            case "collect":
                content.setCollectCount((content.getCollectCount() == null ? 0 : content.getCollectCount()) + 1);
                changed = true;
                break;
            case "comment":
                content.setCommentCount((content.getCommentCount() == null ? 0 : content.getCommentCount()) + 1);
                changed = true;
                break;
        }
        if (changed) {
            content.setUpdateTime(LocalDateTime.now());
            agriContentMapper.updateById(content);
        }
    }

    private void updateInterestTagsFromContent(Long userId, Long contentId, Double weight) {
        AgriContent content = agriContentMapper.selectById(contentId);
        if (content == null) return;

        // 更新分类标签
        if (content.getCategory() != null && !content.getCategory().isBlank()) {
            interestTagMapper.increaseWeight(userId, content.getCategory(), "CATEGORY", weight);
        }

        // 更新内容标签
        if (content.getTags() != null && !content.getTags().isBlank()) {
            for (String tag : content.getTags().split(",")) {
                tag = tag.trim();
                if (!tag.isEmpty()) {
                    interestTagMapper.increaseWeight(userId, tag, "TAG", weight);
                }
            }
        }
    }

    private void updateTagsForSearch(Long userId, String keyword, Double weight) {
        // 按空格和逗号拆分搜索关键词
        String[] parts = keyword.split("[,\\s]+");
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                interestTagMapper.increaseWeight(userId, part, "KEYWORD", weight);
            }
        }
    }

    private void updateUserProfileActiveTime(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setCreateTime(LocalDateTime.now());
        }
        profile.setLastActiveTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }
    }

    // ==================== 推荐内容 ====================

    @Override
    public PageResult<ContentRecommendVO> recommendContent(Long userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;

        // 获取用户兴趣标签
        List<UserInterestTag> userTags = getSortedUserTags(userId);

        // 获取所有已发布内容
        List<AgriContent> allContent = agriContentMapper.selectList(
                new LambdaQueryWrapper<AgriContent>()
                        .eq(AgriContent::getPublishStatus, "PUBLISHED"));

        if (allContent.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
        }

        List<ContentRecommendVO> scoredList;

        if (userTags.isEmpty()) {
            // 冷启动：按热度排序，优先温室草莓相关内容
            scoredList = coldStartRecommend(allContent);
        } else {
            // 个性化推荐
            scoredList = personalizedRecommend(allContent, userTags);
        }

        // 分页
        int total = scoredList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= total) {
            return new PageResult<>(Collections.emptyList(), total, pageNum, pageSize);
        }
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<ContentRecommendVO> pageRecords = scoredList.subList(fromIndex, toIndex);

        return new PageResult<>(pageRecords, total, pageNum, pageSize);
    }

    private List<ContentRecommendVO> coldStartRecommend(List<AgriContent> allContent) {
        // 冷启动：草莓、温室、病虫害相关的优先，然后按热度评分
        List<String> defaultKeywords = Arrays.asList("草莓", "温室", "病虫害防治", "草莓种植");
        return allContent.stream().map(c -> {
            ContentRecommendVO vo = toVO(c);
            double tagBoost = 0;
            String title = c.getTitle() != null ? c.getTitle() : "";
            String category = c.getCategory() != null ? c.getCategory() : "";
            String tags = c.getTags() != null ? c.getTags() : "";
            for (String kw : defaultKeywords) {
                if (title.contains(kw) || category.contains(kw) || tags.contains(kw)) {
                    tagBoost += 5;
                }
            }
            double hotScore = calcHotScore(c);
            double freshScore = calcFreshScore(c.getPublishTime());
            vo.setRecommendScore(tagBoost * 0.5 + hotScore * 0.4 + freshScore * 0.1);
            return vo;
        }).sorted((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()))
                .collect(Collectors.toList());
    }

    private List<ContentRecommendVO> personalizedRecommend(List<AgriContent> allContent,
                                                            List<UserInterestTag> userTags) {
        // 构建用户兴趣权重Map
        Map<String, Double> tagWeightMap = new HashMap<>();
        Map<String, Double> categoryWeightMap = new HashMap<>();
        for (UserInterestTag ut : userTags) {
            if ("TAG".equals(ut.getTagType()) || "KEYWORD".equals(ut.getTagType())) {
                tagWeightMap.merge(ut.getTagName(), ut.getWeight(), Double::sum);
            } else if ("CATEGORY".equals(ut.getTagType())) {
                categoryWeightMap.put(ut.getTagName(), ut.getWeight());
            }
        }

        return allContent.stream().map(c -> {
            ContentRecommendVO vo = toVO(c);

            // 标签匹配分
            double tagScore = 0;
            String contentTags = c.getTags() != null ? c.getTags() : "";
            if (!contentTags.isBlank()) {
                for (String t : contentTags.split(",")) {
                    t = t.trim();
                    if (!t.isEmpty() && tagWeightMap.containsKey(t)) {
                        tagScore += tagWeightMap.get(t);
                    }
                }
            }
            // 分类匹配分
            String category = c.getCategory() != null ? c.getCategory() : "";
            if (!category.isBlank() && categoryWeightMap.containsKey(category)) {
                tagScore += categoryWeightMap.get(category) * 0.5;
            }

            double hotScore = calcHotScore(c);
            double freshScore = calcFreshScore(c.getPublishTime());

            // 最终推荐分数: tagScore * 0.6 + hotScore * 0.3 + freshScore * 0.1
            vo.setRecommendScore(tagScore * 0.6 + hotScore * 0.3 + freshScore * 0.1);
            return vo;
        }).sorted((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()))
                .collect(Collectors.toList());
    }

    private double calcHotScore(AgriContent c) {
        double v = (c.getViewCount() == null ? 0 : c.getViewCount()) * 0.01;
        v += (c.getLikeCount() == null ? 0 : c.getLikeCount()) * 0.5;
        v += (c.getCollectCount() == null ? 0 : c.getCollectCount()) * 1.0;
        v += (c.getCommentCount() == null ? 0 : c.getCommentCount()) * 0.3;
        return v;
    }

    private double calcFreshScore(LocalDateTime publishTime) {
        if (publishTime == null) return 1;
        long days = ChronoUnit.DAYS.between(publishTime, LocalDateTime.now());
        if (days <= 7) return 10;
        if (days <= 30) return 6;
        if (days <= 90) return 3;
        return 1;
    }

    private ContentRecommendVO toVO(AgriContent c) {
        ContentRecommendVO vo = new ContentRecommendVO();
        vo.setId(c.getId());
        vo.setTitle(c.getTitle());
        vo.setContentType(c.getContentType());
        vo.setCategory(c.getCategory());
        vo.setCoverUrl(c.getCoverUrl());
        vo.setSummary(c.getSummary());
        vo.setVideoUrl(c.getVideoUrl());
        vo.setVideoDuration(c.getVideoDuration());
        vo.setAuthor(c.getAuthor());
        vo.setSource(c.getSource());
        vo.setViewCount(c.getViewCount());
        vo.setLikeCount(c.getLikeCount());
        vo.setCollectCount(c.getCollectCount());
        vo.setCommentCount(c.getCommentCount());
        vo.setTags(c.getTags() != null && !c.getTags().isBlank()
                ? Arrays.asList(c.getTags().split(","))
                : Collections.emptyList());
        vo.setPublishTime(c.getPublishTime());
        vo.setRecommendScore(0.0);
        return vo;
    }

    // ==================== 相关技术 ====================

    @Override
    public List<ContentRecommendVO> similarContent(Long contentId, Integer pageSize) {
        if (contentId == null) return Collections.emptyList();
        if (pageSize == null) pageSize = 6;

        AgriContent current = agriContentMapper.selectById(contentId);
        if (current == null) return Collections.emptyList();

        String currentCategory = current.getCategory();
        String currentTags = current.getTags() != null ? current.getTags() : "";

        // 获取同分类或同标签的其他内容
        List<AgriContent> allContent = agriContentMapper.selectList(
                new LambdaQueryWrapper<AgriContent>()
                        .eq(AgriContent::getPublishStatus, "PUBLISHED")
                        .ne(AgriContent::getId, contentId));

        Set<String> currentTagSet = new HashSet<>();
        if (!currentTags.isBlank()) {
            for (String t : currentTags.split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) currentTagSet.add(trimmed);
            }
        }

        List<ContentRecommendVO> result = allContent.stream().map(c -> {
            ContentRecommendVO vo = toVO(c);
            double matchScore = 0;

            // 同分类加分
            if (currentCategory != null && currentCategory.equals(c.getCategory())) {
                matchScore += 5;
            }

            // 标签重叠加分
            String cTags = c.getTags() != null ? c.getTags() : "";
            if (!cTags.isBlank()) {
                for (String t : cTags.split(",")) {
                    if (currentTagSet.contains(t.trim())) {
                        matchScore += 3;
                    }
                }
            }

            double hotScore = calcHotScore(c);
            double freshScore = calcFreshScore(c.getPublishTime());
            vo.setRecommendScore(matchScore * 0.5 + hotScore * 0.3 + freshScore * 0.2);
            return vo;
        }).filter(vo -> vo.getRecommendScore() > 0)
                .sorted((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()))
                .limit(pageSize)
                .collect(Collectors.toList());

        return result;
    }

    // ==================== 用户画像 ====================

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        if (userId == null) return null;

        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);

        // 获取兴趣标签
        List<UserInterestTag> tags = getSortedUserTags(userId);
        if (tags.isEmpty()) {
            vo.setColdStart(true);
            vo.setInterestTags(Collections.emptyMap());
        } else {
            vo.setColdStart(false);
            Map<String, Double> tagMap = new LinkedHashMap<>();
            for (UserInterestTag t : tags) {
                tagMap.put(t.getTagName() + "[" + t.getTagType() + "]", t.getWeight());
            }
            vo.setInterestTags(tagMap);
        }

        // 获取画像汇总
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile != null) {
            vo.setCropPreference(profile.getCropTags());
            vo.setProfileText(profile.getProfileText());
        }

        return vo;
    }

    private List<UserInterestTag> getSortedUserTags(Long userId) {
        return interestTagMapper.selectList(
                new LambdaQueryWrapper<UserInterestTag>()
                        .eq(UserInterestTag::getUserId, userId)
                        .orderByDesc(UserInterestTag::getWeight));
    }

    // ==================== 兼容旧接口 ====================

    @Override
    public List<RecommendArticleVO> recommendArticles(Long userId, Integer limit) {
        PageResult<ContentRecommendVO> result = recommendContent(userId, 1, limit != null ? limit : 10);
        return result.getRecords().stream().map(c -> {
            RecommendArticleVO vo = new RecommendArticleVO();
            vo.setArticleId(c.getId());
            vo.setTitle(c.getTitle());
            vo.setSummary(c.getSummary());
            vo.setScore(c.getRecommendScore());
            vo.setSource(c.getSource());
            vo.setTags(c.getTags());
            vo.setPublishedAt(c.getPublishTime());
            vo.setStrategy("V1");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveRecommendLogs(Long userId, List<RecommendArticleVO> articles) {
        // 暂不实现，保留接口
    }

    @Override
    public PageResult<RecommendLogVO> listLogs(long page, long size, Long userId) {
        return new PageResult<>(Collections.emptyList(), 0);
    }
}
