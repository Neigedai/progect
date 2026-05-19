package com.park.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ServiceDetailVO {
    private Long id;
    private Long categoryId;
    private String serviceName;
    private String summary;
    private String applicableEnterprise;
    private String detailDesc;
    private String categoryName;
    private Integer status;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String stepsJson;
    private String priceInfoJson;

    public List<Map<String, String>> getSteps() {
        if (stepsJson == null) return Collections.emptyList();
        try {
            return MAPPER.readValue(stepsJson, new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public Map<String, String> getPriceInfo() {
        if (priceInfoJson == null) return Collections.emptyMap();
        try {
            return MAPPER.readValue(priceInfoJson, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
}
