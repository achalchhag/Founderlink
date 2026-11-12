package com.founderlink.chatservice.service;

import com.founderlink.chatservice.entity.ChatMessage;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    ChatMessage sendMessage(UUID senderId, ChatMessage message);

    List<ChatMessage> getConversation(UUID currentUserId, UUID otherUserId);

    List<ChatMessage> getMyMessages(UUID currentUserId);

    ChatMessage markAsRead(UUID currentUserId, UUID messageId);

    long getUnreadCount(UUID currentUserId);
}
