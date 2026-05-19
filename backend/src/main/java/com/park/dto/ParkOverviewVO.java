package com.park.dto;

import com.park.entity.ParkFacility;
import com.park.entity.ParkHonor;
import com.park.entity.ParkInfo;
import lombok.Data;
import java.util.List;

@Data
public class ParkOverviewVO {
    private ParkInfo parkInfo;
    private List<ParkFacility> facilities;
    private List<ParkHonor> honors;
}
