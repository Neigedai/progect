package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.dto.UserQueryDTO;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<User> listUsers(UserQueryDTO query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(User::getUsername, query.getKeyword())
                    .or()
                    .like(User::getPhone, query.getKeyword())
                    .or()
                    .like(User::getNickname, query.getKeyword()));
        }
        if (query.getStatus() != null) {
            wrapper.eq(User::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = userMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void createUser(User user) {
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()))) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (user.getPhone() != null && userMapper.exists(
                new LambdaQueryWrapper<User>().eq(User::getPhone, user.getPhone()))) {
            throw new IllegalArgumentException("手机号已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
