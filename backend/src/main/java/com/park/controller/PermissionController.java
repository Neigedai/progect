package com.park.controller;

import com.park.common.Result;
import com.park.entity.Permission;
import com.park.entity.Role;
import com.park.entity.User;
import com.park.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/perm")
@RequiredArgsConstructor
public class PermissionController {

    private final AuthService authService;

    @GetMapping("/roles")
    public Result<List<Role>> listRoles() {
        return Result.ok(authService.getAllRoles());
    }

    @GetMapping("/roles/{id}")
    public Result<Role> getRole(@PathVariable Long id) {
        return Result.ok(authService.getAllRoles().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null));
    }

    @PostMapping("/roles")
    public Result<Role> createRole(@RequestBody Role role) {
        return Result.ok(authService.saveRole(role));
    }

    @PutMapping("/roles/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        authService.updateRole(role);
        return Result.ok();
    }

    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        authService.deleteRole(id);
        return Result.ok();
    }

    @GetMapping("/roles/{id}/permissions")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long id) {
        return Result.ok(authService.getRolePermissions(id));
    }

    @PutMapping("/roles/{id}/permissions")
    public Result<Void> assignRolePermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        authService.assignRolePermissions(id, body.getOrDefault("permissionIds", Collections.emptyList()));
        return Result.ok();
    }

    @GetMapping("/permissions")
    public Result<List<Permission>> listPermissions() {
        return Result.ok(authService.getAllPermissions());
    }

    @GetMapping("/users/{userId}/roles")
    public Result<List<Role>> getUserRoles(@PathVariable Long userId) {
        return Result.ok(authService.getUserRoles(userId));
    }

    @PostMapping("/users/{userId}/roles")
    public Result<Void> assignUserRole(@PathVariable Long userId, @RequestBody Map<String, Long> body) {
        Long roleId = body.get("roleId");
        if (roleId != null) {
            authService.assignUserRole(userId, roleId);
        }
        return Result.ok();
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public Result<Void> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        authService.removeUserRole(userId, roleId);
        return Result.ok();
    }

    @GetMapping("/roles/{id}/users")
    public Result<List<User>> getRoleUsers(@PathVariable Long id) {
        return Result.ok(authService.getRoleUsers(id));
    }

    @PutMapping("/roles/{id}/users")
    public Result<Void> assignRoleUsers(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        authService.assignRoleUsers(id, body.getOrDefault("userIds", Collections.emptyList()));
        return Result.ok();
    }
}
