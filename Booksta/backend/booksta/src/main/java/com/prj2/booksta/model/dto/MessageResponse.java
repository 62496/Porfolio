package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private UserSummary sender;
    private UserSummary recipient;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}
