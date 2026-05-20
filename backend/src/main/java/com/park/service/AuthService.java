package com.park.service;

import com.park.entity.Permission;
import com.park.entity.Role;
import com.park.entity.User;
import java.util.List;
import java.util.Set;

public interface AuthService {
    List<String> getUserPermissionCodes(Long userId);
    Set<Long> getUserRoleIds(Long userId);
    List<Role> getAllRoles();
    List<Role> getUserRoles(Long userId);
    List<Permission> getAllPermissions();
    List<Permission> getRolePermissions(Long roleId);
    void assignRolePermissions(Long roleId, List<Long> permissionIds);
    void assignUserRole(Long userId, Long roleId);
    void removeUserRole(Long userId, Long roleId);
    Role saveRole(Role role);
    void updateRole(Role role);
    void deleteRole(Long id);
    boolean hasPermission(Long userId, String permissionCode);
    List<User> getRoleUsers(Long roleId);
    void assignRoleUsers(Long roleId, List<Long> userIds);
}
