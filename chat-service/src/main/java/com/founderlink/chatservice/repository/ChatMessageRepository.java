package com.founderlink.chatservice.repository;

import com.founderlink.chatservice.entity.ChatMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :userA AND m.recipientId = :userB) OR " +
            "(m.senderId = :userB AND m.recipientId = :userA) " +
            "ORDER BY m.createdAt ASC")
    List<ChatMessage> findConversationHistory(@Param("userA") UUID userA, @Param("userB") UUID userB);

    List<ChatMessage> findBySenderIdOrRecipientIdOrderByCreatedAtDesc(UUID senderId, UUID recipientId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);
}
