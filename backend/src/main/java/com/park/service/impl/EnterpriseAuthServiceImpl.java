package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.EnterpriseAuth;
import com.park.mapper.EnterpriseAuthMapper;
import com.park.service.EnterpriseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnterpriseAuthServiceImpl implements EnterpriseAuthService {

    private final EnterpriseAuthMapper enterpriseAuthMapper;

    @Override
    public EnterpriseAuth submit(EnterpriseAuth auth) {
        auth.setAuthStatus("pending");
        auth.setAuthSubmittedAt(LocalDateTime.now());
        auth.setLegalFaceVerified(auth.getLegalFaceVerified() != null && auth.getLegalFaceVerified());
        EnterpriseAuth existing = enterpriseAuthMapper.selectOne(
                new LambdaQueryWrapper<EnterpriseAuth>().eq(EnterpriseAuth::getUserId, auth.getUserId()));
        if (existing != null) {
            auth.setId(existing.getId());
            enterpriseAuthMapper.updateById(auth);
        } else {
            enterpriseAuthMapper.insert(auth);
        }
        return auth;
    }

    @Override
    public EnterpriseAuth getByUserId(Long userId) {
        return enterpriseAuthMapper.selectOne(
                new LambdaQueryWrapper<EnterpriseAuth>().eq(EnterpriseAuth::getUserId, userId));
    }

    @Override
    public EnterpriseAuth getById(Long id) {
        return enterpriseAuthMapper.selectById(id);
    }

    @Override
    public Page<EnterpriseAuth> listAll(int page, int size, String status, String companyName) {
        LambdaQueryWrapper<EnterpriseAuth> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(EnterpriseAuth::getAuthStatus, status);
        }
        if (companyName != null && !companyName.isEmpty()) {
            wrapper.like(EnterpriseAuth::getCompanyName, companyName);
        }
        wrapper.orderByDesc(EnterpriseAuth::getCreateTime);
        return enterpriseAuthMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void review(Long id, Long reviewerId, String status, String reviewComment) {
        EnterpriseAuth auth = enterpriseAuthMapper.selectById(id);
        if (auth == null) return;
        auth.setAuthStatus(status);
        auth.setReviewedBy(reviewerId);
        auth.setReviewComment(reviewComment);
        auth.setAuthReviewedAt(LocalDateTime.now());
        enterpriseAuthMapper.updateById(auth);
    }

    @Override
    public void updateTag(Long id, String tag) {
        EnterpriseAuth auth = enterpriseAuthMapper.selectById(id);
        if (auth == null) return;
        auth.setTag(tag);
        enterpriseAuthMapper.updateById(auth);
    }
}
