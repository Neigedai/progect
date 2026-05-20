package com.park.service;

import com.park.entity.FloatingMenuItem;
import java.util.List;

public interface FloatingMenuService {
    List<FloatingMenuItem> listEnabled();
    List<FloatingMenuItem> listAll();
    FloatingMenuItem getById(Long id);
    void save(FloatingMenuItem item);
    void update(FloatingMenuItem item);
    void delete(Long id);
}
