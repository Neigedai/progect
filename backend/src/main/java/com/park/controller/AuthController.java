package com.park.controller;

import com.park.common.Result;
import com.park.dto.*;
import com.park.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.ok();
    }

    @PostMapping("/send-code")
    public Result<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return Result.ok(userService.sendCode(request));
    }

    @PostMapping("/phone-login")
    public Result<Map<String, String>> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        return Result.ok(userService.phoneLogin(request));
    }
}
