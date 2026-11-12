package com.founderlink.notificationservice;

import com.founderlink.notificationservice.entity.Notification;
import com.founderlink.notificationservice.entity.NotificationType;
import com.founderlink.notificationservice.repository.NotificationRepository;
import com.founderlink.notificationservice.service.NotificationServiceImpl;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotification_success() {
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification(
                userId,
                "New Match Found!",
                "You have a 94% match with Alex Rivera",
                NotificationType.MATCH
        );

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Notification response = notificationService.createNotification(notification);
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("New Match Found!", response.getTitle());
        assertEquals(NotificationType.MATCH, response.getType());
    }
}
