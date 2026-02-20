package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.prj2.booksta.exception.UserNotReadingBookException;
import com.prj2.booksta.model.*;
import com.prj2.booksta.repository.ReadingSessionRepository;

@ExtendWith(MockitoExtension.class)
class ReadingSessionServiceTest {

    @Mock
    private ReadingSessionRepository readingSessionRepository;

    @Mock
    private BookReadEventService bookReadEventService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReadingSessionService readingSessionService;

    private User user;
    private Book book;
    private ReadingSession session;
    private final String EMAIL = "test@test.com";
    private final String ISBN = "12345";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);

        book = new Book();
        book.setIsbn(ISBN);
        book.setPages(300L);
        
        session = new ReadingSession();
        session.setId(10L);
        session.setUser(user);
        session.setBook(book);
        session.setStartPage(10);
        session.setStartedAt(Instant.now().minusSeconds(3600)); 
        session.setLastResumedAt(Instant.now().minusSeconds(3600)); 
        session.setTotalActiveSeconds(0L);
        session.setStatus(ReadingSessionStatus.ACTIVE);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateSession_Success() {
        BookReadEvent latestEvent = new BookReadEvent();
        latestEvent.setReadingEvent(ReadingEventType.STARTED_READING);

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(latestEvent);
        when(readingSessionRepository.save(any(ReadingSession.class))).thenAnswer(i -> i.getArgument(0));

        ReadingSession result = readingSessionService.createSession(user, book, 10);

        assertNotNull(result);
        assertEquals(ReadingSessionStatus.ACTIVE, result.getStatus());
        assertEquals(10, result.getStartPage());
        assertNotNull(result.getStartedAt());
        
        verify(readingSessionRepository).save(any(ReadingSession.class));
    }

    @Test
    void testCreateSession_UserNotReading_ThrowsException() {
        BookReadEvent latestEvent = new BookReadEvent();
        latestEvent.setReadingEvent(ReadingEventType.FINISHED_READING);

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(latestEvent);

        assertThrows(UserNotReadingBookException.class, () -> 
            readingSessionService.createSession(user, book, 10)
        );

        verify(readingSessionRepository, never()).save(any());
    }

    @Test
    void testDeleteSession_Success() throws AccessDeniedException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);

        readingSessionService.deleteSession(10L);

        verify(readingSessionRepository).delete(session);
    }

    @Test
    void testDeleteSession_AccessDenied_NotOwner() {
        String otherEmail = "hacker@test.com";
        User hacker = new User(); hacker.setId(2L); hacker.setEmail(otherEmail);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(otherEmail);
        SecurityContextHolder.setContext(securityContext);

        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userService.getUserByEmail(otherEmail)).thenReturn(hacker);

        assertThrows(AccessDeniedException.class, () -> readingSessionService.deleteSession(10L));
    }

    @Test
    void testDeleteSession_NotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(readingSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> readingSessionService.deleteSession(99L));
    }

    // --- END SESSION ---

    @Test
    void testEndReadingSession_Success() throws AccessDeniedException {
        // IMPORTANT : session.setLastResumedAt doit être non-null (fait dans setUp)
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(readingSessionRepository.save(any(ReadingSession.class))).thenAnswer(i -> i.getArgument(0));

        ReadingSession result = readingSessionService.endReadingSession(user, 10L, null, 50, "Good chapter");

        assertEquals(ReadingSessionStatus.FINISHED, result.getStatus());
        assertEquals(50, result.getEndPage());
        assertEquals("Good chapter", result.getNote());
        assertNotNull(result.getEndedAt());
        
        // Vérifie que le temps actif a été calculé (devrait être > 0 car lastResumedAt était il y a 1h)
        assertTrue(result.getTotalActiveSeconds() > 0);
    }

    @Test
    void testEndReadingSession_Success_UpdateStartPage() throws AccessDeniedException {
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(readingSessionRepository.save(any(ReadingSession.class))).thenAnswer(i -> i.getArgument(0));

        ReadingSession result = readingSessionService.endReadingSession(user, 10L, 15, 20, null);

        assertEquals(15, result.getStartPage());
        assertEquals(20, result.getEndPage());
    }

    @Test
    void testEndReadingSession_NotFound() {
        when(readingSessionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> 
            readingSessionService.endReadingSession(user, 99L, 10, 20, "Note")
        );
    }

    @Test
    void testEndReadingSession_NotOwner() {
        User otherUser = new User(); otherUser.setId(2L);
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

        assertThrows(AccessDeniedException.class, () -> 
            readingSessionService.endReadingSession(otherUser, 10L, 10, 20, "Note")
        );
    }

    @Test
    void testEndReadingSession_AlreadyFinished() {
        session.setStatus(ReadingSessionStatus.FINISHED);
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> 
            readingSessionService.endReadingSession(user, 10L, 10, 20, "Note")
        );
    }

    @Test
    void testEndReadingSession_InvalidPageOrder() {
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            readingSessionService.endReadingSession(user, 10L, null, 5, "Note")
        );
        assertTrue(ex.getMessage().contains("Start page cannot be greater than end page"));
    }

    @Test
    void testEndReadingSession_EndPageExceedsTotal() {
        when(readingSessionRepository.findById(10L)).thenReturn(Optional.of(session));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            readingSessionService.endReadingSession(user, 10L, null, 500, "Note")
        );
        assertTrue(ex.getMessage().contains("cannot be greater than book total pages"));
    }

    @Test
    void testFindByUserAndIsbn() {
        when(readingSessionRepository.findByUserAndBookIsbn(user, ISBN))
                .thenReturn(Collections.singletonList(session));

        List<ReadingSession> result = readingSessionService.findByUserAndIsbn(user, ISBN);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}