package com.prj2.booksta.service;

import com.prj2.booksta.model.PrivateConversation;
import com.prj2.booksta.model.PrivateMessage;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ConversationSummary;
import com.prj2.booksta.model.dto.MessageResponse;
import com.prj2.booksta.repository.PrivateConversationRepository;
import com.prj2.booksta.repository.PrivateMessageRepository;
import com.prj2.booksta.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivateMessagingServiceTest {

    @Mock
    private PrivateConversationRepository conversationRepository;
    @Mock
    private PrivateMessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PrivateMessagingService messagingService;

    private User sender;
    private User recipient;
    private PrivateConversation conversation;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setFirstName("Alice");
        sender.setLastName("Smith");
        sender.setEmail("alice@test.com");

        recipient = new User();
        recipient.setId(2L);
        recipient.setFirstName("Bob");
        recipient.setLastName("Jones");
        recipient.setEmail("bob@test.com");

        conversation = new PrivateConversation();
        conversation.setId(10L);
        conversation.setParticipant1(sender);
        conversation.setParticipant2(recipient);
    }

    @Test
    void testSendMessage_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));

        when(conversationRepository.findBetweenUsers(1L, 2L)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(PrivateConversation.class))).thenReturn(conversation);

        when(messageRepository.save(any(PrivateMessage.class))).thenAnswer(i -> {
            PrivateMessage msg = i.getArgument(0);
            msg.setId(99L);
            return msg;
        });

        MessageResponse response = messagingService.sendMessage(1L, 2L, "Hello Bob");

        assertNotNull(response);
        assertEquals("Hello Bob", response.getContent());
        assertEquals(1L, response.getSender().getId());
        assertEquals(2L, response.getRecipient().getId());

        verify(conversationRepository).save(any(PrivateConversation.class));
        verify(messageRepository).save(any(PrivateMessage.class));
    }

    @Test
    void testSendMessage_SelfMessage_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> messagingService.sendMessage(1L, 1L, "Self talk"));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void testSendMessage_EmptyContent_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> messagingService.sendMessage(1L, 2L, "   "));
    }

    @Test
    void testListConversations() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(conversationRepository.findAllByParticipant(1L))
                .thenReturn(Collections.singletonList(conversation));

        when(messageRepository.findFirstByConversationIdOrderBySentAtDesc(10L)).thenReturn(null);
        when(messageRepository.countByConversationIdAndRecipientIdAndReadAtIsNull(10L, 1L)).thenReturn(0L);

        List<ConversationSummary> summaries = messagingService.listConversations(1L);

        assertEquals(1, summaries.size());
        assertEquals(2L, summaries.get(0).getOtherUser().getId());
    }

    @Test
    void testGetConversationMessages_Success() {
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        PrivateMessage msg = new PrivateMessage(100L, conversation, sender, recipient, "Hi", LocalDateTime.now(), null);
        when(messageRepository.findByConversationIdOrderBySentAtAsc(10L))
                .thenReturn(Collections.singletonList(msg));

        List<MessageResponse> messages = messagingService.getConversationMessages(10L, 1L);

        assertEquals(1, messages.size());
        assertEquals("Hi", messages.get(0).getContent());
    }

    @Test
    void testGetConversationMessages_AccessDenied() {
        User intruder = new User();
        intruder.setId(3L);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> messagingService.getConversationMessages(10L, 3L));

        assertEquals("User does not belong to this conversation", ex.getMessage());
    }

    @Test
    void testMarkConversationAsRead() {
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        messagingService.markConversationAsRead(10L, 1L);

        verify(messageRepository).markConversationMessagesAsRead(eq(10L), eq(1L), any(LocalDateTime.class));
    }
}