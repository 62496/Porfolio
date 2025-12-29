package com.prj2.booksta.service;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.repository.AuthorRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.SeriesRepository;
import com.prj2.booksta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SeriesService seriesService;

    private Series testSeries;
    private Author testAuthor;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("author@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("John");
        testAuthor.setLastName("Doe");
        testAuthor.setUser(testUser);
        testAuthor.setFollowers(new HashSet<>());

        testSeries = new Series();
        testSeries.setId(1L);
        testSeries.setTitle("Test Series");
        testSeries.setDescription("A test series description");
        testSeries.setAuthor(testAuthor);
        testSeries.setBooks(new LinkedHashSet<>());
        testSeries.setFollowers(new HashSet<>());

        testBook = new Book();
        testBook.setIsbn("9781234567890");
        testBook.setTitle("Test Book");
        testBook.setPublishingYear(2023);
    }

    private void mockSecurityContext(String email, String... roles) {
        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn(email);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        lenient().doReturn(authorities).when(authentication).getAuthorities();

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("getAllSeries tests")
    class GetAllSeriesTests {

        @Test
        @DisplayName("Should return all series")
        void getAllSeries_SeriesExist_ReturnsAllSeries() {
            Series series2 = new Series();
            series2.setId(2L);
            series2.setTitle("Another Series");
            series2.setAuthor(testAuthor);
            series2.setBooks(new LinkedHashSet<>());
            series2.setFollowers(new HashSet<>());

            when(seriesRepository.findAll()).thenReturn(Arrays.asList(testSeries, series2));

            List<SeriesResponse> result = seriesService.getAllSeries();

            assertEquals(2, result.size());
            verify(seriesRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no series exist")
        void getAllSeries_NoSeriesExist_ReturnsEmptyList() {
            when(seriesRepository.findAll()).thenReturn(Collections.emptyList());

            List<SeriesResponse> result = seriesService.getAllSeries();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getSeriesByAuthorId tests")
    class GetSeriesByAuthorIdTests {

        @Test
        @DisplayName("Should return series for author")
        void getSeriesByAuthorId_SeriesExist_ReturnsSeries() {
            when(seriesRepository.findByAuthorId(1L)).thenReturn(List.of(testSeries));

            List<SeriesResponse> result = seriesService.getSeriesByAuthorId(1L);

            assertEquals(1, result.size());
            assertEquals("Test Series", result.get(0).getTitle());
        }

        @Test
        @DisplayName("Should return empty list when author has no series")
        void getSeriesByAuthorId_NoSeries_ReturnsEmptyList() {
            when(seriesRepository.findByAuthorId(1L)).thenReturn(Collections.emptyList());

            List<SeriesResponse> result = seriesService.getSeriesByAuthorId(1L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getSeriesById tests")
    class GetSeriesByIdTests {

        @Test
        @DisplayName("Should return series when found")
        void getSeriesById_SeriesExists_ReturnsSeries() {
            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            SeriesResponse result = seriesService.getSeriesById(1L);

            assertNotNull(result);
            assertEquals("Test Series", result.getTitle());
        }

        @Test
        @DisplayName("Should throw exception when series not found")
        void getSeriesById_SeriesNotFound_ThrowsException() {
            when(seriesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> seriesService.getSeriesById(999L));
        }
    }

    @Nested
    @DisplayName("createSeries tests")
    class CreateSeriesTests {

        @Test
        @DisplayName("Author should create series successfully")
        void createSeries_AsAuthor_CreatesSeriesSuccessfully() {
            mockSecurityContext("author@test.com", "AUTHOR");

            SeriesRequest request = new SeriesRequest();
            request.setTitle("New Series");
            request.setDescription("New series description");

            when(userRepository.findByEmail("author@test.com")).thenReturn(Optional.of(testUser));
            when(authorRepository.findByUser(testUser)).thenReturn(Optional.of(testAuthor));
            when(seriesRepository.save(any(Series.class))).thenAnswer(invocation -> {
                Series s = invocation.getArgument(0);
                s.setId(2L);
                return s;
            });

            SeriesResponse result = seriesService.createSeries(request);

            assertNotNull(result);
            assertEquals("New Series", result.getTitle());
            verify(seriesRepository).save(any(Series.class));
        }

        @Test
        @DisplayName("Librarian should create series with authorId")
        void createSeries_AsLibrarianWithAuthorId_CreatesSeriesSuccessfully() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            SeriesRequest request = new SeriesRequest();
            request.setTitle("New Series");
            request.setDescription("New series description");
            request.setAuthorId(1L);

            when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(seriesRepository.save(any(Series.class))).thenAnswer(invocation -> {
                Series s = invocation.getArgument(0);
                s.setId(2L);
                s.setAuthor(testAuthor);
                return s;
            });

            SeriesResponse result = seriesService.createSeries(request);

            assertNotNull(result);
            assertEquals("New Series", result.getTitle());
            verify(authorRepository).findById(1L);
        }
    }

    @Nested
    @DisplayName("deleteSeries tests")
    class DeleteSeriesTests {

        @Test
        @DisplayName("Author should delete own series")
        void deleteSeries_AsOwner_DeletesSuccessfully() {
            mockSecurityContext("author@test.com", "AUTHOR");

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));
            when(userRepository.findByEmail("author@test.com")).thenReturn(Optional.of(testUser));
            when(authorRepository.findByUser(testUser)).thenReturn(Optional.of(testAuthor));

            seriesService.deleteSeries(1L);

            verify(seriesRepository).delete(testSeries);
        }

        @Test
        @DisplayName("Librarian should delete any series")
        void deleteSeries_AsLibrarian_DeletesSuccessfully() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            seriesService.deleteSeries(1L);

            verify(seriesRepository).delete(testSeries);
        }

        @Test
        @DisplayName("Should throw exception when series not found")
        void deleteSeries_SeriesNotFound_ThrowsException() {
            mockSecurityContext("author@test.com", "AUTHOR");

            when(seriesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> seriesService.deleteSeries(999L));
        }

        @Test
        @DisplayName("Should remove books from series before deleting")
        void deleteSeries_WithBooks_RemovesBooksFirst() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            testBook.setSeries(testSeries);
            testSeries.getBooks().add(testBook);

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            seriesService.deleteSeries(1L);

            verify(bookRepository).save(testBook);
            assertNull(testBook.getSeries());
            verify(seriesRepository).delete(testSeries);
        }
    }

    @Nested
    @DisplayName("addBookToSeries tests")
    class AddBookToSeriesTests {

        @Test
        @DisplayName("Should add book to series successfully")
        void addBookToSeries_ValidRequest_AddsBook() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));
            when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(testBook));

            seriesService.addBookToSeries(1L, "9781234567890");

            verify(bookRepository).save(testBook);
            assertEquals(testSeries, testBook.getSeries());
        }

        @Test
        @DisplayName("Should throw exception when series not found")
        void addBookToSeries_SeriesNotFound_ThrowsException() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            when(seriesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> seriesService.addBookToSeries(999L, "9781234567890"));
        }

        @Test
        @DisplayName("Should throw exception when book not found")
        void addBookToSeries_BookNotFound_ThrowsException() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));
            when(bookRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> seriesService.addBookToSeries(1L, "nonexistent"));
        }
    }

    @Nested
    @DisplayName("removeBookFromSeries tests")
    class RemoveBookFromSeriesTests {

        @Test
        @DisplayName("Should remove book from series successfully")
        void removeBookFromSeries_ValidRequest_RemovesBook() {
            mockSecurityContext("librarian@test.com", "LIBRARIAN");

            testBook.setSeries(testSeries);
            testSeries.getBooks().add(testBook);

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));
            when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(testBook));

            seriesService.removeBookFromSeries(1L, "9781234567890");

            verify(bookRepository).save(testBook);
            assertNull(testBook.getSeries());
        }
    }

    @Nested
    @DisplayName("getSeriesBooks tests")
    class GetSeriesBooksTests {

        @Test
        @DisplayName("Should return books in series")
        void getSeriesBooks_BooksExist_ReturnsBooks() {
            testSeries.getBooks().add(testBook);

            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            List<BookSummary> result = seriesService.getSeriesBooks(1L);

            assertEquals(1, result.size());
            assertEquals("Test Book", result.get(0).getTitle());
        }

        @Test
        @DisplayName("Should return empty list when no books in series")
        void getSeriesBooks_NoBooksInSeries_ReturnsEmptyList() {
            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            List<BookSummary> result = seriesService.getSeriesBooks(1L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("isAuthorOfSeries tests")
    class IsAuthorOfSeriesTests {

        @Test
        @DisplayName("Should return true when user is author of series")
        void isAuthorOfSeries_UserIsAuthor_ReturnsTrue() {
            when(userRepository.findByEmail("author@test.com")).thenReturn(Optional.of(testUser));
            when(authorRepository.findByUser(testUser)).thenReturn(Optional.of(testAuthor));
            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            boolean result = seriesService.isAuthorOfSeries("author@test.com", 1L);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when user is not author of series")
        void isAuthorOfSeries_UserIsNotAuthor_ReturnsFalse() {
            Author anotherAuthor = new Author();
            anotherAuthor.setId(2L);
            testSeries.setAuthor(anotherAuthor);

            when(userRepository.findByEmail("author@test.com")).thenReturn(Optional.of(testUser));
            when(authorRepository.findByUser(testUser)).thenReturn(Optional.of(testAuthor));
            when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

            boolean result = seriesService.isAuthorOfSeries("author@test.com", 1L);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when user not found")
        void isAuthorOfSeries_UserNotFound_ReturnsFalse() {
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            boolean result = seriesService.isAuthorOfSeries("unknown@test.com", 1L);

            assertFalse(result);
        }
    }
}
