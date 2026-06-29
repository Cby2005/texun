package com.agriculture.recommend.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.recommend.dto.BehaviorReportDTO;
import com.agriculture.recommend.service.RecommendService;
import com.agriculture.recommend.vo.ContentRecommendVO;
import com.agriculture.recommend.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "农技内容推荐")
public class RecommendController {

    private final RecommendService recommendService;

    /** 行为上报 */
    @PostMapping("/behavior/report")
    @Operation(summary = "用户行为上报")
    public Result<Void> reportBehavior(@RequestBody BehaviorReportDTO dto) {
        recommendService.reportBehavior(dto);
        return Result.ok();
    }

    /** 农技精选推荐 */
    @GetMapping("/content")
    @Operation(summary = "获取农技精选内容（基于用户画像排序）")
    public Result<PageResult<ContentRecommendVO>> recommendContent(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(recommendService.recommendContent(userId, pageNum, pageSize));
    }

    /** 相关技术 */
    @GetMapping("/similar")
    @Operation(summary = "获取相关技术（同分类/同标签）")
    public Result<List<ContentRecommendVO>> similarContent(
            @RequestParam Long contentId,
            @RequestParam(defaultValue = "6") Integer pageSize) {
        return Result.ok(recommendService.similarContent(contentId, pageSize));
    }

    /** 用户画像 */
    @GetMapping("/profile")
    @Operation(summary = "获取用户画像（管理员/个人中心使用）")
    public Result<UserProfileVO> getUserProfile(@RequestParam Long userId) {
        return Result.ok(recommendService.getUserProfile(userId));
    }
}
