package com.park.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.annotation.RequirePermission;
import com.park.common.Result;
import com.park.entity.Notification;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }

    @GetMapping("/api/notifications")
    public Result<Page<Notification>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isRead) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        return Result.ok(notificationService.listByUser(userId, page, size, type, isRead));
    }

    @GetMapping("/api/notifications/unread-count")
    public Result<Map<String, Long>> unreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        long count = notificationService.getUnreadCount(userId);
        return Result.ok(Map.of("count", count));
    }

    @PutMapping("/api/notifications/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        notificationService.markRead(userId, id);
        return Result.ok();
    }

    @PutMapping("/api/notifications/read-all")
    public Result<Void> markAllRead() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        notificationService.markAllRead(userId);
        return Result.ok();
    }

    @PostMapping("/api/admin/notifications")
    @RequirePermission("notification:add")
    public Result<Void> create(@RequestBody Notification notification) {
        Long senderId = getCurrentUserId();
        notification.setSenderId(senderId);
        notificationService.create(notification);
        return Result.ok();
    }

    @GetMapping("/api/admin/notifications")
    @RequirePermission("notification:view")
    public Result<Page<Notification>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        return Result.ok(notificationService.listAll(page, size, type));
    }

    @DeleteMapping("/api/admin/notifications/{id}")
    @RequirePermission("notification:delete")
    public Result<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return Result.ok();
    }
}
