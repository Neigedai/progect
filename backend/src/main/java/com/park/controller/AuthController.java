package com.park.controller;

import com.park.common.Result;
import com.park.dto.*;
import com.park.entity.Permission;
import com.park.entity.Role;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.AuthService;
import com.park.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/api/auth/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }

    @PostMapping("/api/auth/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.ok();
    }

    @PostMapping("/api/auth/send-code")
    public Result<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return Result.ok(userService.sendCode(request));
    }

    @PostMapping("/api/auth/phone-login")
    public Result<Map<String, String>> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        return Result.ok(userService.phoneLogin(request));
    }

    @GetMapping("/api/auth/me")
    public Result<Map<String, Object>> me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.fail(401, "未登录");
        }
        List<Role> roles = authService.getUserRoles(user.getId());
        List<String> permissions = authService.getUserPermissionCodes(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return Result.ok(data);
    }
}
