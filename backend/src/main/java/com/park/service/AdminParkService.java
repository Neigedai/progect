package com.park.service;

import com.park.common.PageResult;
import com.park.dto.ParkQueryDTO;
import com.park.entity.ParkInfo;

public interface AdminParkService {
    PageResult<ParkInfo> listParks(ParkQueryDTO query);
    ParkInfo getParkById(Long id);
    void createPark(ParkInfo park);
    void updatePark(ParkInfo park);
    void deletePark(Long id);
    void updateStatus(Long id, Integer status);
}
