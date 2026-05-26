package com.park.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.Result;
import com.park.dto.ServiceApplicationRequest;
import com.park.dto.ServiceDetailVO;
import com.park.dto.ServiceItemQueryDTO;
import com.park.dto.ServiceItemVO;
import com.park.entity.ServiceApplication;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import com.park.entity.User;
import com.park.mapper.ServiceApplicationMapper;
import com.park.mapper.ServiceItemMapper;
import com.park.mapper.UserMapper;
import com.park.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;
    private final ServiceItemMapper serviceItemMapper;
    private final ServiceApplicationMapper serviceApplicationMapper;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }

    @GetMapping("/categories")
    public Result<List<ServiceCategory>> listCategories() {
        return Result.ok(serviceCatalogService.listCategories());
    }

    @GetMapping("/items")
    public Result<Object> pageItems(ServiceItemQueryDTO query) {
        Page<ServiceItemVO> page = serviceCatalogService.pageItems(query);
        return Result.ok(page);
    }

    @GetMapping("/items/{id}")
    public Result<ServiceDetailVO> getDetail(@PathVariable Long id) {
        ServiceDetailVO vo = serviceCatalogService.getDetail(id);
        if (vo == null) {
            return Result.fail(404, "服务不存在");
        }
        return Result.ok(vo);
    }

    @PostMapping("/applications")
    public Result<Void> submitApplication(@Valid @RequestBody ServiceApplicationRequest request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        ServiceItem item = serviceItemMapper.selectById(request.getServiceId());
        ServiceApplication app = new ServiceApplication();
        app.setUserId(userId);
        app.setServiceId(request.getServiceId());
        app.setServiceName(item != null ? item.getServiceName() : "");
        app.setContactName(request.getContactName());
        app.setContactPhone(request.getContactPhone());
        app.setStatus("pending");
        serviceApplicationMapper.insert(app);
        return Result.ok();
    }

    @GetMapping("/applications/my")
    public Result<List<ServiceApplication>> myApplications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        return Result.ok(serviceApplicationMapper.selectList(
                new LambdaQueryWrapper<ServiceApplication>()
                        .eq(ServiceApplication::getUserId, userId)
                        .orderByDesc(ServiceApplication::getCreateTime)));
    }
}
