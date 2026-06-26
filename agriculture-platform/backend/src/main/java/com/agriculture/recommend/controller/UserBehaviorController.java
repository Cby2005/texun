package com.agriculture.recommend.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.recommend.dto.BehaviorRecordDTO;
import com.agriculture.recommend.entity.UserBehavior;
import com.agriculture.recommend.mapper.UserBehaviorMapper;
import com.agriculture.recommend.vo.TagVO;
import com.agriculture.recommend.service.TagExtractService;
import com.agriculture.recommend.vo.UserProfileVO;
import com.agriculture.recommend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
@Tag(name = "用户行为")
public class UserBehaviorController {

    private final UserBehaviorMapper behaviorMapper;
    private final UserProfileService userProfileService;
    private final TagExtractService tagExtractService;

    @PostMapping("/record")
    @Operation(summary = "记录用户行为")
    public Result<Void> record(@RequestBody BehaviorRecordDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail("未登录");
        UserBehavior b = new UserBehavior();
        b.setUserId(userId); b.setArticleId(dto.getArticleId()); b.setVideoId(dto.getVideoId());
        b.setTargetType(dto.getTargetType() != null ? dto.getTargetType() : "ARTICLE");
        b.setBehaviorType(dto.getBehaviorType()); b.setStaySeconds(dto.getStaySeconds());
        b.setWeight(getWeight(dto.getBehaviorType()));
        behaviorMapper.insert(b);
        return Result.ok();
    }

    private double getWeight(String type) {
        return switch (type) {
            case "like" -> 1.5; case "collect" -> 2.0; case "comment" -> 1.8;
            case "question" -> 2.5; case "dislike" -> -0.5; default -> 1.0;
        };
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户画像")
    public Result<UserProfileVO> profile() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(userProfileService.getProfileVO(userId));
    }

    @PostMapping("/profile/refresh")
    @Operation(summary = "刷新用户画像")
    public Result<Void> refreshProfile() {
        userProfileService.refreshProfile(UserContext.getCurrentUserId());
        return Result.ok();
    }

    @GetMapping("/tags")
    @Operation(summary = "获取标签列表")
    public Result<List<TagVO>> tags() { return Result.ok(null); }

    @PostMapping("/tags/extract/{articleId}")
    @Operation(summary = "提取文章标签")
    public Result<List<TagVO>> extract(@PathVariable Long articleId) {
        return Result.ok(tagExtractService.extractTags(articleId));
    }
}
