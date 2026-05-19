package com.park.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.Result;
import com.park.dto.ServiceDetailVO;
import com.park.dto.ServiceItemQueryDTO;
import com.park.dto.ServiceItemVO;
import com.park.entity.ServiceCategory;
import com.park.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

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
}
