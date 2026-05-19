package com.park.service;

import com.park.common.PageResult;
import com.park.dto.UserQueryDTO;
import com.park.entity.User;

public interface AdminUserService {
    PageResult<User> listUsers(UserQueryDTO query);
    User getUserById(Long id);
    void createUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    void updateStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword);
}
