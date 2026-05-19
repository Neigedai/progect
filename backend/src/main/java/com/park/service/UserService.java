package com.park.service;

import com.park.dto.LoginRequest;
import com.park.dto.PhoneLoginRequest;
import com.park.dto.RegisterRequest;
import com.park.dto.SendCodeRequest;

import java.util.Map;

public interface UserService {
    Map<String, String> login(LoginRequest request);
    void register(RegisterRequest request);
    Map<String, String> phoneLogin(PhoneLoginRequest request);
    Map<String, Object> sendCode(SendCodeRequest request);
}
