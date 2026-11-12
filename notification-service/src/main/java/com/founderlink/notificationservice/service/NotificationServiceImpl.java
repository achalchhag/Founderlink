package com.founderlink.notificationservice.service;

import com.founderlink.notificationservice.entity.Notification;
import com.founderlink.notificationservice.exception.ResourceNotFoundException;
import com.founderlink.notificationservice.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getMyNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getById(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public Notification markAsRead(UUID userId, UUID notificationId) {
        Notification notification = getById(notificationId);
        if (notification.getUserId().equals(userId)) {
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return notification;
    }

    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID userId, UUID notificationId) {
        Notification notification = getById(notificationId);
        if (notification.getUserId().equals(userId)) {
            notificationRepository.delete(notification);
        }
    }
}
