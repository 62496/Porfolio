package com.prj2.booksta.controller;

import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ConversationSummary;
import com.prj2.booksta.model.dto.MarkMessagesReadRequest;
import com.prj2.booksta.model.dto.MessageResponse;
import com.prj2.booksta.model.dto.SendMessageRequest;
import com.prj2.booksta.service.PrivateMessagingService;
import com.prj2.booksta.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private PrivateMessagingService messagingService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request,@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(request);
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            MessageResponse response = messagingService.sendMessage(
                    user.getId(),
                    request.getRecipientId(),
                    request.getContent()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> listConversations(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user= userService.getUserByEmail(userDetails.getUsername());
            List<ConversationSummary> conversations = messagingService.listConversations(user.getId());
            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<?> getConversationMessages(@PathVariable Long conversationId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            List<MessageResponse> messages = messagingService.getConversationMessages(conversationId, user.getId());
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<?> markConversationAsRead(@PathVariable Long conversationId,
                                                    @AuthenticationPrincipal UserDetails userDetails
                                        
                                                    ) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            messagingService.markConversationAsRead(conversationId, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
