package com.park.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.SatisfactionSurvey;
import java.util.Map;

public interface SurveyService {
    Map<String, Object> assign(Long userId);
    SatisfactionSurvey getByCode(String code);
    void submit(String code, Map<String, Object> data);
    Page<SatisfactionSurvey> list(int page, int size, String status);
    SatisfactionSurvey getById(Long id);
}
