package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.ServiceItemQueryDTO;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import com.park.service.AdminServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final AdminServiceService adminServiceService;

    // ====== 服务分类 ======

    @GetMapping("/categories")
    public Result<List<ServiceCategory>> listCategories() {
        return Result.ok(adminServiceService.listCategories());
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@Valid @RequestBody ServiceCategory category) {
        adminServiceService.createCategory(category);
        return Result.ok();
    }

    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody ServiceCategory category) {
        category.setId(id);
        adminServiceService.updateCategory(category);
        return Result.ok();
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminServiceService.deleteCategory(id);
        return Result.ok();
    }

    // ====== 服务项目 ======

    @GetMapping("/items")
    public Result<PageResult<ServiceItem>> pageItems(@Valid ServiceItemQueryDTO query) {
        return Result.ok(adminServiceService.pageItems(query));
    }

    @GetMapping("/items/{id}")
    public Result<ServiceItem> getItem(@PathVariable Long id) {
        ServiceItem item = adminServiceService.getItemById(id);
        if (item == null) {
            return Result.fail(404, "服务不存在");
        }
        return Result.ok(item);
    }

    @PostMapping("/items")
    public Result<Void> createItem(@Valid @RequestBody ServiceItem item) {
        adminServiceService.createItem(item);
        return Result.ok();
    }

    @PutMapping("/items/{id}")
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody ServiceItem item) {
        item.setId(id);
        adminServiceService.updateItem(item);
        return Result.ok();
    }

    @DeleteMapping("/items/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        adminServiceService.deleteItem(id);
        return Result.ok();
    }

    @PatchMapping("/items/{id}/status")
    public Result<Void> updateItemStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminServiceService.updateItemStatus(id, status);
        return Result.ok();
    }
}
