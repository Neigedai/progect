package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.dto.ServiceDetailVO;
import com.park.dto.ServiceItemQueryDTO;
import com.park.dto.ServiceItemVO;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import com.park.mapper.ServiceCategoryMapper;
import com.park.mapper.ServiceItemMapper;
import com.park.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceCategoryMapper categoryMapper;
    private final ServiceItemMapper itemMapper;

    @Override
    public List<ServiceCategory> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<ServiceCategory>()
                        .orderByAsc(ServiceCategory::getSortOrder)
        );
    }

    @Override
    public Page<ServiceItemVO> pageItems(ServiceItemQueryDTO query) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .orderByAsc(ServiceItem::getSortOrder);

        if (query.getCategoryId() != null) {
            wrapper.eq(ServiceItem::getCategoryId, query.getCategoryId());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(ServiceItem::getServiceName, query.getKeyword())
                    .or()
                    .like(ServiceItem::getSummary, query.getKeyword())
            );
        }

        Page<ServiceItem> entityPage = new Page<>(query.getPage(), query.getSize());
        Page<ServiceItem> result = itemMapper.selectPage(entityPage, wrapper);

        Page<ServiceItemVO> voPage = new Page<>(query.getPage(), query.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(e -> {
            ServiceItemVO vo = new ServiceItemVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).toList());
        return voPage;
    }

    @Override
    public ServiceDetailVO getDetail(Long id) {
        ServiceItem item = itemMapper.selectOne(
                new LambdaQueryWrapper<ServiceItem>()
                        .eq(ServiceItem::getId, id)
                        .eq(ServiceItem::getStatus, 1)
        );
        if (item == null) return null;

        ServiceDetailVO vo = new ServiceDetailVO();
        BeanUtils.copyProperties(item, vo);
        vo.setStepsJson(item.getSteps());
        vo.setPriceInfoJson(item.getPriceInfo());

        ServiceCategory category = categoryMapper.selectById(item.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        return vo;
    }
}
