package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.ReadingProgress;
import com.prj2.booksta.model.ReadingStatus;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.ReadingProgressRepository;
import com.prj2.booksta.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReadingProgressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReadingProgressRepository progressRepository;

    @InjectMocks
    private ReadingProgressService progressService;

    private User user;
    private Book book;
    private final String ISBN = "123456789";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Alice");

        book = new Book();
        book.setIsbn(ISBN);
        book.setTitle("Clean Code");
        book.setPages(200L);
    }

    @Test
    void testCreateProgress_Success() {
        Long currentPage = 50L;
        
        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));
        
        when(progressRepository.save(any(ReadingProgress.class)))
                .thenAnswer(invocation -> {
                    ReadingProgress saved = invocation.getArgument(0);
                    if (saved.getStatus() == null) saved.setStatus(ReadingStatus.READING);
                    return saved;
                });

        ReadingProgress result = progressService.createProgress(user, ISBN, currentPage);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        
        assertEquals(200L, result.getTotalPages()); 
        assertEquals(50L, result.getCurrentPage());
        assertEquals(25.0, result.getProgressPercent(), 0.1);
        
        assertEquals(ReadingStatus.READING, result.getStatus());

        verify(bookRepository).findById(ISBN);
        verify(progressRepository).save(any(ReadingProgress.class));
    }

    @Test
    void testCreateProgress_BookNotFound() {
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, 
            () -> progressService.createProgress(user, ISBN, 10L));
            
        verify(progressRepository, never()).save(any());
    }

    @Test
    void testUpdateProgress_Success() {
        ReadingProgress existingProgress = new ReadingProgress();
        existingProgress.setUser(user);
        existingProgress.setBook(book);
        existingProgress.setTotalPages(200L);
        existingProgress.setCurrentPage(50L);
        existingProgress.setStatus(ReadingStatus.READING);

        when(progressRepository.findByUserIdAndBookIsbn(user.getId(), ISBN))
                .thenReturn(Optional.of(existingProgress));

        when(progressRepository.save(any(ReadingProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReadingProgress result = progressService.updateProgress(user, ISBN, 100L);

        assertEquals(100L, result.getCurrentPage());
        assertEquals(50.0, result.getProgressPercent(), 0.1);
        
        verify(progressRepository).save(existingProgress);
    }

    @Test
    void testUpdateProgress_NotFound() {
        when(progressRepository.findByUserIdAndBookIsbn(user.getId(), ISBN))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> progressService.updateProgress(user, ISBN, 100L));
            
        assertEquals("Progress does not exist yet", ex.getMessage());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void testGetUserProgress() {
        when(progressRepository.findByUserId(user.getId()))
                .thenReturn(Collections.singletonList(new ReadingProgress()));

        List<ReadingProgress> results = progressService.getUserProgress(user);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(progressRepository).findByUserId(user.getId());
    }
}