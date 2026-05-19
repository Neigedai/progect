package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.dto.ServiceItemQueryDTO;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import com.park.mapper.ServiceCategoryMapper;
import com.park.mapper.ServiceItemMapper;
import com.park.service.AdminServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceServiceImpl implements AdminServiceService {

    private final ServiceCategoryMapper categoryMapper;
    private final ServiceItemMapper itemMapper;

    @Override
    public List<ServiceCategory> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<ServiceCategory>().orderByAsc(ServiceCategory::getSortOrder));
    }

    @Override
    public void createCategory(ServiceCategory category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(ServiceCategory category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public PageResult<ServiceItem> pageItems(ServiceItemQueryDTO query) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        if (query.getCategoryId() != null) {
            wrapper.eq(ServiceItem::getCategoryId, query.getCategoryId());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(ServiceItem::getServiceName, query.getKeyword());
        }
        wrapper.orderByAsc(ServiceItem::getSortOrder);

        Page<ServiceItem> page = itemMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public ServiceItem getItemById(Long id) {
        return itemMapper.selectById(id);
    }

    @Override
    public void createItem(ServiceItem item) {
        itemMapper.insert(item);
    }

    @Override
    public void updateItem(ServiceItem item) {
        itemMapper.updateById(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }

    @Override
    public void updateItemStatus(Long id, Integer status) {
        ServiceItem item = new ServiceItem();
        item.setId(id);
        item.setStatus(status);
        itemMapper.updateById(item);
    }
}
