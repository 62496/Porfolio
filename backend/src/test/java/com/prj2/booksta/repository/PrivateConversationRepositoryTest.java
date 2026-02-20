package com.prj2.booksta.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.PrivateConversation;
import com.prj2.booksta.model.User;

@DataJpaTest
class PrivateConversationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PrivateConversationRepository conversationRepository;

    @Test
    void testFindBetweenUsers_OrderDoesNotMatter() {
        User alice = new User();
        alice.setFirstName("Alice");
        alice.setLastName("A");
        alice.setEmail("alice@chat.com");
        entityManager.persist(alice);

        User bob = new User();
        bob.setFirstName("Bob");
        bob.setLastName("B");
        bob.setEmail("bob@chat.com");
        entityManager.persist(bob);

        PrivateConversation convo = new PrivateConversation();
        convo.setParticipant1(alice);
        convo.setParticipant2(bob);
        entityManager.persist(convo);

        entityManager.flush();

        Optional<PrivateConversation> result1 = conversationRepository.findBetweenUsers(alice.getId(), bob.getId());
        assertThat(result1).isPresent();
        assertThat(result1.get()).isEqualTo(convo);

        Optional<PrivateConversation> result2 = conversationRepository.findBetweenUsers(bob.getId(), alice.getId());
        assertThat(result2).isPresent();
        assertThat(result2.get()).isEqualTo(convo);
    }

    @Test
    void testFindAllByParticipant_FindsAsP1AndP2() {
        User me = new User();
        me.setFirstName("Me");
        me.setLastName("Me");
        me.setEmail("me@chat.com");
        entityManager.persist(me);

        User friend1 = new User();
        friend1.setFirstName("F1");
        friend1.setLastName("F1");
        friend1.setEmail("f1@chat.com");
        entityManager.persist(friend1);

        User friend2 = new User();
        friend2.setFirstName("F2");
        friend2.setLastName("F2");
        friend2.setEmail("f2@chat.com");
        entityManager.persist(friend2);

        PrivateConversation c1 = new PrivateConversation();
        c1.setParticipant1(me);
        c1.setParticipant2(friend1);
        entityManager.persist(c1);

        PrivateConversation c2 = new PrivateConversation();
        c2.setParticipant1(friend2);
        c2.setParticipant2(me);
        entityManager.persist(c2);

        entityManager.flush();

        List<PrivateConversation> results = conversationRepository.findAllByParticipant(me.getId());

        assertThat(results).hasSize(2);
        assertThat(results).contains(c1, c2);
    }

    @Test
    void testFindBetweenUsers_NotFound() {
        Optional<PrivateConversation> result = conversationRepository.findBetweenUsers(999L, 888L);
        assertThat(result).isEmpty();
    }
}