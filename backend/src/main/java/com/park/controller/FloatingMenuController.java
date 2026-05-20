package com.park.controller;

import com.park.annotation.RequirePermission;
import com.park.common.Result;
import com.park.entity.FloatingMenuItem;
import com.park.service.FloatingMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floating-menu")
@RequiredArgsConstructor
public class FloatingMenuController {

    private final FloatingMenuService floatingMenuService;

    @GetMapping("/items")
    public Result<List<FloatingMenuItem>> listEnabled() {
        return Result.ok(floatingMenuService.listEnabled());
    }

    @GetMapping("/admin/items")
    @RequirePermission("floating:view")
    public Result<List<FloatingMenuItem>> listAll() {
        return Result.ok(floatingMenuService.listAll());
    }

    @GetMapping("/admin/items/{id}")
    public Result<FloatingMenuItem> getById(@PathVariable Long id) {
        FloatingMenuItem item = floatingMenuService.getById(id);
        if (item == null) {
            return Result.fail(404, "菜单项不存在");
        }
        return Result.ok(item);
    }

    @PostMapping("/admin/items")
    @RequirePermission("floating:add")
    public Result<Void> save(@RequestBody FloatingMenuItem item) {
        floatingMenuService.save(item);
        return Result.ok();
    }

    @PutMapping("/admin/items/{id}")
    @RequirePermission("floating:edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody FloatingMenuItem item) {
        item.setId(id);
        floatingMenuService.update(item);
        return Result.ok();
    }

    @DeleteMapping("/admin/items/{id}")
    @RequirePermission("floating:delete")
    public Result<Void> delete(@PathVariable Long id) {
        floatingMenuService.delete(id);
        return Result.ok();
    }

    @PatchMapping("/admin/items/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody FloatingMenuItem item) {
        FloatingMenuItem existing = floatingMenuService.getById(id);
        if (existing == null) {
            return Result.fail(404, "菜单项不存在");
        }
        existing.setStatus(item.getStatus());
        floatingMenuService.update(existing);
        return Result.ok();
    }
}
