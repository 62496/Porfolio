package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ConversationSummary;
import com.prj2.booksta.model.dto.MessageResponse;
import com.prj2.booksta.model.dto.SendMessageRequest;
import com.prj2.booksta.model.dto.UserSummary;
import com.prj2.booksta.service.PrivateMessagingService;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

        @Mock
        private PrivateMessagingService messagingService;

        @Mock
        private UserService userService;

        @Mock
        private UserDetails userDetails;

        @InjectMocks
        private MessageController messageController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User mockUser;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                mockUser = new User();
                mockUser.setId(1L);
                mockUser.setEmail("test@test.com");

                mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                                        @Override
                                        public boolean supportsParameter(MethodParameter parameter) {
                                                return UserDetails.class.isAssignableFrom(parameter.getParameterType());
                                        }

                                        @Override
                                        public Object resolveArgument(MethodParameter parameter,
                                                        ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                                        WebDataBinderFactory binderFactory) {
                                                return userDetails;
                                        }
                                })
                                .build();
        }

        @Test
        void testSendMessage_Success() throws Exception {
                SendMessageRequest request = new SendMessageRequest();
                request.setRecipientId(2L);
                request.setContent("Hello");

                MessageResponse response = new MessageResponse(
                                100L, 10L,
                                new UserSummary(1L, "A", "B", "a@b.com", null),
                                new UserSummary(2L, "C", "D", "c@d.com", null),
                                "Hello", LocalDateTime.now(), null);

                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);
                when(messagingService.sendMessage(eq(1L), eq(2L), eq("Hello"))).thenReturn(response);

                mockMvc.perform(post("/api/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.content").value("Hello"));

                verify(messagingService).sendMessage(1L, 2L, "Hello");
        }

        @Test
        void testSendMessage_BadRequest() throws Exception {
                SendMessageRequest request = new SendMessageRequest();
                request.setRecipientId(1L);
                request.setContent("Self");

                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                when(messagingService.sendMessage(anyLong(), anyLong(), anyString()))
                                .thenThrow(new IllegalArgumentException("Error message"));

                mockMvc.perform(post("/api/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Error message"));
        }

        @Test
        void testListConversations_Success() throws Exception {
                ConversationSummary summary = new ConversationSummary(
                                10L,
                                new UserSummary(2L, "Bob", "D", "b@b.com", null), // Ici aussi
                                "Hi", LocalDateTime.now(), 1);

                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                when(messagingService.listConversations(1L))
                                .thenReturn(Collections.singletonList(summary));

                mockMvc.perform(get("/api/messages/conversations")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].conversationId").value(10L));
        }

        @Test
        void testListConversations_NotFound() throws Exception {
                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                when(messagingService.listConversations(1L))
                                .thenThrow(new IllegalArgumentException("User not found"));

                mockMvc.perform(get("/api/messages/conversations"))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("User not found"));
        }

        @Test
        void testGetConversationMessages_Success() throws Exception {
                MessageResponse msg = new MessageResponse();
                msg.setId(50L);
                msg.setContent("Test Content");

                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                when(messagingService.getConversationMessages(10L, 1L))
                                .thenReturn(Collections.singletonList(msg));

                mockMvc.perform(get("/api/messages/conversations/{conversationId}", 10L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].content").value("Test Content"));
        }

        @Test
        void testGetConversationMessages_BadRequest() throws Exception {
                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                when(messagingService.getConversationMessages(10L, 1L))
                                .thenThrow(new IllegalArgumentException("Access denied"));

                mockMvc.perform(get("/api/messages/conversations/{conversationId}", 10L))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Access denied"));
        }

        @Test
        void testMarkConversationAsRead_Success() throws Exception {
                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                doNothing().when(messagingService).markConversationAsRead(10L, 1L);

                mockMvc.perform(post("/api/messages/conversations/{conversationId}/read", 10L))
                                .andExpect(status().isOk());

                verify(messagingService).markConversationAsRead(10L, 1L);
        }

        @Test
        void testMarkConversationAsRead_BadRequest() throws Exception {
                when(userDetails.getUsername()).thenReturn("test@test.com");
                when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

                doThrow(new IllegalArgumentException("Error"))
                                .when(messagingService).markConversationAsRead(10L, 1L);

                mockMvc.perform(post("/api/messages/conversations/{conversationId}/read", 10L))
                                .andExpect(status().isBadRequest());
        }
}