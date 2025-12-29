package com.prj2.booksta.repository;

import com.prj2.booksta.model.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    /**
     * Tous les messages d'une conversation, triés par date d'envoi (du plus ancien au plus récent).
     */
    List<PrivateMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);

    /**
     * Nombre de messages non lus dans une conversation pour un destinataire donné.
     */
    long countByConversationIdAndRecipientIdAndReadAtIsNull(Long conversationId, Long recipientId);

    /**
     * Marque tous les messages non lus d'une conversation comme lus pour un utilisateur donné.
     * Retourne le nombre de lignes mises à jour.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE PrivateMessage m
        SET m.readAt = :readAt
        WHERE m.conversation.id = :conversationId
          AND m.recipient.id = :userId
          AND m.readAt IS NULL
    """)
    int markConversationMessagesAsRead(@Param("conversationId") Long conversationId,
                                       @Param("userId") Long userId,
                                       @Param("readAt") LocalDateTime readAt);

    /**
     * Dernier message envoyé dans une conversation (le plus récent).
     * Peut retourner null si aucun message.
     */
    PrivateMessage findFirstByConversationIdOrderBySentAtDesc(Long conversationId);
}
