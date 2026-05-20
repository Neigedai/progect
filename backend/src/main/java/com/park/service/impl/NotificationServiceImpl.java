package com.park.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.entity.Notification;
import com.park.entity.NotificationRead;
import com.park.mapper.NotificationMapper;
import com.park.mapper.NotificationReadMapper;
import com.park.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper mapper;
    private final NotificationReadMapper readMapper;

    @Override
    public void sendToUser(Long userId, String type, String title, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setIsRead(0);
        mapper.insert(n);
    }

    @Override
    public void sendToAll(String type, String title, String content) {
        Notification n = new Notification();
        n.setUserId(0L);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setIsRead(0);
        mapper.insert(n);
    }

    @Override
    public Page<Notification> listByUser(Long userId, int page, int size, String type, Integer isRead) {
        // 查个人通知
        LambdaQueryWrapper<Notification> personalWrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        if (type != null && !type.isBlank()) personalWrapper.eq(Notification::getType, type);

        // 查系统公告
        LambdaQueryWrapper<Notification> broadcastWrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, 0L)
                .orderByDesc(Notification::getCreateTime);
        if (type != null && !type.isBlank()) broadcastWrapper.eq(Notification::getType, type);

        List<Notification> personalList = mapper.selectList(personalWrapper);
        List<Notification> broadcastList = mapper.selectList(broadcastWrapper);

        // 标记系统公告已读状态
        if (!broadcastList.isEmpty()) {
            List<Long> broadcastIds = broadcastList.stream().map(Notification::getId).toList();
            List<NotificationRead> reads = readMapper.selectList(
                    new LambdaQueryWrapper<NotificationRead>()
                            .eq(NotificationRead::getUserId, userId)
                            .in(NotificationRead::getNotificationId, broadcastIds)
            );
            Set<Long> readIds = reads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());
            for (Notification n : broadcastList) {
                n.setIsRead(readIds.contains(n.getId()) ? 1 : 0);
            }
        }

        // 合并+过滤+排序+分页
        List<Notification> merged = new ArrayList<>();
        merged.addAll(personalList);
        merged.addAll(broadcastList);
        merged.sort((a, b) -> {
            if (a.getCreateTime() == null || b.getCreateTime() == null) return 0;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        // isRead 过滤
        if (isRead != null) {
            merged = merged.stream().filter(n -> isRead.equals(n.getIsRead())).toList();
        }

        // type 过滤已在上游完成

        // 手动分页
        long total = merged.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, merged.size());
        List<Notification> paged = from < merged.size() ? merged.subList(from, to) : Collections.emptyList();

        Page<Notification> result = new Page<>(page, size, total);
        result.setRecords(paged);
        return result;
    }

    @Override
    public long getUnreadCount(Long userId) {
        // 个人未读
        long personalUnread = mapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );

        // 系统公告总数
        long broadcastTotal = mapper.selectCount(
                new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, 0L)
        );

        // 系统公告已读数
        long broadcastRead = readMapper.selectCount(
                new LambdaQueryWrapper<NotificationRead>().eq(NotificationRead::getUserId, userId)
        );

        return personalUnread + Math.max(0, broadcastTotal - broadcastRead);
    }

    @Override
    public void markRead(Long userId, Long id) {
        Notification n = mapper.selectById(id);
        if (n == null) return;

        if (n.getUserId() != null && n.getUserId() == 0L) {
            // 系统公告 → 写已读记录
            NotificationRead read = new NotificationRead();
            read.setNotificationId(id);
            read.setUserId(userId);
            try {
                readMapper.insert(read);
            } catch (Exception ignored) {
                // 已存在则忽略(唯一键冲突)
            }
        } else if (n.getUserId() != null && n.getUserId().equals(userId)) {
            // 个人通知 → 更新 is_read
            Notification update = new Notification();
            update.setId(id);
            update.setIsRead(1);
            mapper.update(update, new LambdaUpdateWrapper<Notification>()
                    .eq(Notification::getId, id)
                    .eq(Notification::getUserId, userId));
        }
    }

    @Override
    public void markAllRead(Long userId) {
        // 个人通知全部已读
        Notification update = new Notification();
        update.setIsRead(1);
        mapper.update(update, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));

        // 系统公告全部已读
        List<Notification> broadcasts = mapper.selectList(
                new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, 0L)
        );
        if (!broadcasts.isEmpty()) {
            List<NotificationRead> existing = readMapper.selectList(
                    new LambdaQueryWrapper<NotificationRead>().eq(NotificationRead::getUserId, userId)
            );
            Set<Long> readIds = existing.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());

            for (Notification b : broadcasts) {
                if (!readIds.contains(b.getId())) {
                    NotificationRead read = new NotificationRead();
                    read.setNotificationId(b.getId());
                    read.setUserId(userId);
                    readMapper.insert(read);
                }
            }
        }
    }

    @Override
    public Page<Notification> listAll(int page, int size, String type) {
        Page<Notification> p = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .orderByDesc(Notification::getCreateTime);
        if (type != null && !type.isBlank()) {
            wrapper.eq(Notification::getType, type);
        }
        return mapper.selectPage(p, wrapper);
    }

    @Override
    public void create(Notification notification) {
        notification.setIsRead(0);
        if (notification.getUserId() == null || notification.getUserId() == 0) {
            sendToAll(notification.getType(), notification.getTitle(), notification.getContent());
        } else {
            mapper.insert(notification);
        }
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
        // 同时删除已读记录
        readMapper.delete(new LambdaQueryWrapper<NotificationRead>()
                .eq(NotificationRead::getNotificationId, id));
    }
}
