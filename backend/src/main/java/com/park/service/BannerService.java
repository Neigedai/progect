package com.park.service;

import com.park.common.PageResult;
import com.park.dto.BannerQueryDTO;
import com.park.entity.CmsBanner;

public interface BannerService {
    PageResult<CmsBanner> listBanners(BannerQueryDTO query);
    CmsBanner getBannerById(Long id);
    void createBanner(CmsBanner banner);
    void updateBanner(CmsBanner banner);
    void deleteBanner(Long id);
    void updateStatus(Long id, Integer status);
}
