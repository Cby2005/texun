package com.agriculture.recommend.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.recommend.service.RecommendService;
import com.agriculture.recommend.vo.RecommendArticleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "个性化推荐")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/articles")
    @Operation(summary = "获取推荐文章")
    public Result<List<RecommendArticleVO>> recommend(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long uid = userId != null ? userId : UserContext.getCurrentUserId();
        return Result.ok(recommendService.recommendArticles(uid, limit));
    }
}
