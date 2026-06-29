package com.agriculture.system.user.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.common.result.PageResult;
import com.agriculture.system.role.entity.SysRole;
import com.agriculture.system.role.mapper.SysRoleMapper;
import com.agriculture.system.user.dto.*;
import com.agriculture.system.user.entity.SysLoginLog;
import com.agriculture.system.user.entity.SysPhoneVerifyCode;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.mapper.SysLoginLogMapper;
import com.agriculture.system.user.mapper.SysPhoneVerifyCodeMapper;
import com.agriculture.system.user.mapper.SysUserMapper;
import com.agriculture.system.user.service.ProfileService;
import com.agriculture.system.user.vo.LoginLogVO;
import com.agriculture.system.user.vo.SecurityInfoVO;
import com.agriculture.system.user.vo.UserProfileVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final SysPhoneVerifyCodeMapper verifyCodeMapper;
    private final PasswordEncoder passwordEncoder;

    // 农技内容 Mapper（收藏/评论/行为/画像）
    private final com.agriculture.knowledge.article.mapper.KnowledgeArticleFavoriteMapper favoriteMapper;
    private final com.agriculture.knowledge.article.mapper.KnowledgeArticleCommentMapper commentMapper;
    private final com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper articleMapper;
    private final com.agriculture.recommend.mapper.UserBehaviorLogMapper behaviorLogMapper;
    private final com.agriculture.recommend.mapper.UserBehaviorMapper behaviorMapper;
    private final com.agriculture.recommend.mapper.UserInterestTagMapper interestTagMapper;

    @Value("${app.file.base-path:./runtime/files}")
    private String fileBasePath;

    @Override
    public UserProfileVO getProfile(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");
        return toProfileVO(user);
    }

    @Override
    @Transactional
    public UserProfileVO updateProfile(Long userId, UpdateProfileDTO dto) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");

        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getProfile() != null) user.setProfile(dto.getProfile());

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return toProfileVO(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");
        if (dto.getOldPassword() == null || dto.getOldPassword().isBlank()) throw new BizException(400, "原密码不能为空");
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) throw new BizException(400, "新密码长度不少于6位");
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) throw new BizException(400, "两次密码不一致");
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) throw new BizException(400, "原密码错误");
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) throw new BizException(400, "新密码不能与原密码相同");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public String uploadAvatar(Long userId, byte[] fileBytes, String originalName) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");

        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".png";
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path dir = Paths.get(fileBasePath, "avatar");
        try {
            Files.createDirectories(dir);
            Path filePath = dir.resolve(fileName);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(fileBytes);
            }
            String avatarUrl = "/files/avatar/" + fileName;
            user.setAvatar(avatarUrl);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            return avatarUrl;
        } catch (Exception e) {
            throw new BizException(500, "头像上传失败: " + e.getMessage());
        }
    }

    @Override
    public String sendPhoneCode(String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) throw new BizException(400, "手机号格式不正确");

        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);

        SysPhoneVerifyCode vc = new SysPhoneVerifyCode();
        vc.setPhone(phone);
        vc.setCode(code);
        vc.setUsed(0);
        vc.setExpireTime(expireTime);
        vc.setCreateTime(LocalDateTime.now());
        verifyCodeMapper.insert(vc);

        // 开发环境返回验证码
        return code;
    }

    @Override
    @Transactional
    public void bindPhone(Long userId, BindPhoneDTO dto) {
        validateCode(dto.getPhone(), dto.getCode());
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");
        // 检查手机号是否已被其他用户绑定
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUser::getPhone, dto.getPhone()).ne(SysUser::getId, userId);
        if (userMapper.selectCount(qw) > 0) throw new BizException(400, "该手机号已被其他用户绑定");

        user.setPhone(dto.getPhone());
        user.setPhoneBindTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePhone(Long userId, ChangePhoneDTO dto) {
        validateCode(dto.getNewPhone(), dto.getCode());
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");
        if (user.getPhone() == null) throw new BizException(400, "还未绑定手机号，请先绑定");

        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUser::getPhone, dto.getNewPhone()).ne(SysUser::getId, userId);
        if (userMapper.selectCount(qw) > 0) throw new BizException(400, "该手机号已被其他用户绑定");

        user.setPhone(dto.getNewPhone());
        user.setPhoneBindTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void unbindPhone(Long userId, UnbindPhoneDTO dto) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");
        if (dto.getPassword() == null && dto.getCode() == null) throw new BizException(400, "需要输入密码或验证码验证");

        if (dto.getPassword() != null) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new BizException(400, "密码错误");
        } else if (dto.getCode() != null) {
            validateCode(user.getPhone(), dto.getCode());
        }

        user.setPhone(null);
        user.setPhoneBindTime(null);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public SecurityInfoVO getSecurityInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BizException(400, "用户不存在");

        SecurityInfoVO vo = new SecurityInfoVO();
        vo.setPhoneBound(user.getPhone() != null && !user.getPhone().isBlank());
        vo.setEmailFilled(user.getEmail() != null && !user.getEmail().isBlank());
        vo.setPasswordUpdateTime(user.getPasswordUpdateTime());
        vo.setLastLoginTime(user.getLastLoginTime());

        if (user.getPhone() != null && user.getPhone().length() >= 11) {
            vo.setMaskedPhone(user.getPhone().substring(0, 3) + "****" + user.getPhone().substring(7));
        }

        // 安全等级
        boolean recentlyUpdatedPwd = user.getPasswordUpdateTime() != null
                && ChronoUnit.DAYS.between(user.getPasswordUpdateTime(), LocalDateTime.now()) <= 30;
        if (vo.getPhoneBound() && vo.getEmailFilled() && recentlyUpdatedPwd) {
            vo.setSecurityLevel("HIGH");
        } else if (!vo.getPhoneBound()) {
            vo.setSecurityLevel("LOW");
        } else {
            vo.setSecurityLevel("MIDDLE");
        }
        return vo;
    }

    @Override
    public PageResult<LoginLogVO> getLoginLogs(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<SysLoginLog> qw = new LambdaQueryWrapper<>();
        qw.eq(SysLoginLog::getUserId, userId).orderByDesc(SysLoginLog::getLoginTime);
        Page<SysLoginLog> page = loginLogMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        List<LoginLogVO> vos = page.getRecords().stream().map(log -> {
            LoginLogVO vo = new LoginLogVO();
            vo.setId(log.getId());
            vo.setUsername(log.getUsername());
            vo.setLoginIp(log.getLoginIp());
            vo.setLoginLocation(log.getLoginLocation());
            vo.setBrowser(log.getBrowser());
            vo.setOs(log.getOs());
            vo.setLoginStatus(log.getLoginStatus());
            vo.setLoginTime(log.getLoginTime());
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(vos, page.getTotal(), pageNum, pageSize);
    }

    private void validateCode(String phone, String code) {
        LambdaQueryWrapper<SysPhoneVerifyCode> qw = new LambdaQueryWrapper<>();
        qw.eq(SysPhoneVerifyCode::getPhone, phone)
          .eq(SysPhoneVerifyCode::getCode, code)
          .eq(SysPhoneVerifyCode::getUsed, 0)
          .gt(SysPhoneVerifyCode::getExpireTime, LocalDateTime.now())
          .orderByDesc(SysPhoneVerifyCode::getCreateTime)
          .last("LIMIT 1");
        SysPhoneVerifyCode vc = verifyCodeMapper.selectOne(qw);
        if (vc == null) throw new BizException(400, "验证码错误或已过期");

        vc.setUsed(1);
        verifyCodeMapper.updateById(vc);
    }

    private UserProfileVO toProfileVO(SysUser user) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setProfile(user.getProfile());
        vo.setPhoneBound(user.getPhone() != null && !user.getPhone().isBlank());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setPasswordUpdateTime(user.getPasswordUpdateTime());

        String[] typeNames = {"管理员", "农场管理员", "溯源企业", "专家", "农户", "消费者"};
        int ut = user.getUserType() != null ? user.getUserType() : 4;
        vo.setUserType(ut >= 0 && ut < typeNames.length ? typeNames[ut] : "未知");

        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getId());
        vo.setRoleName(roles.isEmpty() ? "普通用户" : roles.get(0).getName());
        return vo;
    }

    // ==================== 收藏 / 评论 / 行为 / 画像 ====================

    @Override
    public PageResult<Map<String, Object>> getFavorites(Long userId, int pageNum, int pageSize) {
        var qw = new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite::getUserId, userId);
        var page = favoriteMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        List<Map<String, Object>> list = page.getRecords().stream().map(fav -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", fav.getId()); m.put("articleId", fav.getArticleId()); m.put("createTime", fav.getCreateTime());
            try {
                var article = articleMapper.selectById(fav.getArticleId());
                if (article != null) { m.put("title", article.getTitle()); m.put("cropType", article.getCropType()); m.put("viewCount", article.getViewCount()); }
            } catch (Exception ignored) {}
            return m;
        }).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public void removeFavorite(Long userId, Long articleId) {
        var qw = new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite::getUserId, userId)
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite::getArticleId, articleId);
        favoriteMapper.delete(qw);
    }

    @Override
    public PageResult<Map<String, Object>> getComments(Long userId, int pageNum, int pageSize) {
        var qw = new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticleComment>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleComment::getUserId, userId)
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleComment::getDeleted, 0)
                .orderByDesc(com.agriculture.knowledge.article.entity.KnowledgeArticleComment::getCreateTime);
        var page = commentMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        List<Map<String, Object>> list = page.getRecords().stream().map(c -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", c.getId()); m.put("articleId", c.getArticleId());
            m.put("content", c.getContent()); m.put("createTime", c.getCreateTime());
            m.put("status", c.getStatus() != null && c.getStatus() == 1 ? "已发布" : "待审核");
            try { var a = articleMapper.selectById(c.getArticleId()); if (a != null) m.put("articleTitle", a.getTitle()); } catch (Exception ignored) {}
            return m;
        }).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        var comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BizException(400, "评论不存在");
        if (!comment.getUserId().equals(userId)) throw new BizException(403, "无权删除");
        comment.setDeleted(1);
        commentMapper.updateById(comment);
    }

    @Override
    public Map<String, Object> getBehaviorStats(Long userId) {
        Map<String, Object> stats = new java.util.LinkedHashMap<>();
        var logs = behaviorLogMapper.selectList(new LambdaQueryWrapper<com.agriculture.recommend.entity.UserBehaviorLog>()
                .eq(com.agriculture.recommend.entity.UserBehaviorLog::getUserId, userId));
        long viewCount = logs.stream().filter(l -> "VIEW".equals(l.getBehaviorType())).count();
        long searchCount = logs.stream().filter(l -> "SEARCH".equals(l.getBehaviorType())).count();
        var recentViews = logs.stream().filter(l -> "VIEW".equals(l.getBehaviorType()))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime())).limit(5)
                .map(l -> { Map<String,Object> m = new java.util.LinkedHashMap<>(); m.put("contentId", l.getContentId()); m.put("time", l.getCreateTime()); return m; }).collect(Collectors.toList());
        var recentSearches = logs.stream().filter(l -> "SEARCH".equals(l.getBehaviorType()))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime())).limit(5)
                .map(l -> l.getKeyword()).distinct().collect(Collectors.toList());
        long favoriteCount = favoriteMapper.selectCount(new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleFavorite::getUserId, userId));
        long commentCount = commentMapper.selectCount(new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticleComment>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleComment::getUserId, userId)
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticleComment::getDeleted, 0));
        stats.put("viewCount", viewCount); stats.put("searchCount", searchCount);
        stats.put("favoriteCount", favoriteCount); stats.put("commentCount", commentCount);
        stats.put("recentViews", recentViews); stats.put("recentSearches", recentSearches);
        return stats;
    }

    @Override
    public List<Map<String, Object>> getInterestTags(Long userId) {
        var tags = interestTagMapper.selectList(new LambdaQueryWrapper<com.agriculture.recommend.entity.UserInterestTag>()
                .eq(com.agriculture.recommend.entity.UserInterestTag::getUserId, userId)
                .orderByDesc(com.agriculture.recommend.entity.UserInterestTag::getWeight));
        if (tags.isEmpty()) {
            String[][] defaults = {{"草莓", "crop"}, {"温室管理", "greenhouse"}, {"病虫害防治", "disease"}, {"水肥一体化", "irrigation"}};
            return java.util.Arrays.stream(defaults).map(d -> {
                Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("tagName", d[0]); m.put("tagType", d[1]); m.put("weight", 1.0); return m;
            }).collect(Collectors.toList());
        }
        return tags.stream().map(t -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("tagName", t.getTagName()); m.put("tagType", t.getTagType()); m.put("weight", t.getWeight()); return m;
        }).collect(Collectors.toList());
    }
}
