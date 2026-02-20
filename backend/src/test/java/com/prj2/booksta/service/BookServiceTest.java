package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.UpdateBook;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prj2.booksta.repository.BookRepository;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("123");
        book.setTitle("Test Book");
        book.setAuthors(new HashSet<>()); 
        book.setSubjects(new HashSet<>());
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));
        List<Book> result = bookService.getAllBooks();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void testGetBookByIsbnFound() {
        when(bookRepository.findById("123")).thenReturn(Optional.of(book));
        Book result = bookService.getBookByIsbn("123");
        assertNotNull(result);
        assertEquals("123", result.getIsbn());
    }

    @Test
    void testGetBookByIsbnNotFound() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.getBookByIsbn("999"));
    }

    @Test
    void testSaveBook() {
        when(bookRepository.save(book)).thenReturn(book);
        Book savedBook = bookService.save(book);
        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
    }

    @Test
    void testSearchBooks() {
        when(bookRepository.searchBooks(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        List<Book> result = bookService.searchBooks("Harry", "Rowling", "Fantasy", 2000);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteBook() {
        bookService.delete("123");
        verify(bookRepository).deleteById("123");
    }

    @Test
    void testFindBySeriesId() {
        when(bookRepository.findBySeries_Id(10L)).thenReturn(Collections.singletonList(book));
        List<Book> result = bookService.findBySeriesId(10L);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByAuthorId() {
        when(bookRepository.findByAuthors_Id(50L)).thenReturn(Collections.singletonList(book));
        List<Book> result = bookService.findByAuthorId(50L);
        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateBook_Success_WithFile() throws IOException {
        UpdateBook dto = new UpdateBook(2023, 300L, "New Title", "Desc", null, null, null);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        when(bookRepository.findById("123")).thenReturn(Optional.of(book));
        when(fileStorageService.saveBookImage(mockFile, "123")).thenReturn("path/to/image.jpg");
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        Book updated = bookService.updateBook(dto, "123", mockFile);

        assertEquals("New Title", updated.getTitle());
        assertEquals("Desc", updated.getDescription());
        assertNotNull(updated.getImage());
        assertEquals("path/to/image.jpg", updated.getImage().getUrl());

        verify(imageService).createImage(any(Image.class));
        verify(bookRepository).save(book);
    }

    @Test
    void testUpdateBook_Success_WithImageUrl() {
        UpdateBook dto = new UpdateBook(2023, 300L, "New Title", "Desc", null, null, "http://url.com/img.png");
        
        when(bookRepository.findById("123")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        Book updated = bookService.updateBook(dto, "123", null);

        assertNotNull(updated.getImage());
        assertEquals("http://url.com/img.png", updated.getImage().getUrl());
        verify(imageService).createImage(any(Image.class));
    }

    @Test
    void testUpdateBook_WithAuthorsAndSubjects() {
        List<Long> authorIds = Arrays.asList(1L, 2L);
        List<Long> subjectIds = Collections.singletonList(5L);
        UpdateBook dto = new UpdateBook(2023, 300L, "Title", "Desc", authorIds, subjectIds, null);

        Set<Author> authors = new HashSet<>(); authors.add(new Author());
        Set<Subject> subjects = new HashSet<>(); subjects.add(new Subject());

        when(bookRepository.findById("123")).thenReturn(Optional.of(book));
        when(authorService.findAllById(authorIds)).thenReturn(authors);
        when(subjectService.findAllById(subjectIds)).thenReturn(subjects);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        Book updated = bookService.updateBook(dto, "123", null);

        assertEquals(1, updated.getAuthors().size());
        assertEquals(1, updated.getSubjects().size());
    }

    @Test
    void testUpdateBook_NotFound() {
        UpdateBook dto = new UpdateBook(2023, 300L, "Title", "Desc", null, null, null);
        when(bookRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            bookService.updateBook(dto, "999", null)
        );
        
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    void testUpdateBook_FileIOException() throws IOException {
        UpdateBook dto = new UpdateBook(2023, 300L, "Title", "Desc", null, null, null);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        when(bookRepository.findById("123")).thenReturn(Optional.of(book));
        when(fileStorageService.saveBookImage(mockFile, "123")).thenThrow(new IOException("Disk full"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            bookService.updateBook(dto, "123", mockFile)
        );
        
        assertEquals("Failed to update image", ex.getMessage());
    }

    @Test
    void testIsBookOwnedByAuthor_True() {
        Author author = new Author(); author.setId(10L);
        book.getAuthors().add(author);

        when(bookRepository.findById("123")).thenReturn(Optional.of(book));

        assertTrue(bookService.isBookOwnedByAuthor(10L, "123"));
    }

    @Test
    void testIsBookOwnedByAuthor_False() {
        Author author = new Author(); author.setId(10L);
        book.getAuthors().add(author);

        when(bookRepository.findById("123")).thenReturn(Optional.of(book));

        assertFalse(bookService.isBookOwnedByAuthor(20L, "123"));
    }

    @Test
    void testIsBookOwnedByAuthor_BookNotFound() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());
        
        assertFalse(bookService.isBookOwnedByAuthor(10L, "999"));
    }
}