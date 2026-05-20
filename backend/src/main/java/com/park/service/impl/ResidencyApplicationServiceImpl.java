package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.ResidencyApplication;
import com.park.mapper.ResidencyApplicationMapper;
import com.park.service.ResidencyApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResidencyApplicationServiceImpl implements ResidencyApplicationService {

    private final ResidencyApplicationMapper mapper;

    @Override
    public ResidencyApplication submit(ResidencyApplication app) {
        app.setStatus("pending");
        mapper.insert(app);
        return app;
    }

    @Override
    public Page<ResidencyApplication> listByUserId(Long userId, int page, int size) {
        Page<ResidencyApplication> p = new Page<>(page, size);
        return mapper.selectPage(p,
                new LambdaQueryWrapper<ResidencyApplication>()
                        .eq(ResidencyApplication::getUserId, userId)
                        .orderByDesc(ResidencyApplication::getCreateTime)
        );
    }

    @Override
    public Page<ResidencyApplication> listAll(int page, int size, String status) {
        Page<ResidencyApplication> p = new Page<>(page, size);
        LambdaQueryWrapper<ResidencyApplication> wrapper = new LambdaQueryWrapper<ResidencyApplication>()
                .orderByDesc(ResidencyApplication::getCreateTime);
        if (status != null && !status.isBlank()) {
            wrapper.eq(ResidencyApplication::getStatus, status);
        }
        return mapper.selectPage(p, wrapper);
    }

    @Override
    public ResidencyApplication getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void update(ResidencyApplication app) {
        mapper.updateById(app);
    }

    @Override
    public void approve(Long id, Long reviewerId, String status, String comment) {
        ResidencyApplication app = new ResidencyApplication();
        app.setId(id);
        app.setStatus(status);
        app.setReviewedBy(reviewerId);
        app.setReviewComment(comment);
        mapper.updateById(app);
    }
}
