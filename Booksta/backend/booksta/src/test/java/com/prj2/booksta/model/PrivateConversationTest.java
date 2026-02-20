package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class PrivateConversationTest {

    @Test
    void testInvolvesUser_ReturnsTrueForParticipants() {
        User p1 = new User();
        p1.setId(10L);

        User p2 = new User();
        p2.setId(20L);

        PrivateConversation conversation = new PrivateConversation();
        conversation.setParticipant1(p1);
        conversation.setParticipant2(p2);

        assertTrue(conversation.involvesUser(10L), "Doit retourner vrai pour le participant 1");
        assertTrue(conversation.involvesUser(20L), "Doit retourner vrai pour le participant 2");
    }

    @Test
    void testInvolvesUser_ReturnsFalseForOutsider() {
        User p1 = new User();
        p1.setId(10L);
        User p2 = new User();
        p2.setId(20L);

        PrivateConversation conversation = new PrivateConversation(1L, p1, p2, new ArrayList<>());

        assertFalse(conversation.involvesUser(99L), "Doit retourner faux pour un utilisateur tiers");
    }

    @Test
    void testInvolvesUser_HandlesNullParticipantsSafely() {
        PrivateConversation conversation = new PrivateConversation();

        conversation.setParticipant1(null);
        conversation.setParticipant2(null);
        assertFalse(conversation.involvesUser(1L));

        User p1 = new User();
        p1.setId(1L);
        conversation.setParticipant1(p1);
        conversation.setParticipant2(null);

        assertFalse(conversation.involvesUser(1L),
                "Doit retourner false si la conversation n'est pas complète (participant2 est null)");

        User p2 = new User();
        p2.setId(2L);
        conversation.setParticipant2(p2);

        assertTrue(conversation.involvesUser(1L), "Doit retourner true maintenant que les 2 participants sont là");
    }

    @Test
    void testDefaultMessagesListIsNotNull() {
        PrivateConversation conversation = new PrivateConversation();

        assertNotNull(conversation.getMessages());
        assertTrue(conversation.getMessages().isEmpty());
    }

    @Test
    void testEqualsAndHashCodeExcludesMessages() {
        User p1 = new User();
        p1.setId(1L);
        User p2 = new User();
        p2.setId(2L);

        PrivateConversation c1 = new PrivateConversation(1L, p1, p2, new ArrayList<>());
        PrivateConversation c2 = new PrivateConversation(1L, p1, p2, new ArrayList<>());

        c1.getMessages().add(new PrivateMessage());

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToStringExcludesMessages() {
        PrivateConversation conversation = new PrivateConversation();
        conversation.setId(55L);
        conversation.getMessages().add(new PrivateMessage());

        String str = conversation.toString();

        assertTrue(str.contains("55"));
        assertFalse(str.contains("messages="));
    }
}