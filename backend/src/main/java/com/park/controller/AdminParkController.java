package com.park.controller;

import com.park.common.PageResult;
import com.park.common.Result;
import com.park.dto.ParkQueryDTO;
import com.park.entity.ParkFacility;
import com.park.entity.ParkHonor;
import com.park.entity.ParkInfo;
import com.park.mapper.ParkFacilityMapper;
import com.park.mapper.ParkHonorMapper;
import com.park.service.AdminParkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/parks")
@RequiredArgsConstructor
public class AdminParkController {

    private final AdminParkService adminParkService;
    private final ParkFacilityMapper facilityMapper;
    private final ParkHonorMapper honorMapper;

    // ====== 园区基础信息 ======

    @GetMapping
    public Result<PageResult<ParkInfo>> listParks(@Valid ParkQueryDTO query) {
        return Result.ok(adminParkService.listParks(query));
    }

    @GetMapping("/{id}")
    public Result<ParkInfo> getPark(@PathVariable Long id) {
        ParkInfo park = adminParkService.getParkById(id);
        if (park == null) {
            return Result.fail(404, "园区不存在");
        }
        return Result.ok(park);
    }

    @PostMapping
    public Result<Void> createPark(@Valid @RequestBody ParkInfo park) {
        park.setCreateBy(getCurrentUser());
        park.setUpdateBy(getCurrentUser());
        adminParkService.createPark(park);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updatePark(@PathVariable Long id, @Valid @RequestBody ParkInfo park) {
        park.setId(id);
        park.setUpdateBy(getCurrentUser());
        adminParkService.updatePark(park);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePark(@PathVariable Long id) {
        adminParkService.deletePark(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminParkService.updateStatus(id, status);
        return Result.ok();
    }

    // ====== 配套设施 ======

    @GetMapping("/{parkId}/facilities")
    public Result<List<ParkFacility>> listFacilities(@PathVariable Long parkId) {
        List<ParkFacility> list = facilityMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParkFacility>()
                        .eq(ParkFacility::getParkId, parkId)
                        .orderByAsc(ParkFacility::getSortOrder));
        return Result.ok(list);
    }

    @PostMapping("/{parkId}/facilities")
    public Result<Void> createFacility(@PathVariable Long parkId, @Valid @RequestBody ParkFacility facility) {
        facility.setParkId(parkId);
        facilityMapper.insert(facility);
        return Result.ok();
    }

    @PutMapping("/{parkId}/facilities/{id}")
    public Result<Void> updateFacility(@PathVariable Long parkId, @PathVariable Long id,
                                       @Valid @RequestBody ParkFacility facility) {
        facility.setId(id);
        facility.setParkId(parkId);
        facilityMapper.updateById(facility);
        return Result.ok();
    }

    @DeleteMapping("/{parkId}/facilities/{id}")
    public Result<Void> deleteFacility(@PathVariable Long parkId, @PathVariable Long id) {
        facilityMapper.deleteById(id);
        return Result.ok();
    }

    // ====== 荣誉资质 ======

    @GetMapping("/{parkId}/honors")
    public Result<List<ParkHonor>> listHonors(@PathVariable Long parkId) {
        List<ParkHonor> list = honorMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParkHonor>()
                        .eq(ParkHonor::getParkId, parkId)
                        .orderByAsc(ParkHonor::getSortOrder));
        return Result.ok(list);
    }

    @PostMapping("/{parkId}/honors")
    public Result<Void> createHonor(@PathVariable Long parkId, @Valid @RequestBody ParkHonor honor) {
        honor.setParkId(parkId);
        honorMapper.insert(honor);
        return Result.ok();
    }

    @PutMapping("/{parkId}/honors/{id}")
    public Result<Void> updateHonor(@PathVariable Long parkId, @PathVariable Long id,
                                    @Valid @RequestBody ParkHonor honor) {
        honor.setId(id);
        honor.setParkId(parkId);
        honorMapper.updateById(honor);
        return Result.ok();
    }

    @DeleteMapping("/{parkId}/honors/{id}")
    public Result<Void> deleteHonor(@PathVariable Long parkId, @PathVariable Long id) {
        honorMapper.deleteById(id);
        return Result.ok();
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
