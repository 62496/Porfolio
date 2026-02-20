package com.prj2.booksta.service;

import com.prj2.booksta.model.PrivateConversation;
import com.prj2.booksta.model.PrivateMessage;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ConversationSummary;
import com.prj2.booksta.model.dto.MessageResponse;
import com.prj2.booksta.model.dto.UserSummary;
import com.prj2.booksta.repository.PrivateConversationRepository;
import com.prj2.booksta.repository.PrivateMessageRepository;
import com.prj2.booksta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PrivateMessagingService {

    private final PrivateConversationRepository conversationRepository;
    private final PrivateMessageRepository messageRepository;
    private final UserRepository userRepository;

    public PrivateMessagingService(PrivateConversationRepository conversationRepository,
                                   PrivateMessageRepository messageRepository,
                                   UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // ----------- API publique -----------

    @Transactional
    public MessageResponse sendMessage(Long senderId, Long recipientId, String content) {
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("You cannot send a message to yourself");
        }

        User sender = getUserOrThrow(senderId, "Sender not found");
        User recipient = getUserOrThrow(recipientId, "Recipient not found");

        PrivateConversation conversation = findOrCreateConversation(sender, recipient);

        PrivateMessage message = new PrivateMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content.trim());
        message.setSentAt(LocalDateTime.now());

        PrivateMessage saved = messageRepository.save(message);
        return toMessageResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ConversationSummary> listConversations(Long userId) {
        // Vérifier que l'utilisateur existe
        getUserOrThrow(userId, "User not found");

        List<PrivateConversation> conversations =
                conversationRepository.findAllByParticipant(userId);

        return conversations.stream()
                .map(conversation -> toConversationSummary(conversation, userId))
                .sorted(Comparator
                        .comparing(ConversationSummary::getLastMessageAt,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessages(Long conversationId, Long userId) {
        PrivateConversation conversation = getConversationOrThrow(conversationId);

        if (!conversation.involvesUser(userId)) {
            throw new IllegalArgumentException("User does not belong to this conversation");
        }

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        PrivateConversation conversation = getConversationOrThrow(conversationId);

        if (!conversation.involvesUser(userId)) {
            throw new IllegalArgumentException("User does not belong to this conversation");
        }

        messageRepository.markConversationMessagesAsRead(
                conversationId,
                userId,
                LocalDateTime.now()
        );
    }

    // ----------- Helpers internes -----------

    private User getUserOrThrow(Long userId, String messageIfMissing) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(messageIfMissing));
    }

    private PrivateConversation getConversationOrThrow(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
    }

    private PrivateConversation findOrCreateConversation(User sender, User recipient) {
        // On impose un ordre stable pour éviter les doublons (1,2) et (2,1)
        User first = sender.getId() < recipient.getId() ? sender : recipient;
        User second = sender.getId() < recipient.getId() ? recipient : sender;

        return conversationRepository
                .findBetweenUsers(first.getId(), second.getId())
                .orElseGet(() -> {
                    PrivateConversation conversation = new PrivateConversation();
                    conversation.setParticipant1(first);
                    conversation.setParticipant2(second);
                    return conversationRepository.save(conversation);
                });
    }

    private ConversationSummary toConversationSummary(PrivateConversation conversation, Long currentUserId) {
        // Déterminer l'autre utilisateur dans la conversation
        User otherUser = conversation.getParticipant1().getId().equals(currentUserId)
                ? conversation.getParticipant2()
                : conversation.getParticipant1();

        // Dernier message envoyé dans cette conversation
        PrivateMessage lastMessage =
                messageRepository.findFirstByConversationIdOrderBySentAtDesc(conversation.getId());

        long unreadCount = messageRepository
                .countByConversationIdAndRecipientIdAndReadAtIsNull(
                        conversation.getId(),
                        currentUserId
                );

        return new ConversationSummary(
                conversation.getId(),
                toUserSummary(otherUser),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getSentAt() : null,
                unreadCount
        );
    }

    private MessageResponse toMessageResponse(PrivateMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                toUserSummary(message.getSender()),
                toUserSummary(message.getRecipient()),
                message.getContent(),
                message.getSentAt(),
                message.getReadAt()
        );
    }

    private UserSummary toUserSummary(User user) {
        return new UserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPicture()
        );
    }
}
