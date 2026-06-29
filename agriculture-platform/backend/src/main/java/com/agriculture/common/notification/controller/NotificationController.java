package com.agriculture.common.notification.controller;

import com.agriculture.common.notification.entity.AgriNotification;
import com.agriculture.common.notification.mapper.AgriNotificationMapper;
import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "消息通知")
public class NotificationController {

    private final AgriNotificationMapper notificationMapper;

    @GetMapping("/my")
    public Result<List<AgriNotification>> myNotifications() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(notificationMapper.selectList(
                new LambdaQueryWrapper<AgriNotification>().eq(AgriNotification::getUserId, userId)
                        .or().eq(AgriNotification::getUserId, 0L)
                        .orderByDesc(AgriNotification::getCreateTime)));
    }

    @GetMapping("/unread-count")
    public Result<Integer> unreadCount() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(notificationMapper.selectCount(
                new LambdaQueryWrapper<AgriNotification>()
                        .and(w -> w.eq(AgriNotification::getUserId, userId).or().eq(AgriNotification::getUserId, 0L))
                        .eq(AgriNotification::getReadStatus, 0)).intValue());
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        AgriNotification n = notificationMapper.selectById(id);
        if (n != null) { n.setReadStatus(1); notificationMapper.updateById(n); }
        return Result.ok();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = UserContext.getCurrentUserId();
        List<AgriNotification> list = notificationMapper.selectList(
                new LambdaQueryWrapper<AgriNotification>()
                        .and(w -> w.eq(AgriNotification::getUserId, userId).or().eq(AgriNotification::getUserId, 0L))
                        .eq(AgriNotification::getReadStatus, 0));
        for (AgriNotification n : list) { n.setReadStatus(1); notificationMapper.updateById(n); }
        return Result.ok();
    }
}
