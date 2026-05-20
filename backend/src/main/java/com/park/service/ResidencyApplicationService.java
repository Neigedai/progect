package com.park.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.ResidencyApplication;

public interface ResidencyApplicationService {
    ResidencyApplication submit(ResidencyApplication app);
    Page<ResidencyApplication> listByUserId(Long userId, int page, int size);
    Page<ResidencyApplication> listAll(int page, int size, String status);
    ResidencyApplication getById(Long id);
    void update(ResidencyApplication app);
    void approve(Long id, Long reviewerId, String status, String comment);
}
