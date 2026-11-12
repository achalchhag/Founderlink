package com.founderlink.chatservice.controller;

import com.founderlink.chatservice.entity.ChatMessage;
import com.founderlink.chatservice.service.ChatService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Direct messaging and conversation management")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    @Operation(summary = "Send a message", description = "Sends a new message to a recipient founder.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ChatMessage> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChatMessage message) {
        UUID senderId = authenticatedUserId(jwt);
        ChatMessage saved = chatService.sendMessage(senderId, message);
        return ResponseEntity.created(URI.create("/api/chat/messages/" + saved.getId())).body(saved);
    }

    @GetMapping("/messages/{otherUserId}")
    @Operation(summary = "Get conversation history", description = "Returns chronological message history with another user.")
    public ResponseEntity<List<ChatMessage>> getConversationHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID otherUserId) {
        UUID currentUserId = authenticatedUserId(jwt);
        return ResponseEntity.ok(chatService.getConversation(currentUserId, otherUserId));
    }

    @GetMapping("/my-messages")
    @Operation(summary = "List all messages for the current user")
    public ResponseEntity<List<ChatMessage>> getMyMessages(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chatService.getMyMessages(authenticatedUserId(jwt)));
    }

    @PatchMapping("/messages/{messageId}/read")
    @Operation(summary = "Mark message as read", description = "Marks a received message as read.")
    public ResponseEntity<ChatMessage> markAsRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID messageId) {
        return ResponseEntity.ok(chatService.markAsRead(authenticatedUserId(jwt), messageId));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread message count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal Jwt jwt) {
        long count = chatService.getUnreadCount(authenticatedUserId(jwt));
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
