package com.prj2.booksta.repository;

import com.prj2.booksta.model.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Long> {

    /**
     * Retrouve une conversation privée entre deux utilisateurs,
     * quels que soient les rôles (participant1 / participant2).
     */
    @Query("""
        SELECT c FROM PrivateConversation c
        WHERE (c.participant1.id = :user1Id AND c.participant2.id = :user2Id)
           OR (c.participant1.id = :user2Id AND c.participant2.id = :user1Id)
    """)
    Optional<PrivateConversation> findBetweenUsers(@Param("user1Id") Long user1Id,
                                                   @Param("user2Id") Long user2Id);

    /**
     * Retourne toutes les conversations où l'utilisateur intervient
     * (en tant que participant1 ou participant2).
     */
    @Query("""
        SELECT DISTINCT c FROM PrivateConversation c
        WHERE c.participant1.id = :userId OR c.participant2.id = :userId
    """)
    List<PrivateConversation> findAllByParticipant(@Param("userId") Long userId);
}
