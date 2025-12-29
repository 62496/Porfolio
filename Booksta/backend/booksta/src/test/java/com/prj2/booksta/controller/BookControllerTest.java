package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.exception.GlobalExceptionHandler;
import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.Subject;
import com.prj2.booksta.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageService imageService;

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @Mock
    private BookReadEventService bookReadEventService;

    @Mock
    private ReadingSessionService readingSessionService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;
    private Author testAuthor;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("John");
        testAuthor.setLastName("Doe");

        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setName("Fiction");

        testBook = new Book();
        testBook.setIsbn("9781234567890");
        testBook.setTitle("Test Book");
        testBook.setPublishingYear(2023);
        testBook.setDescription("A test book description");
        testBook.setAuthors(new HashSet<>(Set.of(testAuthor)));
        testBook.setSubjects(new HashSet<>(Set.of(testSubject)));
        testBook.setPages(300L);
        testBook.setImage(new Image("http://example.com/cover.jpg"));
    }

    @Nested
    @DisplayName("GET /api/books tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return empty list when no books exist")
        void getAllBooks_NoBooksExist_ReturnsEmptyList() throws Exception {
            when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(bookService, times(1)).getAllBooks();
        }

        @Test
        @DisplayName("Should return all books when books exist")
        void getAllBooks_BooksExist_ReturnsAllBooks() throws Exception {
            Book book2 = new Book();
            book2.setIsbn("9789876543210");
            book2.setTitle("Another Book");
            book2.setPublishingYear(2022);
            book2.setDescription("Another description");
            book2.setAuthors(new HashSet<>());
            book2.setSubjects(new HashSet<>());

            when(bookService.getAllBooks()).thenReturn(Arrays.asList(testBook, book2));

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].isbn").value("9781234567890"))
                    .andExpect(jsonPath("$[1].isbn").value("9789876543210"));

            verify(bookService, times(1)).getAllBooks();
        }
    }

    @Nested
    @DisplayName("GET /api/books/{isbn} tests")
    class GetBookByIsbnTests {

        @Test
        @DisplayName("Should return book when ISBN exists")
        void getBookByIsbn_BookExists_ReturnsBook() throws Exception {
            when(bookService.getBookByIsbn("9781234567890")).thenReturn(testBook);

            mockMvc.perform(get("/api/books/9781234567890"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.isbn").value("9781234567890"))
                    .andExpect(jsonPath("$.title").value("Test Book"))
                    .andExpect(jsonPath("$.publishingYear").value(2023))
                    .andExpect(jsonPath("$.description").value("A test book description"))
                    .andExpect(jsonPath("$.pages").value(300));

            verify(bookService).getBookByIsbn("9781234567890");
        }

        @Test
        @DisplayName("Should return 400 when ISBN does not exist")
        void getBookByIsbn_BookNotExists_Returns400() throws Exception {
            when(bookService.getBookByIsbn("nonexistent"))
                    .thenThrow(new IllegalArgumentException("Book with isbn not found: nonexistent"));

            mockMvc.perform(get("/api/books/nonexistent"))
                    .andExpect(status().isBadRequest());

            verify(bookService).getBookByIsbn("nonexistent");
        }
    }

    @Nested
    @DisplayName("GET /api/books/search tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Should search by title")
        void searchBooks_ByTitle_ReturnsMatchingBooks() throws Exception {
            when(bookService.searchBooks("Test", null, null, null))
                    .thenReturn(Collections.singletonList(testBook));

            mockMvc.perform(get("/api/books/search")
                            .param("title", "Test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Book"));

            verify(bookService).searchBooks("Test", null, null, null);
        }

        @Test
        @DisplayName("Should search by author name")
        void searchBooks_ByAuthorName_ReturnsMatchingBooks() throws Exception {
            when(bookService.searchBooks(null, "John", null, null))
                    .thenReturn(Collections.singletonList(testBook));

            mockMvc.perform(get("/api/books/search")
                            .param("authorName", "John"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(bookService).searchBooks(null, "John", null, null);
        }

        @Test
        @DisplayName("Should search by subject")
        void searchBooks_BySubject_ReturnsMatchingBooks() throws Exception {
            when(bookService.searchBooks(null, null, "Fiction", null))
                    .thenReturn(Collections.singletonList(testBook));

            mockMvc.perform(get("/api/books/search")
                            .param("subjectName", "Fiction"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(bookService).searchBooks(null, null, "Fiction", null);
        }

        @Test
        @DisplayName("Should search by year")
        void searchBooks_ByYear_ReturnsMatchingBooks() throws Exception {
            when(bookService.searchBooks(null, null, null, 2023))
                    .thenReturn(Collections.singletonList(testBook));

            mockMvc.perform(get("/api/books/search")
                            .param("year", "2023"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(bookService).searchBooks(null, null, null, 2023);
        }

        @Test
        @DisplayName("Should search by multiple criteria")
        void searchBooks_ByMultipleCriteria_ReturnsMatchingBooks() throws Exception {
            when(bookService.searchBooks("Test", "John", "Fiction", 2023))
                    .thenReturn(Collections.singletonList(testBook));

            mockMvc.perform(get("/api/books/search")
                            .param("title", "Test")
                            .param("authorName", "John")
                            .param("subjectName", "Fiction")
                            .param("year", "2023"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(bookService).searchBooks("Test", "John", "Fiction", 2023);
        }

        @Test
        @DisplayName("Should return empty list when no matches")
        void searchBooks_NoMatches_ReturnsEmptyList() throws Exception {
            when(bookService.searchBooks("Nonexistent", null, null, null))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/books/search")
                            .param("title", "Nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /api/books/{isbn} tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book successfully")
        void deleteBook_BookExists_ReturnsNoContent() throws Exception {
            doNothing().when(bookService).delete("9781234567890");

            mockMvc.perform(delete("/api/books/9781234567890"))
                    .andExpect(status().isNoContent());

            verify(bookService).delete("9781234567890");
        }

        @Test
        @DisplayName("Should return 404 when book not found")
        void deleteBook_BookNotFound_Returns404() throws Exception {
            doThrow(new EntityNotFoundException("Book not found"))
                    .when(bookService).delete("nonexistent");

            mockMvc.perform(delete("/api/books/nonexistent"))
                    .andExpect(status().isNotFound());

            verify(bookService).delete("nonexistent");
        }
    }

    @Nested
    @DisplayName("GET /api/books/series/{seriesId} tests")
    class GetBooksBySeriesTests {

        @Test
        @DisplayName("Should return books in series")
        void getBooksBySeries_BooksExist_ReturnsBooks() throws Exception {
            when(bookService.findBySeriesId(1L)).thenReturn(List.of(testBook));

            mockMvc.perform(get("/api/books/series/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].isbn").value("9781234567890"));

            verify(bookService).findBySeriesId(1L);
        }

        @Test
        @DisplayName("Should return empty list when no books in series")
        void getBooksBySeries_NoBooksInSeries_ReturnsEmptyList() throws Exception {
            when(bookService.findBySeriesId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/books/series/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/books/author/{authorId} tests")
    class GetBooksByAuthorTests {

        @Test
        @DisplayName("Should return books by author")
        void getBooksByAuthor_BooksExist_ReturnsBooks() throws Exception {
            when(bookService.findByAuthorId(1L)).thenReturn(List.of(testBook));

            mockMvc.perform(get("/api/books/author/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].isbn").value("9781234567890"));

            verify(bookService).findByAuthorId(1L);
        }

        @Test
        @DisplayName("Should return empty list when author has no books")
        void getBooksByAuthor_NoBooksForAuthor_ReturnsEmptyList() throws Exception {
            when(bookService.findByAuthorId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/books/author/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
}
