package com.founderlink.notificationservice.controller;

import com.founderlink.notificationservice.entity.Notification;
import com.founderlink.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "User notifications and alert management")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Create notification", description = "Triggers a notification for a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification notification) {
        Notification saved = notificationService.createNotification(notification);
        return ResponseEntity.created(URI.create("/api/notifications/" + saved.getId())).body(saved);
    }

    @GetMapping
    @Operation(summary = "Get my notifications", description = "Returns all notifications for the authenticated user.")
    public ResponseEntity<List<Notification>> getMyNotifications(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(notificationService.getMyNotifications(authenticatedUserId(jwt)));
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<Notification> getById(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.getById(notificationId));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notifications count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal Jwt jwt) {
        long count = notificationService.getUnreadCount(authenticatedUserId(jwt));
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Notification> markAsRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(authenticatedUserId(jwt), notificationId));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        int updated = notificationService.markAllAsRead(authenticatedUserId(jwt));
        return ResponseEntity.ok(Map.of("markedReadCount", updated));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID notificationId) {
        notificationService.deleteNotification(authenticatedUserId(jwt), notificationId);
        return ResponseEntity.noContent().build();
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
