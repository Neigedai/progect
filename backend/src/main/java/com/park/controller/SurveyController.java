package com.park.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.PageResult;
import com.park.common.Result;
import com.park.entity.SatisfactionSurvey;
import com.park.entity.User;
import com.park.mapper.UserMapper;
import com.park.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final UserMapper userMapper;

    @PostMapping("/api/admin/survey/assign")
    public Result<Map<String, Object>> assign(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        if (userId == null) return Result.fail(400, "请选择用户");
        return Result.ok(surveyService.assign(userId));
    }

    @GetMapping("/api/admin/survey/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<SatisfactionSurvey> p = surveyService.list(page, size, status);
        List<Long> userIds = p.getRecords().stream().map(SatisfactionSurvey::getUserId).distinct().toList();
        Map<Long, String> userMap = userIds.isEmpty() ? Map.of()
                : userMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().in(User::getId, userIds))
                        .stream().collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));
        var records = p.getRecords().stream().map(s -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("surveyCode", s.getSurveyCode());
            m.put("userId", s.getUserId());
            m.put("companyName", s.getCompanyName());
            m.put("status", s.getStatus());
            m.put("assignedAt", s.getAssignedAt());
            m.put("submittedAt", s.getSubmittedAt());
            m.put("username", userMap.getOrDefault(s.getUserId(), ""));
            return m;
        }).toList();
        return Result.ok(new PageResult<>(records, p.getTotal(), page, size));
    }

    @GetMapping("/api/admin/survey/{id}")
    public Result<SatisfactionSurvey> detail(@PathVariable Long id) {
        SatisfactionSurvey s = surveyService.getById(id);
        if (s == null) return Result.fail(404, "问卷不存在");
        return Result.ok(s);
    }

    @GetMapping("/api/survey/{code}")
    public Result<SatisfactionSurvey> getByCode(@PathVariable String code) {
        SatisfactionSurvey s = surveyService.getByCode(code);
        if (s == null) return Result.fail(404, "问卷不存在或链接已失效");
        return Result.ok(s);
    }

    @PostMapping("/api/survey/{code}/submit")
    public Result<Void> submit(@PathVariable String code, @RequestBody Map<String, Object> data) {
        try {
            surveyService.submit(code, data);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }
}
