package com.agriculture.knowledge.article.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.knowledge.article.dto.ArticleCommentRequest;
import com.agriculture.knowledge.article.entity.KnowledgeArticle;
import com.agriculture.knowledge.article.entity.KnowledgeArticleComment;
import com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite;
import com.agriculture.knowledge.article.entity.KnowledgeArticleLike;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleCommentMapper;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleFavoriteMapper;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleLikeMapper;
import com.agriculture.recommend.entity.UserBehavior;
import com.agriculture.recommend.mapper.UserBehaviorMapper;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/articles")
@RequiredArgsConstructor
@Tag(name = "农技文章")
public class KnowledgeArticleController {

    private final IService<KnowledgeArticle> service;
    private final KnowledgeArticleCommentMapper commentMapper;
    private final KnowledgeArticleLikeMapper likeMapper;
    private final KnowledgeArticleFavoriteMapper favoriteMapper;
    private final UserBehaviorMapper behaviorMapper;
    private final SysUserMapper userMapper;

    @GetMapping
    public Result<PageResult<KnowledgeArticle>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<KnowledgeArticle> qw = new LambdaQueryWrapper<KnowledgeArticle>()
                .eq(KnowledgeArticle::getStatus, "PUBLISHED");
        if (categoryId != null) qw.eq(KnowledgeArticle::getCategoryId, categoryId);
        if (cropId != null) qw.eq(KnowledgeArticle::getCropId, cropId);
        if (keyword != null && !keyword.isEmpty()) qw.like(KnowledgeArticle::getTitle, keyword);
        qw.orderByDesc(KnowledgeArticle::getCreateTime);
        Page<KnowledgeArticle> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeArticle> getById(@PathVariable Long id) {
        KnowledgeArticle article = service.getById(id);
        if (article != null) {
            article.setViewCount(safeCount(article.getViewCount()) + 1);
            service.updateById(article);
        }
        return Result.ok(article);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> add(@RequestBody KnowledgeArticle article) {
        article.setUserId(UserContext.getCurrentUserId());
        article.setStatus("DRAFT");
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setFavoriteCount(0);
        service.save(article);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeArticle article) {
        article.setId(id);
        service.updateById(article);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> audit(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String remark) {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(id);
        article.setStatus(status);
        article.setRejectReason(remark);
        service.updateById(article);
        return Result.ok();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<PageResult<KnowledgeArticle>> pending(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<KnowledgeArticle> qw = new LambdaQueryWrapper<KnowledgeArticle>()
                .eq(KnowledgeArticle::getStatus, "PENDING_AUDIT")
                .orderByAsc(KnowledgeArticle::getCreateTime);
        Page<KnowledgeArticle> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/my/likes")
    public Result<PageResult<Map<String, Object>>> myLikes(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        Page<KnowledgeArticleLike> page = likeMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<KnowledgeArticleLike>()
                        .eq(KnowledgeArticleLike::getUserId, userId)
                        .eq(KnowledgeArticleLike::getDeleted, 0)
                        .orderByDesc(KnowledgeArticleLike::getCreateTime));
        List<Map<String, Object>> records = buildInteractionRecords(page.getRecords(), "likedAt");
        return Result.ok(new PageResult<>(records, page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/my/favorites")
    public Result<PageResult<Map<String, Object>>> myFavorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        Page<KnowledgeArticleFavorite> page = favoriteMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<KnowledgeArticleFavorite>()
                        .eq(KnowledgeArticleFavorite::getUserId, userId)
                        .eq(KnowledgeArticleFavorite::getDeleted, 0)
                        .orderByDesc(KnowledgeArticleFavorite::getCreateTime));
        List<Map<String, Object>> records = buildInteractionRecords(page.getRecords(), "favoritedAt");
        return Result.ok(new PageResult<>(records, page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}/interaction")
    public Result<Map<String, Object>> interaction(@PathVariable Long id) {
        KnowledgeArticle article = service.getById(id);
        Long userId = UserContext.getCurrentUserId();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("likeCount", article == null ? 0 : safeCount(article.getLikeCount()));
        data.put("favoriteCount", article == null ? 0 : safeCount(article.getFavoriteCount()));
        data.put("commentCount", article == null ? 0 : safeCount(article.getCommentCount()));
        data.put("liked", userId != null && existsLike(id, userId));
        data.put("favorited", userId != null && existsFavorite(id, userId));
        return Result.ok(data);
    }

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        KnowledgeArticle article = service.getById(id);
        if (article == null) return Result.fail("文章不存在");
        if (!existsLike(id, userId)) {
            KnowledgeArticleLike like = new KnowledgeArticleLike();
            like.setArticleId(id);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            like.setDeleted(0);
            likeMapper.insert(like);
            article.setLikeCount(safeCount(article.getLikeCount()) + 1);
            service.updateById(article);
            recordBehavior(id, userId, "like", 1.5);
        }
        return Result.ok();
    }

    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        int removed = likeMapper.delete(new LambdaQueryWrapper<KnowledgeArticleLike>()
                .eq(KnowledgeArticleLike::getArticleId, id)
                .eq(KnowledgeArticleLike::getUserId, userId));
        if (removed > 0) {
            KnowledgeArticle article = service.getById(id);
            if (article != null) {
                article.setLikeCount(Math.max(safeCount(article.getLikeCount()) - 1, 0));
                service.updateById(article);
            }
        }
        return Result.ok();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        KnowledgeArticle article = service.getById(id);
        if (article == null) return Result.fail("文章不存在");
        if (!existsFavorite(id, userId)) {
            KnowledgeArticleFavorite favorite = new KnowledgeArticleFavorite();
            favorite.setArticleId(id);
            favorite.setUserId(userId);
            favorite.setCreateTime(LocalDateTime.now());
            favorite.setDeleted(0);
            favoriteMapper.insert(favorite);
            article.setFavoriteCount(safeCount(article.getFavoriteCount()) + 1);
            service.updateById(article);
            recordBehavior(id, userId, "collect", 2.0);
        }
        return Result.ok();
    }

    @PostMapping("/{id}/cancel-favorite")
    public Result<Void> cancelFavorite(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        int removed = favoriteMapper.delete(new LambdaQueryWrapper<KnowledgeArticleFavorite>()
                .eq(KnowledgeArticleFavorite::getArticleId, id)
                .eq(KnowledgeArticleFavorite::getUserId, userId));
        if (removed > 0) {
            KnowledgeArticle article = service.getById(id);
            if (article != null) {
                article.setFavoriteCount(Math.max(safeCount(article.getFavoriteCount()) - 1, 0));
                service.updateById(article);
            }
        }
        return Result.ok();
    }

    @GetMapping("/{id}/comments")
    public Result<List<Map<String, Object>>> comments(@PathVariable Long id) {
        List<KnowledgeArticleComment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<KnowledgeArticleComment>()
                        .eq(KnowledgeArticleComment::getArticleId, id)
                        .eq(KnowledgeArticleComment::getStatus, 0)
                        .eq(KnowledgeArticleComment::getDeleted, 0)
                        .orderByDesc(KnowledgeArticleComment::getCreateTime)
                        .last("LIMIT 100"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (KnowledgeArticleComment comment : comments) {
            SysUser user = userMapper.selectById(comment.getUserId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", comment.getId());
            item.put("articleId", comment.getArticleId());
            item.put("userId", comment.getUserId());
            item.put("nickname", user == null ? "用户" + comment.getUserId() : resolveNickname(user));
            item.put("avatar", user == null ? "" : user.getAvatar());
            item.put("content", comment.getContent());
            item.put("createTime", comment.getCreateTime());
            result.add(item);
        }
        return Result.ok(result);
    }

    @PostMapping("/{id}/comments")
    public Result<Void> createComment(@PathVariable Long id, @RequestBody ArticleCommentRequest request) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        KnowledgeArticle article = service.getById(id);
        if (article == null) return Result.fail("文章不存在");
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("评论内容不能为空");
        }
        String content = request.getContent().trim();
        if (content.length() > 1000) {
            return Result.fail("评论内容不能超过 1000 字");
        }

        KnowledgeArticleComment comment = new KnowledgeArticleComment();
        comment.setArticleId(id);
        comment.setUserId(userId);
        comment.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        comment.setContent(content);
        comment.setStatus(0);
        comment.setDeleted(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        article.setCommentCount(safeCount(article.getCommentCount()) + 1);
        service.updateById(article);
        return Result.ok();
    }

    private List<Map<String, Object>> buildInteractionRecords(List<?> interactions, String timeKey) {
        List<Map<String, Object>> records = new ArrayList<>();
        for (Object interaction : interactions) {
            Long articleId;
            LocalDateTime createTime;
            if (interaction instanceof KnowledgeArticleLike like) {
                articleId = like.getArticleId();
                createTime = like.getCreateTime();
            } else if (interaction instanceof KnowledgeArticleFavorite favorite) {
                articleId = favorite.getArticleId();
                createTime = favorite.getCreateTime();
            } else {
                continue;
            }

            KnowledgeArticle article = service.getById(articleId);
            if (article == null) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("article", article);
            item.put(timeKey, createTime);
            records.add(item);
        }
        return records;
    }

    private void recordBehavior(Long articleId, Long userId, String behaviorType, double weight) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setArticleId(articleId);
            behavior.setTargetType("ARTICLE");
            behavior.setBehaviorType(behaviorType);
            behavior.setWeight(weight);
            behavior.setCreateTime(LocalDateTime.now());
            behavior.setUpdateTime(LocalDateTime.now());
            behavior.setDeleted(0);
            behaviorMapper.insert(behavior);
        } catch (RuntimeException ignored) {
            // Behavior records are used for recommendation only; article interaction must not fail because of them.
        }
    }

    private boolean existsLike(Long articleId, Long userId) {
        return likeMapper.selectCount(new LambdaQueryWrapper<KnowledgeArticleLike>()
                .eq(KnowledgeArticleLike::getArticleId, articleId)
                .eq(KnowledgeArticleLike::getUserId, userId)
                .eq(KnowledgeArticleLike::getDeleted, 0)) > 0;
    }

    private boolean existsFavorite(Long articleId, Long userId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<KnowledgeArticleFavorite>()
                .eq(KnowledgeArticleFavorite::getArticleId, articleId)
                .eq(KnowledgeArticleFavorite::getUserId, userId)
                .eq(KnowledgeArticleFavorite::getDeleted, 0)) > 0;
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }

    private String resolveNickname(SysUser user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}
