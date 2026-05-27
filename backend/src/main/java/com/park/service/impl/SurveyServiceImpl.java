package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.SatisfactionSurvey;
import com.park.mapper.SatisfactionSurveyMapper;
import com.park.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SatisfactionSurveyMapper mapper;

    @Override
    public Map<String, Object> assign(Long userId) {
        String code = UUID.randomUUID().toString().replace("-", "");
        SatisfactionSurvey s = new SatisfactionSurvey();
        s.setSurveyCode(code);
        s.setUserId(userId);
        s.setStatus("pending");
        s.setAssignedAt(LocalDateTime.now());
        mapper.insert(s);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", s.getId());
        result.put("surveyCode", code);
        result.put("userId", userId);
        result.put("status", "pending");
        return result;
    }

    @Override
    public SatisfactionSurvey getByCode(String code) {
        return mapper.selectOne(new LambdaQueryWrapper<SatisfactionSurvey>()
                .eq(SatisfactionSurvey::getSurveyCode, code));
    }

    @Override
    public void submit(String code, Map<String, Object> data) {
        SatisfactionSurvey s = getByCode(code);
        if (s == null) throw new RuntimeException("问卷不存在");
        if ("completed".equals(s.getStatus())) throw new RuntimeException("问卷已提交");

        s.setCompanyName((String) data.get("companyName"));
        s.setIndustry((String) data.get("industry"));
        s.setEmployeeCount(data.get("employeeCount") != null ? Integer.parseInt(data.get("employeeCount").toString()) : null);
        s.setContactName((String) data.get("contactName"));
        s.setContactPhone((String) data.get("contactPhone"));

        s.setQ1(toInt(data.get("q1")));
        s.setQ2(toInt(data.get("q2")));
        s.setQ3(toInt(data.get("q3")));
        s.setQ4(toInt(data.get("q4")));
        s.setQ5(toInt(data.get("q5")));
        s.setQ6(toInt(data.get("q6")));
        s.setQ7(toInt(data.get("q7")));
        s.setQ8(toInt(data.get("q8")));
        s.setQ9(toInt(data.get("q9")));
        s.setQ10(toInt(data.get("q10")));
        s.setQ11((String) data.get("q11"));

        s.setStatus("completed");
        s.setSubmittedAt(LocalDateTime.now());
        mapper.updateById(s);
    }

    private Integer toInt(Object v) {
        if (v == null) return null;
        return Integer.parseInt(v.toString());
    }

    @Override
    public Page<SatisfactionSurvey> list(int page, int size, String status) {
        LambdaQueryWrapper<SatisfactionSurvey> w = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) w.eq(SatisfactionSurvey::getStatus, status);
        w.orderByDesc(SatisfactionSurvey::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), w);
    }

    @Override
    public SatisfactionSurvey getById(Long id) {
        return mapper.selectById(id);
    }
}
