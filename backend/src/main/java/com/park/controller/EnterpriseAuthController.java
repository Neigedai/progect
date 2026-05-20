package com.park.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.annotation.RequirePermission;
import com.park.common.Result;
import com.park.entity.EnterpriseAuth;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.EnterpriseAuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EnterpriseAuthController {

    private final EnterpriseAuthService enterpriseAuthService;
    private final UserMapper userMapper;
    private final com.park.service.NotificationService notificationService;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }

    @PostMapping("/api/enterprise/submit")
    public Result<EnterpriseAuth> submit(@RequestBody EnterpriseAuth auth) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        auth.setUserId(userId);
        return Result.ok(enterpriseAuthService.submit(auth));
    }

    @GetMapping("/api/enterprise/status")
    public Result<EnterpriseAuth> status() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        return Result.ok(enterpriseAuthService.getByUserId(userId));
    }

    @GetMapping("/api/admin/enterprise")
    @RequirePermission("enterprise:view")
    public Result<Page<EnterpriseAuth>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyName) {
        Page<EnterpriseAuth> result = enterpriseAuthService.listAll(page, size, status, companyName);
        List<Long> userIds = result.getRecords().stream().map(EnterpriseAuth::getUserId).distinct().collect(Collectors.toList());
        if (!userIds.isEmpty()) {
            Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
            result.getRecords().forEach(auth -> {
                User u = userMap.get(auth.getUserId());
                if (u != null) {
                    auth.setUserNickname(u.getNickname());
                    auth.setUserPhone(u.getPhone());
                }
            });
        }
        return Result.ok(result);
    }

    @GetMapping("/api/admin/enterprise/{id}")
    @RequirePermission("enterprise:view")
    public Result<EnterpriseAuth> getById(@PathVariable Long id) {
        EnterpriseAuth auth = enterpriseAuthService.getById(id);
        if (auth == null) {
            return Result.fail(404, "认证记录不存在");
        }
        return Result.ok(auth);
    }

    @PatchMapping("/api/admin/enterprise/{id}/review")
    @RequirePermission("enterprise:approve")
    public Result<Void> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long reviewerId = getCurrentUserId();
        if (reviewerId == null) {
            return Result.fail(401, "请先登录");
        }
        String status = body.get("status");
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            return Result.fail(400, "status 必须为 approved 或 rejected");
        }
        enterpriseAuthService.review(id, reviewerId, status, body.getOrDefault("reviewComment", ""));
        EnterpriseAuth auth = enterpriseAuthService.getById(id);
        if (auth != null) {
            String result = "approved".equals(status) ? "已通过" : "已驳回";
            notificationService.sendToUser(auth.getUserId(), "business",
                    "企业认证审核结果通知",
                    "您的企业认证（" + auth.getCompanyName() + "）审核" + result +
                            ("rejected".equals(status) && body.get("reviewComment") != null ? "，原因：" + body.get("reviewComment") : ""));
        }
        return Result.ok();
    }

    @PatchMapping("/api/admin/enterprise/{id}/tag")
    @RequirePermission("enterprise:view")
    public Result<Void> updateTag(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String tag = body.get("tag");
        enterpriseAuthService.updateTag(id, tag);
        return Result.ok();
    }
}
