package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.entity.*;
import com.park.mapper.*;
import com.park.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserMapper userMapper;

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) return Collections.emptyList();

        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        List<RolePermission> rps = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds));
        if (rps.isEmpty()) return Collections.emptyList();

        List<Long> permIds = rps.stream().map(RolePermission::getPermissionId).distinct().toList();
        List<Permission> perms = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().in(Permission::getId, permIds));
        return perms.stream().map(Permission::getPermissionCode).distinct().toList();
    }

    @Override
    public Set<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
                .stream().map(UserRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getId));
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        List<UserRole> urs = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) return Collections.emptyList();
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).toList();
        return roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSortOrder));
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        List<RolePermission> rps = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        if (rps.isEmpty()) return Collections.emptyList();
        List<Long> permIds = rps.stream().map(RolePermission::getPermissionId).toList();
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().in(Permission::getId, permIds));
    }

    @Override
    public void assignRolePermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        for (Long permId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermissionMapper.insert(rp);
        }
    }

    @Override
    public void assignUserRole(Long userId, Long roleId) {
        Long count = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, roleId));
        if (count == 0) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }

    @Override
    public void removeUserRole(Long userId, Long roleId) {
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, roleId));
    }

    @Override
    public Role saveRole(Role role) {
        roleMapper.insert(role);
        return role;
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        return getUserPermissionCodes(userId).contains(permissionCode);
    }

    @Override
    public List<User> getRoleUsers(Long roleId) {
        List<UserRole> urs = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
        if (urs.isEmpty()) return Collections.emptyList();
        List<Long> userIds = urs.stream().map(UserRole::getUserId).toList();
        return userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
    }

    @Override
    public void assignRoleUsers(Long roleId, List<Long> userIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
        for (Long userId : userIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }
}
