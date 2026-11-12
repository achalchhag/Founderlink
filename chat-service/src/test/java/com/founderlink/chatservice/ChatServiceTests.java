package com.founderlink.chatservice;

import com.founderlink.chatservice.entity.ChatMessage;
import com.founderlink.chatservice.repository.ChatMessageRepository;
import com.founderlink.chatservice.service.ChatServiceImpl;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTests {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void sendMessage_success() {
        UUID sender = UUID.randomUUID();
        UUID recipient = UUID.randomUUID();
        ChatMessage input = new ChatMessage(sender, recipient, "Hello, lets collaborate!");

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ChatMessage response = chatService.sendMessage(sender, input);
        assertNotNull(response);
        assertEquals(sender, response.getSenderId());
        assertEquals(recipient, response.getRecipientId());
        assertEquals("Hello, lets collaborate!", response.getContent());
    }

    @Test
    void markAsRead_success() {
        UUID msgId = UUID.randomUUID();
        UUID recipient = UUID.randomUUID();
        ChatMessage msg = new ChatMessage(msgId, UUID.randomUUID(), recipient, "Hey", false, Instant.now());

        when(chatMessageRepository.findById(msgId)).thenReturn(Optional.of(msg));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(msg);

        ChatMessage response = chatService.markAsRead(recipient, msgId);
        assertNotNull(response);
        assertTrue(response.isRead());
    }
}
