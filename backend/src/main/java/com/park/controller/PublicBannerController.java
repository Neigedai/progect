package com.park.controller;

import com.park.common.Result;
import com.park.entity.CmsBanner;
import com.park.mapper.CmsBannerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class PublicBannerController {

    private final CmsBannerMapper bannerMapper;

    @GetMapping
    public Result<List<CmsBanner>> listActiveBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<CmsBanner> list = bannerMapper.selectList(
                new LambdaQueryWrapper<CmsBanner>()
                        .eq(CmsBanner::getStatus, 1)
                        .le(CmsBanner::getBeginTime, now)
                        .ge(CmsBanner::getEndTime, now)
                        .orderByAsc(CmsBanner::getSortOrder));
        return Result.ok(list);
    }
}
