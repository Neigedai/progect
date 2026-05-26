package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.entity.*;
import com.park.mapper.*;
import com.park.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EnterpriseAuthMapper enterpriseAuthMapper;
    private final ResidencyApplicationMapper residencyApplicationMapper;
    private final ServiceApplicationMapper serviceApplicationMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final UserMapper userMapper;
    private final CmsArticleMapper cmsArticleMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        // KPI cards
        long enterpriseCount = enterpriseAuthMapper.selectCount(
                new LambdaQueryWrapper<EnterpriseAuth>().eq(EnterpriseAuth::getAuthStatus, "approved"));
        long residencyCount = residencyApplicationMapper.selectCount(null);
        long serviceAppCount = serviceApplicationMapper.selectCount(null);
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        long serviceItemCount = serviceItemMapper.selectCount(new LambdaQueryWrapper<ServiceItem>().eq(ServiceItem::getStatus, 1));

        result.put("enterpriseCount", enterpriseCount);
        result.put("residencyCount", residencyCount);
        result.put("serviceAppCount", serviceAppCount);
        result.put("userCount", userCount);
        result.put("serviceItemCount", serviceItemCount);

        // Enterprise by tag
        List<EnterpriseAuth> allEnterprises = enterpriseAuthMapper.selectList(null);
        Map<String, Long> enterpriseByTag = allEnterprises.stream()
                .filter(e -> e.getTag() != null && !e.getTag().isEmpty())
                .collect(Collectors.groupingBy(EnterpriseAuth::getTag, Collectors.counting()));
        result.put("enterpriseByTag", toNameValueList(enterpriseByTag));

        // Enterprise by status
        Map<String, Long> enterpriseByStatus = allEnterprises.stream()
                .collect(Collectors.groupingBy(EnterpriseAuth::getAuthStatus, Collectors.counting()));
        result.put("enterpriseByStatus", toNameValueList(enterpriseByStatus));

        // Residency by industry
        List<ResidencyApplication> allResidency = residencyApplicationMapper.selectList(null);
        Map<String, Long> residencyByIndustry = allResidency.stream()
                .collect(Collectors.groupingBy(ResidencyApplication::getIndustryType, Collectors.counting()));
        result.put("residencyByIndustry", toNameValueList(residencyByIndustry));

        // Residency by status
        Map<String, Long> residencyByStatus = allResidency.stream()
                .collect(Collectors.groupingBy(ResidencyApplication::getStatus, Collectors.counting()));
        result.put("residencyByStatus", toNameValueList(residencyByStatus));

        // Residency trend (by month, last 6 months)
        List<Map<String, Object>> residencyTrend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            long count = allResidency.stream()
                    .filter(r -> r.getCreateTime() != null
                            && !r.getCreateTime().toLocalDate().isBefore(monthStart)
                            && r.getCreateTime().toLocalDate().isBefore(monthEnd))
                    .count();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("month", monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            point.put("count", count);
            residencyTrend.add(point);
        }
        result.put("residencyTrend", residencyTrend);

        // Top services
        List<ServiceApplication> allServiceApps = serviceApplicationMapper.selectList(null);
        Map<String, Long> serviceTop = allServiceApps.stream()
                .collect(Collectors.groupingBy(ServiceApplication::getServiceName, Collectors.counting()));
        List<Map<String, Object>> topServices = serviceTop.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                }).collect(Collectors.toList());
        result.put("serviceTop", topServices);

        // User registration trend (last 6 months)
        List<User> allUsers = userMapper.selectList(null);
        List<Map<String, Object>> userTrend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            long count = allUsers.stream()
                    .filter(u -> u.getCreateTime() != null
                            && !u.getCreateTime().toLocalDate().isBefore(monthStart)
                            && u.getCreateTime().toLocalDate().isBefore(monthEnd))
                    .count();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("month", monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            point.put("count", count);
            userTrend.add(point);
        }
        result.put("userTrend", userTrend);

        return result;
    }

    private List<Map<String, Object>> toNameValueList(Map<String, Long> map) {
        return map.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                }).collect(Collectors.toList());
    }
}
