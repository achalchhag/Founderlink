package com.founderlink.notificationservice.service;

import com.founderlink.notificationservice.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification createNotification(Notification notification);

    List<Notification> getMyNotifications(UUID userId);

    Notification getById(UUID notificationId);

    long getUnreadCount(UUID userId);

    Notification markAsRead(UUID userId, UUID notificationId);

    int markAllAsRead(UUID userId);

    void deleteNotification(UUID userId, UUID notificationId);
}
