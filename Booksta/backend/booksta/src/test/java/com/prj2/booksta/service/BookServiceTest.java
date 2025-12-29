package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookReportRepository bookReportRepository;

    @Mock
    private BookReadEventRepository bookReadEventRepository;

    @Mock
    private ReadingSessionRepository readingSessionRepository;

    @Mock
    private ReadingProgressRepository readingProgressRepository;

    @Mock
    private UserBookInventoryRepository userBookInventoryRepository;

    @Mock
    private BookCollectionRepository bookCollectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageService imageService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private Author testAuthor;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
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
    }

    @Nested
    @DisplayName("getAllBooks tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return empty list when no books exist")
        void getAllBooks_WhenNoBooksExist_ReturnsEmptyList() {
            when(bookRepository.findAll()).thenReturn(Collections.emptyList());

            List<Book> result = bookService.getAllBooks();

            assertTrue(result.isEmpty());
            verify(bookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return all books when books exist")
        void getAllBooks_WhenBooksExist_ReturnsAllBooks() {
            Book book2 = new Book();
            book2.setIsbn("9789876543210");
            book2.setTitle("Another Book");

            when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook, book2));

            List<Book> result = bookService.getAllBooks();

            assertEquals(2, result.size());
            verify(bookRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getBookByIsbn tests")
    class GetBookByIsbnTests {

        @Test
        @DisplayName("Should return book when ISBN exists")
        void getBookByIsbn_WhenIsbnExists_ReturnsBook() {
            when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(testBook));

            Book result = bookService.getBookByIsbn("9781234567890");

            assertNotNull(result);
            assertEquals("9781234567890", result.getIsbn());
            assertEquals("Test Book", result.getTitle());
            verify(bookRepository, times(1)).findById("9781234567890");
        }

        @Test
        @DisplayName("Should throw exception when ISBN does not exist")
        void getBookByIsbn_WhenIsbnNotExists_ThrowsException() {
            when(bookRepository.findById("nonexistent")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> bookService.getBookByIsbn("nonexistent")
            );

            assertTrue(exception.getMessage().contains("nonexistent"));
            verify(bookRepository, times(1)).findById("nonexistent");
        }
    }

    @Nested
    @DisplayName("searchBooks tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Should search by title only")
        void searchBooks_ByTitleOnly_ReturnsMatchingBooks() {
            when(bookRepository.searchBooks("Test", null, null, null))
                    .thenReturn(Collections.singletonList(testBook));

            List<Book> result = bookService.searchBooks("Test", null, null, null);

            assertEquals(1, result.size());
            assertEquals("Test Book", result.get(0).getTitle());
            verify(bookRepository).searchBooks("Test", null, null, null);
        }

        @Test
        @DisplayName("Should search by multiple criteria")
        void searchBooks_ByMultipleCriteria_ReturnsMatchingBooks() {
            when(bookRepository.searchBooks("Test", "John", "Fiction", 2023))
                    .thenReturn(Collections.singletonList(testBook));

            List<Book> result = bookService.searchBooks("Test", "John", "Fiction", 2023);

            assertEquals(1, result.size());
            verify(bookRepository).searchBooks("Test", "John", "Fiction", 2023);
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void searchBooks_NoMatches_ReturnsEmptyList() {
            when(bookRepository.searchBooks("Nonexistent", null, null, null))
                    .thenReturn(Collections.emptyList());

            List<Book> result = bookService.searchBooks("Nonexistent", null, null, null);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("save tests")
    class SaveTests {

        @Test
        @DisplayName("Should save and return book")
        void save_ValidBook_ReturnsSavedBook() {
            when(bookRepository.save(testBook)).thenReturn(testBook);

            Book result = bookService.save(testBook);

            assertNotNull(result);
            assertEquals(testBook.getIsbn(), result.getIsbn());
            verify(bookRepository, times(1)).save(testBook);
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete book and all related entities")
        void delete_ExistingBook_DeletesBookAndRelatedEntities() throws Exception {
            String isbn = "9781234567890";
            testBook.setImage(new Image("http://example.com/image.png"));

            when(bookRepository.findById(isbn)).thenReturn(Optional.of(testBook));
            when(bookCollectionRepository.findByBooksIsbn(isbn)).thenReturn(Collections.emptyList());
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            bookService.delete(isbn);

            verify(bookReportRepository).deleteByBook_Isbn(isbn);
            verify(bookReadEventRepository).deleteByBook_Isbn(isbn);
            verify(readingSessionRepository).deleteByBook_Isbn(isbn);
            verify(readingProgressRepository).deleteByBook_Isbn(isbn);
            verify(userBookInventoryRepository).deleteByBook_Isbn(isbn);
            verify(bookRepository).delete(testBook);
        }

        @Test
        @DisplayName("Should throw exception when book not found")
        void delete_NonExistentBook_ThrowsException() {
            when(bookRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> bookService.delete("nonexistent"));

            verify(bookRepository, never()).delete(any(Book.class));
        }

        @Test
        @DisplayName("Should remove book from collections")
        void delete_BookInCollections_RemovesFromCollections() throws Exception {
            String isbn = "9781234567890";
            BookCollection collection = new BookCollection();
            collection.setId(1L);
            collection.setBooks(new HashSet<>(Set.of(testBook)));

            when(bookRepository.findById(isbn)).thenReturn(Optional.of(testBook));
            when(bookCollectionRepository.findByBooksIsbn(isbn)).thenReturn(List.of(collection));
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            bookService.delete(isbn);

            verify(bookCollectionRepository).save(collection);
            assertFalse(collection.getBooks().contains(testBook));
        }

        @Test
        @DisplayName("Should remove book from user favorites and owned books")
        void delete_BookInUserFavorites_RemovesFromFavorites() throws Exception {
            String isbn = "9781234567890";
            User user = new User();
            user.setId(1L);
            user.setFavoriteList(new HashSet<>(Set.of(testBook)));
            user.setOwnedBooks(new HashSet<>(Set.of(testBook)));

            when(bookRepository.findById(isbn)).thenReturn(Optional.of(testBook));
            when(bookCollectionRepository.findByBooksIsbn(isbn)).thenReturn(Collections.emptyList());
            when(userRepository.findAll()).thenReturn(List.of(user));

            bookService.delete(isbn);

            verify(userRepository).save(user);
            assertFalse(user.getFavoriteList().contains(testBook));
            assertFalse(user.getOwnedBooks().contains(testBook));
        }
    }

    @Nested
    @DisplayName("findBySeriesId tests")
    class FindBySeriesIdTests {

        @Test
        @DisplayName("Should return books in series")
        void findBySeriesId_BooksExist_ReturnsBooks() {
            when(bookRepository.findBySeries_Id(1L)).thenReturn(List.of(testBook));

            List<Book> result = bookService.findBySeriesId(1L);

            assertEquals(1, result.size());
            verify(bookRepository).findBySeries_Id(1L);
        }

        @Test
        @DisplayName("Should return empty list when no books in series")
        void findBySeriesId_NoBooksInSeries_ReturnsEmptyList() {
            when(bookRepository.findBySeries_Id(1L)).thenReturn(Collections.emptyList());

            List<Book> result = bookService.findBySeriesId(1L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByAuthorId tests")
    class FindByAuthorIdTests {

        @Test
        @DisplayName("Should return books by author")
        void findByAuthorId_BooksExist_ReturnsBooks() {
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(List.of(testBook));

            List<Book> result = bookService.findByAuthorId(1L);

            assertEquals(1, result.size());
            verify(bookRepository).findByAuthors_Id(1L);
        }
    }

    @Nested
    @DisplayName("isBookOwnedByAuthor tests")
    class IsBookOwnedByAuthorTests {

        @Test
        @DisplayName("Should return true when author owns book")
        void isBookOwnedByAuthor_AuthorOwnsBook_ReturnsTrue() {
            when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(testBook));

            boolean result = bookService.isBookOwnedByAuthor(1L, "9781234567890");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when author does not own book")
        void isBookOwnedByAuthor_AuthorDoesNotOwnBook_ReturnsFalse() {
            when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(testBook));

            boolean result = bookService.isBookOwnedByAuthor(999L, "9781234567890");

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when book not found")
        void isBookOwnedByAuthor_BookNotFound_ReturnsFalse() {
            when(bookRepository.findById("nonexistent")).thenReturn(Optional.empty());

            boolean result = bookService.isBookOwnedByAuthor(1L, "nonexistent");

            assertFalse(result);
        }
    }
}
