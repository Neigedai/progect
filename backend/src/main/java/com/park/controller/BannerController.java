package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.BannerQueryDTO;
import com.park.entity.CmsBanner;
import com.park.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<PageResult<CmsBanner>> listBanners(@Valid BannerQueryDTO query) {
        return Result.ok(bannerService.listBanners(query));
    }

    @GetMapping("/{id}")
    public Result<CmsBanner> getBanner(@PathVariable Long id) {
        CmsBanner banner = bannerService.getBannerById(id);
        if (banner == null) {
            return Result.fail(404, "轮播图不存在");
        }
        return Result.ok(banner);
    }

    @PostMapping
    public Result<Void> createBanner(@Valid @RequestBody CmsBanner banner) {
        banner.setCreateBy(getCurrentUser());
        banner.setUpdateBy(getCurrentUser());
        bannerService.createBanner(banner);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @Valid @RequestBody CmsBanner banner) {
        banner.setId(id);
        banner.setUpdateBy(getCurrentUser());
        bannerService.updateBanner(banner);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bannerService.updateStatus(id, status);
        return Result.ok();
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
