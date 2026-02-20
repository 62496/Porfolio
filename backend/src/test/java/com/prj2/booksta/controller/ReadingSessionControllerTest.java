package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.ReadingSession;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ReadingSessionCreate;
import com.prj2.booksta.model.dto.ReadingSessionUpdate;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.ReadingSessionService;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReadingSessionControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ReadingSessionService readingSessionService;

    @Mock
    private BookService bookService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReadingSessionController readingSessionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final String EMAIL = "user@test.com";
    private final String ISBN = "12345";
    private final Long SESSION_ID = 10L;

    private User user;
    private Book book;
    private ReadingSession session;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(readingSessionController).build();

        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);

        book = new Book();
        book.setIsbn(ISBN);

        session = new ReadingSession();
        session.setId(SESSION_ID);
        session.setUser(user);
        session.setBook(book);
    }

    @Test
    void testCreateSession() throws Exception {
        ReadingSessionCreate request = new ReadingSessionCreate(ISBN, 10);

        when(authentication.getName()).thenReturn(EMAIL);
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(readingSessionService.createSession(user, book, 10)).thenReturn(session);

        mockMvc.perform(post("/api/reading-sessions")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SESSION_ID));

        verify(readingSessionService).createSession(user, book, 10);
    }

    @Test
    void testEndReadingSession() throws Exception {
        ReadingSessionUpdate request = new ReadingSessionUpdate(10, 50, "Great book");

        when(authentication.getName()).thenReturn(EMAIL);
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);

        session.setEndPage(50);
        
        when(readingSessionService.endReadingSession(eq(user), eq(SESSION_ID), eq(10), eq(50), eq("Great book")))
                .thenReturn(session);

        mockMvc.perform(put("/api/reading-sessions/{id}/end", SESSION_ID) 
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endPage").value(50));

        verify(readingSessionService).endReadingSession(user, SESSION_ID, 10, 50, "Great book");
    }


    @Test
    void testPauseReadingSession() throws Exception {
        when(authentication.getName()).thenReturn(EMAIL);
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        
        when(readingSessionService.pauseReadingSession(user, SESSION_ID)).thenReturn(session);

        mockMvc.perform(put("/api/reading-sessions/{id}/pause", SESSION_ID)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SESSION_ID));

        verify(readingSessionService).pauseReadingSession(user, SESSION_ID);
    }

    @Test
    void testResumeReadingSession() throws Exception {
        when(authentication.getName()).thenReturn(EMAIL);
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        
        when(readingSessionService.resumeReadingSession(user, SESSION_ID)).thenReturn(session);

        mockMvc.perform(put("/api/reading-sessions/{id}/resume", SESSION_ID)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SESSION_ID));

        verify(readingSessionService).resumeReadingSession(user, SESSION_ID);
    }

    @Test
    void testDeleteSession_Success() throws Exception {
        doNothing().when(readingSessionService).deleteSession(SESSION_ID);

        mockMvc.perform(delete("/api/reading-sessions/{id}", SESSION_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted"));

        verify(readingSessionService).deleteSession(SESSION_ID);
    }
}