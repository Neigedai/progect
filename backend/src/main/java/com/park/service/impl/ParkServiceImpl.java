package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.dto.ParkOverviewVO;
import com.park.entity.ParkFacility;
import com.park.entity.ParkHonor;
import com.park.entity.ParkInfo;
import com.park.mapper.ParkFacilityMapper;
import com.park.mapper.ParkHonorMapper;
import com.park.mapper.ParkInfoMapper;
import com.park.service.ParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkServiceImpl implements ParkService {

    private final ParkInfoMapper parkInfoMapper;
    private final ParkFacilityMapper facilityMapper;
    private final ParkHonorMapper honorMapper;

    @Override
    public List<ParkInfo> listParks() {
        return parkInfoMapper.selectList(null);
    }

    @Override
    public ParkOverviewVO getOverview(Long parkId) {
        ParkInfo park = parkInfoMapper.selectById(parkId);
        if (park == null) {
            return null;
        }

        List<ParkFacility> facilities = facilityMapper.selectList(
                new LambdaQueryWrapper<ParkFacility>()
                        .eq(ParkFacility::getParkId, parkId)
                        .orderByAsc(ParkFacility::getSortOrder)
        );

        List<ParkHonor> honors = honorMapper.selectList(
                new LambdaQueryWrapper<ParkHonor>()
                        .eq(ParkHonor::getParkId, parkId)
                        .orderByAsc(ParkHonor::getSortOrder)
        );

        ParkOverviewVO vo = new ParkOverviewVO();
        vo.setParkInfo(park);
        vo.setFacilities(facilities);
        vo.setHonors(honors);
        return vo;
    }
}
