package com.park.controller;

import com.park.common.Result;
import com.park.dto.ParkOverviewVO;
import com.park.entity.ParkInfo;
import com.park.service.ParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parks")
@RequiredArgsConstructor
public class ParkController {

    private final ParkService parkService;

    /**
     * 获取可用园区列表
     * 返回 id, name, logo, is_default 用于前端切换器
     */
    @GetMapping
    public Result<List<ParkInfo>> listParks() {
        return Result.ok(parkService.listParks());
    }

    /**
     * 获取指定园区概况
     * 包含基础信息、配套设施列表、荣誉资质列表、规划/交通说明
     */
    @GetMapping("/{id}/overview")
    public Result<ParkOverviewVO> getOverview(@PathVariable Long id) {
        ParkOverviewVO vo = parkService.getOverview(id);
        if (vo == null) {
            return Result.fail(404, "园区不存在");
        }
        return Result.ok(vo);
    }
}
