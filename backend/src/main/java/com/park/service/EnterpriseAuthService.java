package com.park.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.EnterpriseAuth;

public interface EnterpriseAuthService {
    EnterpriseAuth submit(EnterpriseAuth auth);
    EnterpriseAuth getByUserId(Long userId);
    EnterpriseAuth getById(Long id);
    Page<EnterpriseAuth> listAll(int page, int size, String status, String companyName);
    void review(Long id, Long reviewerId, String status, String reviewComment);
    void updateTag(Long id, String tag);
}
