package com.founderlink.chatservice.service;

import com.founderlink.chatservice.entity.ChatMessage;
import com.founderlink.chatservice.exception.ResourceNotFoundException;
import com.founderlink.chatservice.repository.ChatMessageRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(UUID senderId, ChatMessage message) {
        message.setSenderId(senderId);
        message.setRead(false);
        return chatMessageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getConversation(UUID currentUserId, UUID otherUserId) {
        return chatMessageRepository.findConversationHistory(currentUserId, otherUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getMyMessages(UUID currentUserId) {
        return chatMessageRepository.findBySenderIdOrRecipientIdOrderByCreatedAtDesc(currentUserId, currentUserId);
    }

    @Override
    @Transactional
    public ChatMessage markAsRead(UUID currentUserId, UUID messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + messageId));

        if (message.getRecipientId().equals(currentUserId)) {
            message.setRead(true);
            return chatMessageRepository.save(message);
        }

        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID currentUserId) {
        return chatMessageRepository.countByRecipientIdAndIsReadFalse(currentUserId);
    }
}
