package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PrivateMessageTest {

    @Test
    void testDefaultInitialization() {
        PrivateMessage message = new PrivateMessage();

        assertNotNull(message.getSentAt(), "La date d'envoi ne doit pas être null par défaut");

        assertTrue(message.getSentAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(message.getSentAt().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    void testToStringExcludesConversation() {
        PrivateConversation conversation = new PrivateConversation();
        conversation.setId(100L);

        PrivateMessage message = new PrivateMessage();
        message.setId(1L);
        message.setContent("Hello World");
        message.setConversation(conversation);

        String str = message.toString();

        assertTrue(str.contains("Hello World"));
        assertFalse(str.contains("conversation="));
    }

    @Test
    void testEqualsAndHashCodeExcludesConversation() {
        User sender = new User();
        sender.setId(1L);
        User recipient = new User();
        recipient.setId(2L);
        LocalDateTime now = LocalDateTime.now();

        PrivateConversation conv1 = new PrivateConversation();
        conv1.setId(10L);
        PrivateConversation conv2 = new PrivateConversation();
        conv2.setId(20L);

        PrivateMessage m1 = new PrivateMessage(1L, conv1, sender, recipient, "Hi", now, null);
        PrivateMessage m2 = new PrivateMessage(1L, conv2, sender, recipient, "Hi", now, null);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testSettersAndGetters() {
        PrivateMessage message = new PrivateMessage();
        message.setContent("Test Content");

        assertEquals("Test Content", message.getContent());
    }
}