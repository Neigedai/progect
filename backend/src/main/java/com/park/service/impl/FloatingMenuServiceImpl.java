package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.entity.FloatingMenuItem;
import com.park.mapper.FloatingMenuItemMapper;
import com.park.service.FloatingMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloatingMenuServiceImpl implements FloatingMenuService {

    private final FloatingMenuItemMapper mapper;

    @Override
    public List<FloatingMenuItem> listEnabled() {
        return mapper.selectList(
                new LambdaQueryWrapper<FloatingMenuItem>()
                        .eq(FloatingMenuItem::getStatus, 1)
                        .orderByAsc(FloatingMenuItem::getSortOrder)
        );
    }

    @Override
    public List<FloatingMenuItem> listAll() {
        return mapper.selectList(
                new LambdaQueryWrapper<FloatingMenuItem>()
                        .orderByAsc(FloatingMenuItem::getSortOrder)
        );
    }

    @Override
    public FloatingMenuItem getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(FloatingMenuItem item) {
        mapper.insert(item);
    }

    @Override
    public void update(FloatingMenuItem item) {
        mapper.updateById(item);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
