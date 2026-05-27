package com.park.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.ServiceItemQueryDTO;
import com.park.entity.ServiceApplication;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import com.park.entity.User;
import com.park.mapper.ServiceApplicationMapper;
import com.park.mapper.UserMapper;
import com.park.service.AdminServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final AdminServiceService adminServiceService;
    private final ServiceApplicationMapper serviceApplicationMapper;
    private final UserMapper userMapper;

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

    // ====== 服务申请 ======

    @GetMapping("/applications")
    public Result<PageResult<Map<String, Object>>> pageApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<ServiceApplication> wrapper = new LambdaQueryWrapper<>();
        if (contactName != null && !contactName.isBlank()) {
            wrapper.like(ServiceApplication::getContactName, contactName);
        }
        if (serviceName != null && !serviceName.isBlank()) {
            wrapper.like(ServiceApplication::getServiceName, serviceName);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(ServiceApplication::getStatus, status);
        }
        wrapper.orderByDesc(ServiceApplication::getCreateTime);
        Page<ServiceApplication> result = serviceApplicationMapper.selectPage(new Page<>(page, size), wrapper);

        List<Long> userIds = result.getRecords().stream().map(ServiceApplication::getUserId).distinct().toList();
        Map<Long, String> userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds))
                .stream().collect(Collectors.toMap(User::getId, User::getUsername));
        List<Map<String, Object>> records = result.getRecords().stream().map(app -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", app.getId());
            map.put("userId", app.getUserId());
            map.put("serviceId", app.getServiceId());
            map.put("serviceName", app.getServiceName());
            map.put("contactName", app.getContactName());
            map.put("contactPhone", app.getContactPhone());
            map.put("status", app.getStatus());
            map.put("createTime", app.getCreateTime());
            map.put("username", userMap.getOrDefault(app.getUserId(), ""));
            return map;
        }).collect(Collectors.toList());

        return Result.ok(new PageResult<>(records, result.getTotal(), page, size));
    }

    @PatchMapping("/applications/{id}")
    public Result<Void> updateApplication(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ServiceApplication app = serviceApplicationMapper.selectById(id);
        if (app == null) {
            return Result.fail(404, "申请不存在");
        }
        if (body.containsKey("status")) {
            app.setStatus(body.get("status"));
        }
        if (body.containsKey("contactName")) {
            app.setContactName(body.get("contactName"));
        }
        if (body.containsKey("contactPhone")) {
            app.setContactPhone(body.get("contactPhone"));
        }
        serviceApplicationMapper.updateById(app);
        return Result.ok();
    }
}
