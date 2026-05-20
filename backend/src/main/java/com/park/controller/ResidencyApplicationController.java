package com.park.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.annotation.RequirePermission;
import com.park.common.Result;
import com.park.entity.ResidencyApplication;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.ResidencyApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ResidencyApplicationController {

    private final ResidencyApplicationService residencyApplicationService;
    private final UserMapper userMapper;
    private final com.park.service.NotificationService notificationService;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }

    @PostMapping("/api/residency/applications")
    public Result<ResidencyApplication> submit(@RequestBody ResidencyApplication app) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        app.setUserId(userId);
        return Result.ok(residencyApplicationService.submit(app));
    }

    @GetMapping("/api/residency/applications/my")
    public Result<Page<ResidencyApplication>> myApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        return Result.ok(residencyApplicationService.listByUserId(userId, page, size));
    }

    @GetMapping("/api/admin/residency/applications")
    @RequirePermission("residency:view")
    public Result<Page<ResidencyApplication>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.ok(residencyApplicationService.listAll(page, size, status));
    }

    @GetMapping("/api/admin/residency/applications/{id}")
    @RequirePermission("residency:view")
    public Result<ResidencyApplication> getById(@PathVariable Long id) {
        ResidencyApplication app = residencyApplicationService.getById(id);
        if (app == null) {
            return Result.fail(404, "申请不存在");
        }
        return Result.ok(app);
    }

    @PutMapping("/api/admin/residency/applications/{id}")
    @RequirePermission("residency:edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody ResidencyApplication app) {
        app.setId(id);
        residencyApplicationService.update(app);
        return Result.ok();
    }

    @PatchMapping("/api/admin/residency/applications/{id}/approve")
    @RequirePermission("residency:approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long reviewerId = getCurrentUserId();
        if (reviewerId == null) {
            return Result.fail(401, "请先登录");
        }
        String status = body.get("status");
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            return Result.fail(400, "status 必须为 approved 或 rejected");
        }
        residencyApplicationService.approve(id, reviewerId, status, body.getOrDefault("reviewComment", ""));
        ResidencyApplication app = residencyApplicationService.getById(id);
        if (app != null) {
            String result = "approved".equals(status) ? "已通过" : "已驳回";
            notificationService.sendToUser(app.getUserId(), "business",
                    "入驻申请审核结果通知",
                    "您的入驻申请（" + app.getArea() + " / " + app.getIndustryType() + "）审核" + result);
        }
        return Result.ok();
    }
}
