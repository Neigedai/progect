package com.park.service;

import com.park.dto.ParkOverviewVO;
import com.park.entity.ParkInfo;
import java.util.List;

public interface ParkService {
    List<ParkInfo> listParks();
    ParkOverviewVO getOverview(Long parkId);
}
