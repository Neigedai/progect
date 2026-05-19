package com.park.service;

import com.park.common.PageResult;
import com.park.dto.ServiceItemQueryDTO;
import com.park.entity.ServiceCategory;
import com.park.entity.ServiceItem;
import java.util.List;

public interface AdminServiceService {
    // Category
    List<ServiceCategory> listCategories();
    void createCategory(ServiceCategory category);
    void updateCategory(ServiceCategory category);
    void deleteCategory(Long id);

    // Item
    PageResult<ServiceItem> pageItems(ServiceItemQueryDTO query);
    ServiceItem getItemById(Long id);
    void createItem(ServiceItem item);
    void updateItem(ServiceItem item);
    void deleteItem(Long id);
    void updateItemStatus(Long id, Integer status);
}
