package com.prj2.booksta.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.PrivateConversation;
import com.prj2.booksta.model.PrivateMessage;
import com.prj2.booksta.model.User;

@DataJpaTest
class PrivateMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PrivateMessageRepository messageRepository;

    private User user1;
    private User user2;
    private PrivateConversation conversation;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setFirstName("A");
        user1.setLastName("A");
        user1.setEmail("a@test.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setFirstName("B");
        user2.setLastName("B");
        user2.setEmail("b@test.com");
        entityManager.persist(user2);

        conversation = new PrivateConversation();
        conversation.setParticipant1(user1);
        conversation.setParticipant2(user2);
        entityManager.persist(conversation);

        entityManager.flush();
    }

    @Test
    void testFindByConversationIdOrderBySentAtAsc() {
        createMessage("Hier", LocalDateTime.now().minusDays(1));
        createMessage("Maintenant", LocalDateTime.now());
        createMessage("Avant-hier", LocalDateTime.now().minusDays(2));

        entityManager.flush();

        List<PrivateMessage> results = messageRepository.findByConversationIdOrderBySentAtAsc(conversation.getId());

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getContent()).isEqualTo("Avant-hier");
        assertThat(results.get(1).getContent()).isEqualTo("Hier");
        assertThat(results.get(2).getContent()).isEqualTo("Maintenant");
    }

    @Test
    void testFindFirstByConversationIdOrderBySentAtDesc() {
        createMessage("Vieux", LocalDateTime.now().minusHours(2));
        createMessage("Récent", LocalDateTime.now()); // Le plus récent
        createMessage("Moyen", LocalDateTime.now().minusHours(1));
        entityManager.flush();

        PrivateMessage result = messageRepository.findFirstByConversationIdOrderBySentAtDesc(conversation.getId());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Récent");
    }

    @Test
    void testCountByConversationIdAndRecipientIdAndReadAtIsNull() {
        createMessageForRecipient(user1, null);
        createMessageForRecipient(user1, LocalDateTime.now());
        createMessageForRecipient(user2, null);

        entityManager.flush();

        long count = messageRepository.countByConversationIdAndRecipientIdAndReadAtIsNull(conversation.getId(),
                user1.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void testMarkConversationMessagesAsRead() {
        PrivateMessage msg1 = createMessageForRecipient(user1, null);
        PrivateMessage msg2 = createMessageForRecipient(user1, LocalDateTime.now().minusDays(1));
        PrivateMessage msg3 = createMessageForRecipient(user2, null);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime readTime = LocalDateTime.now();

        int updatedRows = messageRepository.markConversationMessagesAsRead(conversation.getId(), user1.getId(),
                readTime);

        assertThat(updatedRows).isEqualTo(1);

        PrivateMessage updatedMsg1 = entityManager.find(PrivateMessage.class, msg1.getId());
        assertThat(updatedMsg1.getReadAt()).isNotNull();

        PrivateMessage unchangedMsg3 = entityManager.find(PrivateMessage.class, msg3.getId());
        assertThat(unchangedMsg3.getReadAt()).isNull();
    }

    private void createMessage(String content, LocalDateTime sentAt) {
        PrivateMessage msg = new PrivateMessage();
        msg.setConversation(conversation);
        msg.setSender(user1);
        msg.setRecipient(user2);
        msg.setContent(content);
        msg.setSentAt(sentAt);
        entityManager.persist(msg);
    }

    private PrivateMessage createMessageForRecipient(User recipient, LocalDateTime readAt) {
        PrivateMessage msg = new PrivateMessage();
        msg.setConversation(conversation);
        msg.setSender(recipient == user1 ? user2 : user1);
        msg.setRecipient(recipient);
        msg.setContent("Test");
        msg.setSentAt(LocalDateTime.now());
        msg.setReadAt(readAt);
        entityManager.persist(msg);
        return msg;
    }
}