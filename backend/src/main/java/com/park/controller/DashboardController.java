package com.park.controller;

import com.park.annotation.RequirePermission;
import com.park.common.Result;
import com.park.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @RequirePermission("dashboard:view")
    public Result<Map<String, Object>> getStats() {
        return Result.ok(dashboardService.getStats());
    }
}
