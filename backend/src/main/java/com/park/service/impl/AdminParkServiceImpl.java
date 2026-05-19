package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.dto.ParkQueryDTO;
import com.park.entity.ParkInfo;
import com.park.mapper.ParkInfoMapper;
import com.park.service.AdminParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminParkServiceImpl implements AdminParkService {

    private final ParkInfoMapper parkInfoMapper;

    @Override
    public PageResult<ParkInfo> listParks(ParkQueryDTO query) {
        LambdaQueryWrapper<ParkInfo> wrapper = new LambdaQueryWrapper<>();
        if (query.getParkName() != null && !query.getParkName().isBlank()) {
            wrapper.like(ParkInfo::getParkName, query.getParkName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ParkInfo::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(ParkInfo::getSortOrder);

        Page<ParkInfo> page = parkInfoMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public ParkInfo getParkById(Long id) {
        return parkInfoMapper.selectById(id);
    }

    @Override
    public void createPark(ParkInfo park) {
        parkInfoMapper.insert(park);
    }

    @Override
    public void updatePark(ParkInfo park) {
        parkInfoMapper.updateById(park);
    }

    @Override
    public void deletePark(Long id) {
        parkInfoMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ParkInfo park = new ParkInfo();
        park.setId(id);
        park.setStatus(status);
        parkInfoMapper.updateById(park);
    }
}
