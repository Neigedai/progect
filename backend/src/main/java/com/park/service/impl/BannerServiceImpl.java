package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.dto.BannerQueryDTO;
import com.park.entity.CmsBanner;
import com.park.mapper.CmsBannerMapper;
import com.park.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final CmsBannerMapper bannerMapper;

    @Override
    public PageResult<CmsBanner> listBanners(BannerQueryDTO query) {
        LambdaQueryWrapper<CmsBanner> wrapper = new LambdaQueryWrapper<>();
        if (query.getTitle() != null && !query.getTitle().isBlank()) {
            wrapper.like(CmsBanner::getTitle, query.getTitle());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CmsBanner::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(CmsBanner::getSortOrder);

        Page<CmsBanner> page = bannerMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public CmsBanner getBannerById(Long id) {
        return bannerMapper.selectById(id);
    }

    @Override
    public void createBanner(CmsBanner banner) {
        bannerMapper.insert(banner);
    }

    @Override
    public void updateBanner(CmsBanner banner) {
        bannerMapper.updateById(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        CmsBanner banner = new CmsBanner();
        banner.setId(id);
        banner.setStatus(status);
        bannerMapper.updateById(banner);
    }
}
