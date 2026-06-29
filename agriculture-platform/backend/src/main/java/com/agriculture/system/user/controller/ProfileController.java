package com.agriculture.system.user.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.system.user.dto.*;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.service.ProfileService;
import com.agriculture.system.user.service.SysUserService;
import com.agriculture.system.user.vo.LoginLogVO;
import com.agriculture.system.user.vo.SecurityInfoVO;
import com.agriculture.system.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "个人中心与账号安全")
public class ProfileController {

    private final ProfileService profileService;
    private final SysUserService sysUserService;

    @GetMapping
    public Result<UserProfileVO> getProfile() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getProfile(userId));
    }

    @PutMapping
    public Result<UserProfileVO> updateProfile(@RequestBody UpdateProfileDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.updateProfile(userId, dto));
    }

    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getCurrentUserId();
        if (file.isEmpty()) return Result.fail(400, "请选择文件");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(jpeg|png)")) return Result.fail(400, "仅支持jpg/png格式");
        if (file.getSize() > 2 * 1024 * 1024) return Result.fail(400, "文件大小不能超过2MB");
        try {
            String url = profileService.uploadAvatar(userId, file.getBytes(), file.getOriginalFilename());
            return Result.ok("头像上传成功", url);
        } catch (Exception e) {
            return Result.fail(500, "上传失败: " + e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody UpdatePasswordDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        profileService.updatePassword(userId, dto);
        return Result.ok("密码修改成功，请重新登录");
    }

    @PostMapping("/phone/code")
    public Result<String> sendPhoneCode(@RequestBody PhoneCodeDTO dto) {
        String code = profileService.sendPhoneCode(dto.getPhone());
        return Result.ok("验证码已发送", code);
    }

    @PostMapping("/phone/bind")
    public Result<String> bindPhone(@RequestBody BindPhoneDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        profileService.bindPhone(userId, dto);
        return Result.ok("手机号绑定成功");
    }

    @PostMapping("/phone/change")
    public Result<String> changePhone(@RequestBody ChangePhoneDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        profileService.changePhone(userId, dto);
        return Result.ok("手机号换绑成功");
    }

    @PostMapping("/phone/unbind")
    public Result<String> unbindPhone(@RequestBody UnbindPhoneDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        profileService.unbindPhone(userId, dto);
        return Result.ok("手机号解绑成功");
    }

    @GetMapping("/security")
    public Result<SecurityInfoVO> getSecurityInfo() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getSecurityInfo(userId));
    }

    @GetMapping("/login-log")
    public Result<PageResult<LoginLogVO>> getLoginLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getLoginLogs(userId, pageNum, pageSize));
    }

    // ==================== 我的收藏 ====================

    @GetMapping("/favorites")
    public Result<PageResult<Map<String, Object>>> getFavorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getFavorites(userId, pageNum, pageSize));
    }

    @DeleteMapping("/favorites/{articleId}")
    public Result<String> removeFavorite(@PathVariable Long articleId) {
        Long userId = UserContext.getCurrentUserId();
        profileService.removeFavorite(userId, articleId);
        return Result.ok("已取消收藏");
    }

    // ==================== 我的评论 ====================

    @GetMapping("/comments")
    public Result<PageResult<Map<String, Object>>> getComments(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getComments(userId, pageNum, pageSize));
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getCurrentUserId();
        profileService.deleteComment(userId, commentId);
        return Result.ok("已删除评论");
    }

    // ==================== 行为统计 ====================

    @GetMapping("/behavior-stats")
    public Result<Map<String, Object>> getBehaviorStats() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getBehaviorStats(userId));
    }

    // ==================== 兴趣画像 ====================

    @GetMapping("/interest-tags")
    public Result<List<Map<String, Object>>> getInterestTags() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(profileService.getInterestTags(userId));
    }

    // ==================== 默认城市 ====================

    @GetMapping("/default-city")
    public Result<Map<String, String>> getDefaultCity() {
        Long userId = UserContext.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        Map<String, String> result = new java.util.HashMap<>();
        result.put("city", user.getDefaultCity());
        result.put("adcode", user.getDefaultAdcode());
        return Result.ok(result);
    }

    @PutMapping("/default-city")
    public Result<String> setDefaultCity(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getCurrentUserId();
        SysUser user = new SysUser();
        user.setId(userId);
        user.setDefaultCity(body.get("city"));
        user.setDefaultAdcode(body.get("adcode"));
        sysUserService.updateById(user);
        return Result.ok("默认城市已更新");
    }
}
