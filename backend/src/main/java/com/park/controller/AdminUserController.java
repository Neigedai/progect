package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.UserQueryDTO;
import com.park.entity.User;
import com.park.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Result<PageResult<User>> listUsers(@Valid UserQueryDTO query) {
        return Result.ok(adminUserService.listUsers(query));
    }

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = adminUserService.getUserById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.ok(user);
    }

    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody User user) {
        adminUserService.createUser(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        adminUserService.updateUser(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminUserService.updateStatus(id, status);
        return Result.ok();
    }

    @PatchMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.isBlank()) {
            return Result.fail(400, "密码不能为空");
        }
        adminUserService.resetPassword(id, newPassword);
        return Result.ok();
    }
}
