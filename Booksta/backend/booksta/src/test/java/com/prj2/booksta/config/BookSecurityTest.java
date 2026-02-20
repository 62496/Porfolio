package com.prj2.booksta.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.prj2.booksta.model.BookReadEvent;
import com.prj2.booksta.model.ReadingEventType;
import com.prj2.booksta.service.BookReadEventService;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.UserService;

@ExtendWith(MockitoExtension.class)
class BookSecurityTest {

    @Mock
    private UserService userService;

    @Mock
    private BookReadEventService bookReadEventService;

    @Mock
    private BookService bookService; 

    @InjectMocks
    private BookSecurity bookSecurity;

    private Authentication authentication;
    private final String EMAIL = "user@test.com";
    private final String ISBN = "12345";

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
    }

    @Test
    void testUserOwnsBook_True() {
        when(userService.userOwnsBook(EMAIL, ISBN)).thenReturn(true);

        boolean result = bookSecurity.userOwnsBook(authentication, ISBN);

        assertTrue(result, "Doit retourner vrai si le service confirme la possession");
    }

    @Test
    void testUserOwnsBook_False() {
        when(userService.userOwnsBook(EMAIL, ISBN)).thenReturn(false);

        boolean result = bookSecurity.userOwnsBook(authentication, ISBN);

        assertFalse(result, "Doit retourner faux si le service nie la possession");
    }

    @Test
    void testUserReadsBook_StartedReading_True() {
        BookReadEvent event = new BookReadEvent();
        event.setReadingEvent(ReadingEventType.STARTED_READING);

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(event);

        boolean result = bookSecurity.userReadsBook(authentication, ISBN);

        assertTrue(result, "STARTED_READING doit autoriser l'accès");
    }

    @Test
    void testUserReadsBook_RestartedReading_True() {
        BookReadEvent event = new BookReadEvent();
        event.setReadingEvent(ReadingEventType.RESTARTED_READING);

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(event);

        boolean result = bookSecurity.userReadsBook(authentication, ISBN);

        assertTrue(result, "RESTARTED_READING doit autoriser l'accès");
    }

    @Test
    void testUserReadsBook_FinishedReading_False() {
        BookReadEvent event = new BookReadEvent();
        event.setReadingEvent(ReadingEventType.FINISHED_READING);

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(event);

        boolean result = bookSecurity.userReadsBook(authentication, ISBN);

        assertFalse(result, "Une lecture terminée ne doit pas être considérée comme 'en cours'");
    }
    
    @Test
    void testUserReadsBook_AbandonedReading_False() {
        BookReadEvent event = new BookReadEvent();
        event.setReadingEvent(ReadingEventType.ABANDONED_READING); 

        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(event);

        boolean result = bookSecurity.userReadsBook(authentication, ISBN);

        assertFalse(result);
    }

    @Test
    void testUserReadsBook_NoEventFound_False() {
        when(bookReadEventService.getLatestReadEvent(EMAIL, ISBN)).thenReturn(null);

        boolean result = bookSecurity.userReadsBook(authentication, ISBN);

        assertFalse(result, "Doit gérer le null sans crasher et retourner false");
    }
}