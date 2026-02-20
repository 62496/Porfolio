package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prj2.booksta.exception.InvalidReadingEventTransitionException;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.BookReadEvent;
import com.prj2.booksta.model.ReadingEventType;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookReadEventRepository;

@ExtendWith(MockitoExtension.class)
class BookReadEventServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private BookReadEventRepository bookReadEventRepository;

    @InjectMocks
    private BookReadEventService bookReadEventService;

    private User user;
    private Book book;
    private final String EMAIL = "test@test.com";
    private final String ISBN = "12345";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);

        book = new Book();
        book.setIsbn(ISBN);
    }

    @Test
    void testCreateReadEvent_FirstStart_Success() {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.empty()); 

        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.STARTED_READING);

        assertNotNull(result);
        assertEquals(ReadingEventType.STARTED_READING, result.getReadingEvent());
        assertNotNull(result.getOccurredAt());
        verify(bookReadEventRepository).save(any(BookReadEvent.class));
    }

    @Test
    void testCreateReadEvent_FinishReading_Success() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.STARTED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.FINISHED_READING);

        assertEquals(ReadingEventType.FINISHED_READING, result.getReadingEvent());
    }

    @Test
    void testCreateReadEvent_AbandonReading_Success() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.STARTED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.ABANDONED_READING);

        assertEquals(ReadingEventType.ABANDONED_READING, result.getReadingEvent());
    }

    @Test
    void testCreateReadEvent_RestartAfterFinish_Success() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.FINISHED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.RESTARTED_READING);

        assertEquals(ReadingEventType.RESTARTED_READING, result.getReadingEvent());
    }
    
    @Test
    void testCreateReadEvent_RestartAfterAbandon_Success() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.ABANDONED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.RESTARTED_READING);

        assertEquals(ReadingEventType.RESTARTED_READING, result.getReadingEvent());
    }
    
    @Test
    void testCreateReadEvent_FinishAfterRestart_Success() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.RESTARTED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));
        when(bookReadEventRepository.save(any(BookReadEvent.class))).thenAnswer(i -> i.getArgument(0));

        BookReadEvent result = bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.FINISHED_READING);

        assertEquals(ReadingEventType.FINISHED_READING, result.getReadingEvent());
    }

    @Test
    void testCreateReadEvent_InvalidStart_ThrowsException() {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.empty());

        assertThrows(InvalidReadingEventTransitionException.class, () -> 
            bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.FINISHED_READING)
        );
        
        verify(bookReadEventRepository, never()).save(any());
    }

    @Test
    void testCreateReadEvent_InvalidDoubleStart_ThrowsException() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.STARTED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        assertThrows(InvalidReadingEventTransitionException.class, () -> 
            bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.STARTED_READING)
        );
    }
    
    @Test
    void testCreateReadEvent_InvalidFinishedToFinished_ThrowsException() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.FINISHED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        assertThrows(InvalidReadingEventTransitionException.class, () -> 
            bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.FINISHED_READING)
        );
    }

    @Test
    void testCreateReadEvent_InvalidFinishedToStart_ThrowsException() {
        BookReadEvent lastEvent = new BookReadEvent();
        lastEvent.setReadingEvent(ReadingEventType.FINISHED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(lastEvent));

        assertThrows(InvalidReadingEventTransitionException.class, () -> 
            bookReadEventService.createReadEvent(EMAIL, ISBN, ReadingEventType.STARTED_READING)
        );
    }

    @Test
    void testGetLatestReadEvent_Found() {
        BookReadEvent event = new BookReadEvent();
        event.setReadingEvent(ReadingEventType.STARTED_READING);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.of(event));

        BookReadEvent result = bookReadEventService.getLatestReadEvent(EMAIL, ISBN);

        assertNotNull(result);
        assertEquals(ReadingEventType.STARTED_READING, result.getReadingEvent());
    }

    @Test
    void testGetLatestReadEvent_NotFound() {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(bookService.getBookByIsbn(ISBN)).thenReturn(book);
        when(bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book))
                .thenReturn(Optional.empty());

        BookReadEvent result = bookReadEventService.getLatestReadEvent(EMAIL, ISBN);

        assertNull(result);
    }

    @Test
    void testFindByUserAndIsbn() {
        BookReadEvent event = new BookReadEvent();
        when(bookReadEventRepository.findByUser_IdAndBook_IsbnOrderByOccurredAtDesc(1L, ISBN))
                .thenReturn(Arrays.asList(event));

        List<BookReadEvent> result = bookReadEventService.findByUserAndIsbn(1L, ISBN);

        assertEquals(1, result.size());
        verify(bookReadEventRepository).findByUser_IdAndBook_IsbnOrderByOccurredAtDesc(1L, ISBN);
    }
}