package com.park.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.dto.ServiceDetailVO;
import com.park.dto.ServiceItemQueryDTO;
import com.park.dto.ServiceItemVO;
import com.park.entity.ServiceCategory;
import java.util.List;

public interface ServiceCatalogService {
    List<ServiceCategory> listCategories();
    Page<ServiceItemVO> pageItems(ServiceItemQueryDTO query);
    ServiceDetailVO getDetail(Long id);
}
