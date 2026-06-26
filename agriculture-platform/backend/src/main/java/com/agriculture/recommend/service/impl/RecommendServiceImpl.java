package com.agriculture.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.agriculture.common.result.PageResult;
import com.agriculture.knowledge.article.entity.KnowledgeArticle;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper;
import com.agriculture.knowledge.article_tag.entity.KnowledgeArticleTag;
import com.agriculture.knowledge.article_tag.mapper.KnowledgeArticleTagMapper;
import com.agriculture.knowledge.tag.entity.KnowledgeTag;
import com.agriculture.knowledge.tag.mapper.KnowledgeTagMapper;
import com.agriculture.recommend.entity.RecommendLog;
import com.agriculture.recommend.entity.UserBehavior;
import com.agriculture.recommend.entity.UserProfile;
import com.agriculture.recommend.mapper.RecommendLogMapper;
import com.agriculture.recommend.mapper.UserBehaviorMapper;
import com.agriculture.recommend.service.RecommendService;
import com.agriculture.recommend.service.UserProfileService;
import com.agriculture.recommend.vo.RecommendArticleVO;
import com.agriculture.recommend.vo.RecommendLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UserProfileService userProfileService;
    private final KnowledgeArticleMapper articleMapper;
    private final KnowledgeArticleTagMapper articleTagMapper;
    private final KnowledgeTagMapper tagMapper;
    private final UserBehaviorMapper behaviorMapper;
    private final RecommendLogMapper recommendLogMapper;

    @Override
    @Transactional
    public List<RecommendArticleVO> recommendArticles(Long userId, Integer limit) {
        int size = normalizeLimit(limit);
        UserProfile profile = userProfileService.getOrBuildProfile(userId);
        Map<String, Double> interests = profile == null ? Collections.emptyMap() : userProfileService.parseInterestTags(profile.getInterestTags());
        if (interests.isEmpty()) return coldStartRecommend(userId, size);

        List<KnowledgeArticle> candidates = selectCandidates(userId, Math.max(size * 5, 30));
        List<RecommendArticleVO> result = new ArrayList<>();
        for (KnowledgeArticle a : candidates) {
            Map<String, Double> articleTags = getArticleTagWeights(a.getId());
            double finalScore = 0.35 * calcTagMatch(interests, articleTags)
                + 0.20 * calcBehaviorSim(userId, articleTags)
                + 0.15 * calcHot(a) + 0.10 * calcFresh(a) + 0.10 * calcTrust(a) + 0.10 * calcDiversity(a, result);
            RecommendArticleVO vo = toVO(a, new ArrayList<>(articleTags.keySet()));
            vo.setScore(round(finalScore));
            vo.setReason(buildReason(interests, articleTags, a));
            vo.setStrategy("tag_match");
            result.add(vo);
        }
        List<RecommendArticleVO> top = result.stream().sorted(Comparator.comparing(RecommendArticleVO::getScore).reversed())
            .limit(size).collect(Collectors.toList());
        saveRecommendLogs(userId, top);
        return top;
    }

    @Override
    public void saveRecommendLogs(Long userId, List<RecommendArticleVO> articles) {
        if (userId == null || articles == null || articles.isEmpty()) return;
        articles.forEach(a -> {
            RecommendLog log = new RecommendLog();
            log.setUserId(userId); log.setArticleId(a.getArticleId());
            log.setScore(a.getScore()); log.setReason(a.getReason()); log.setStrategy(a.getStrategy());
            recommendLogMapper.insert(log);
        });
    }

    @Override
    public PageResult<RecommendLogVO> listLogs(long page, long size, Long userId) {
        Page<RecommendLog> pg = new Page<>(page, size);
        LambdaQueryWrapper<RecommendLog> w = new LambdaQueryWrapper<>();
        if (userId != null) w.eq(RecommendLog::getUserId, userId);
        w.orderByDesc(RecommendLog::getCreateTime);
        Page<RecommendLog> logs = recommendLogMapper.selectPage(pg, w);
        List<Long> aids = logs.getRecords().stream().map(RecommendLog::getArticleId).distinct().collect(Collectors.toList());
        Map<Long, KnowledgeArticle> am = aids.isEmpty() ? Collections.emptyMap()
            : articleMapper.selectBatchIds(aids).stream().collect(Collectors.toMap(KnowledgeArticle::getId, a -> a));
        List<RecommendLogVO> records = logs.getRecords().stream().map(l -> {
            RecommendLogVO vo = new RecommendLogVO();
            vo.setId(l.getId()); vo.setUserId(l.getUserId()); vo.setArticleId(l.getArticleId());
            KnowledgeArticle art = am.get(l.getArticleId());
            vo.setArticleTitle(art != null ? art.getTitle() : "");
            vo.setScore(l.getScore()); vo.setReason(l.getReason()); vo.setStrategy(l.getStrategy());
            vo.setCreateTime(l.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(records, logs.getTotal(), (int) logs.getCurrent(), (int) logs.getSize());
    }

    private List<RecommendArticleVO> coldStartRecommend(Long userId, int size) {
        Set<Long> disliked = getBehaviorIds(userId, "dislike");
        LambdaQueryWrapper<KnowledgeArticle> w = new LambdaQueryWrapper<KnowledgeArticle>()
            .eq(KnowledgeArticle::getStatus, "PUBLISHED");
        if (!disliked.isEmpty()) w.notIn(KnowledgeArticle::getId, disliked);
        w.orderByDesc(KnowledgeArticle::getPublishedAt).orderByDesc(KnowledgeArticle::getViewCount).last("LIMIT " + size);
        List<KnowledgeArticle> articles = articleMapper.selectList(w);
        return articles.stream().map(a -> {
            RecommendArticleVO vo = toVO(a, Collections.emptyList());
            vo.setScore(round(0.45 + 0.25 * calcTrust(a) + 0.2 * calcFresh(a) + 0.1 * calcHot(a)));
            vo.setReason("你当前浏览记录较少，系统优先为你推荐近期热门且经过审核的农技文章。");
            vo.setStrategy("cold_start");
            return vo;
        }).collect(Collectors.toList());
    }

    private List<KnowledgeArticle> selectCandidates(Long userId, int limit) {
        Set<Long> disliked = getBehaviorIds(userId, "dislike");
        Set<Long> viewed = getBehaviorIds(userId, "view");
        LambdaQueryWrapper<KnowledgeArticle> w = new LambdaQueryWrapper<>();
        w.eq(KnowledgeArticle::getStatus, "PUBLISHED");
        if (!disliked.isEmpty()) w.notIn(KnowledgeArticle::getId, disliked);
        if (!viewed.isEmpty()) w.notIn(KnowledgeArticle::getId, viewed);
        w.orderByDesc(KnowledgeArticle::getPublishedAt).last("LIMIT " + limit);
        return articleMapper.selectList(w);
    }

    private Set<Long> getBehaviorIds(Long userId, String type) {
        if (userId == null) return Collections.emptySet();
        return behaviorMapper.selectList(new LambdaQueryWrapper<UserBehavior>()
            .eq(UserBehavior::getUserId, userId).eq(UserBehavior::getBehaviorType, type))
            .stream().map(UserBehavior::getArticleId).collect(Collectors.toSet());
    }

    private Map<String, Double> getArticleTagWeights(Long articleId) {
        List<KnowledgeArticleTag> rels = articleTagMapper.selectList(
            new LambdaQueryWrapper<KnowledgeArticleTag>().eq(KnowledgeArticleTag::getArticleId, articleId));
        if (rels.isEmpty()) return Collections.emptyMap();
        List<Long> tids = rels.stream().map(KnowledgeArticleTag::getTagId).collect(Collectors.toList());
        Map<Long, KnowledgeTag> tm = tagMapper.selectBatchIds(tids).stream()
            .collect(Collectors.toMap(KnowledgeTag::getId, t -> t));
        Map<String, Double> result = new LinkedHashMap<>();
        rels.forEach(r -> {
            KnowledgeTag t = tm.get(r.getTagId());
            if (t != null) result.put(t.getName(), r.getWeight() != null ? r.getWeight() : 1.0);
        });
        return result;
    }

    private double calcTagMatch(Map<String, Double> interests, Map<String, Double> articleTags) {
        double total = interests.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0 || articleTags.isEmpty()) return 0;
        double hit = articleTags.keySet().stream().mapToDouble(t -> interests.getOrDefault(t, 0.0)).sum();
        return clamp(hit / total);
    }

    private double calcBehaviorSim(Long userId, Map<String, Double> articleTags) {
        if (userId == null || articleTags.isEmpty()) return 0;
        List<UserBehavior> behaviors = behaviorMapper.selectList(new LambdaQueryWrapper<UserBehavior>()
            .eq(UserBehavior::getUserId, userId)
            .in(UserBehavior::getBehaviorType, List.of("view", "like", "collect", "comment", "question"))
            .orderByDesc(UserBehavior::getCreateTime).last("LIMIT 30"));
        if (behaviors.isEmpty()) return 0;
        int hit = 0;
        for (UserBehavior b : behaviors) {
            if (getArticleTagWeights(b.getArticleId()).keySet().stream().anyMatch(articleTags::containsKey)) hit++;
        }
        return clamp((double) hit / Math.min(behaviors.size(), 10));
    }

    private double calcHot(KnowledgeArticle a) {
        return clamp((safe(a.getViewCount()) * 0.2 + safe(a.getLikeCount()) * 0.3 + safe(a.getFavoriteCount()) * 0.5) / 100.0);
    }

    private double calcFresh(KnowledgeArticle a) {
        LocalDateTime t = a.getPublishedAt() != null ? a.getPublishedAt() : a.getCreateTime();
        if (t == null) return 0.2;
        long days = Duration.between(t, LocalDateTime.now()).toDays();
        if (days <= 7) return 1.0; if (days <= 30) return 0.7; if (days <= 90) return 0.4;
        return 0.2;
    }

    private double calcTrust(KnowledgeArticle a) {
        if ("official".equalsIgnoreCase(a.getTrustedLevel())) return 1.0;
        if ("expert".equalsIgnoreCase(a.getTrustedLevel())) return 0.8;
        return 0.5;
    }

    private double calcDiversity(KnowledgeArticle a, List<RecommendArticleVO> selected) {
        String crop = StringUtils.hasText(a.getCropType()) ? a.getCropType() : "UNKNOWN";
        long same = selected.stream().filter(v -> Objects.equals(crop, v.getCropType())).count();
        if (same >= 3) return 0.2; if (same == 2) return 0.5; if (same == 1) return 0.8;
        return 1.0;
    }

    private String buildReason(Map<String, Double> interests, Map<String, Double> articleTags, KnowledgeArticle a) {
        List<String> focus = interests.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(2).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> matched = articleTags.keySet().stream().filter(interests::containsKey).limit(3).collect(Collectors.toList());
        if (matched.isEmpty()) matched = articleTags.keySet().stream().limit(3).collect(Collectors.toList());
        return "你近期关注了" + String.join("、", focus) + "等内容，该文章涉及"
            + (matched.isEmpty() ? "农业技术" : String.join("、", matched)) + "，与您的兴趣较匹配。";
    }

    private RecommendArticleVO toVO(KnowledgeArticle a, List<String> tags) {
        RecommendArticleVO vo = new RecommendArticleVO();
        vo.setArticleId(a.getId()); vo.setTitle(a.getTitle()); vo.setSummary(a.getSummary());
        vo.setTrustedLevel(a.getTrustedLevel()); vo.setSource(a.getSource()); vo.setSourceUrl(a.getSourceUrl());
        vo.setCropType(a.getCropType()); vo.setTags(tags); vo.setPublishedAt(a.getPublishedAt());
        return vo;
    }

    private int normalizeLimit(Integer limit) { return limit == null || limit <= 0 ? 10 : Math.min(limit, 30); }
    private int safe(Integer v) { return v == null ? 0 : v; }
    private double clamp(double v) { return Math.max(0, Math.min(1, v)); }
    private double round(double s) { return Math.round(s * 10000.0) / 10000.0; }
}
