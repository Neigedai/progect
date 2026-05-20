package com.park.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.Notification;

public interface NotificationService {
    void sendToUser(Long userId, String type, String title, String content);
    void sendToAll(String type, String title, String content);
    Page<Notification> listByUser(Long userId, int page, int size, String type, Integer isRead);
    long getUnreadCount(Long userId);
    void markRead(Long userId, Long id);
    void markAllRead(Long userId);
    Page<Notification> listAll(int page, int size, String type);
    void create(Notification notification);
    void delete(Long id);
}
