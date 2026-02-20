package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummary {
    private Long conversationId;
    private UserSummary otherUser;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}
