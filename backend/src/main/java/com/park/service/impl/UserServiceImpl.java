package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.dto.LoginRequest;
import com.park.dto.PhoneLoginRequest;
import com.park.dto.RegisterRequest;
import com.park.dto.SendCodeRequest;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.security.JwtUtils;
import com.park.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        // 更新最后登录时间
        User loginUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (loginUser != null) {
            loginUser.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(loginUser);
        }
        String token = jwtUtils.generateToken(request.getUsername());
        return Map.of("token", token);
    }

    @Override
    public void register(RegisterRequest request) {
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()))) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone()))) {
            throw new IllegalArgumentException("手机号已存在");
        }
        String savedCode = codeStore.get(request.getPhone());
        if (savedCode == null || !savedCode.equals(request.getCode())) {
            throw new IllegalArgumentException("验证码错误");
        }
        codeStore.remove(request.getPhone());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public Map<String, Object> sendCode(SendCodeRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone()));
        if (user == null) {
            // 未注册手机号，仅发送验证码，不创建用户
            String code = String.format("%06d", new Random().nextInt(1000000));
            codeStore.put(request.getPhone(), code);
            return Map.of("code", code, "debug", true);
        }
        String code = String.format("%06d", new Random().nextInt(1000000));
        user.setVerificationCode(code);
        user.setVerificationCodeTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Map.of("code", code, "debug", true);
    }

    @Override
    public Map<String, String> phoneLogin(PhoneLoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone()));
        if (user == null) {
            throw new IllegalArgumentException("手机号未注册");
        }
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new IllegalArgumentException("验证码错误");
        }
        if (user.getVerificationCodeTime() == null ||
                user.getVerificationCodeTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("验证码已过期，请重新获取");
        }
        user.setVerificationCode(null);
        user.setVerificationCodeTime(null);
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        String token = jwtUtils.generateToken(user.getUsername());
        return Map.of("token", token);
    }
}
