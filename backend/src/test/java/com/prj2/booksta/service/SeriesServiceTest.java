package com.prj2.booksta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.repository.AuthorRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.SeriesRepository;
import com.prj2.booksta.repository.UserRepository;

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

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    private User user;
    private Author author;
    private Series series;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        author = new Author();
        author.setId(10L);
        author.setUser(user);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setImage(new Image("http://img.com"));

        series = new Series();
        series.setId(100L);
        series.setTitle("Harry Potter");
        series.setDescription("Description");
        series.setAuthor(author);
        series.setBooks(new LinkedHashSet<>());
        series.setFollowers(new HashSet<>());

        book = new Book();
        book.setIsbn("12345");
        book.setTitle("Sorcerer's Stone");
        book.setPublishingYear(1997);
        book.setImage(new Image("http://book.img"));
        book.setSeries(series);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn(user.getEmail());
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testGetAllSeries() {
        when(seriesRepository.findAll()).thenReturn(Arrays.asList(series));
        List<SeriesResponse> responses = seriesService.getAllSeries();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Harry Potter");
        assertThat(responses.get(0).getAuthor().getImageUrl()).isEqualTo("http://img.com");
    }

    @Test
    void testGetSeriesByAuthorId() {
        when(seriesRepository.findByAuthorId(10L)).thenReturn(Arrays.asList(series));
        List<SeriesResponse> responses = seriesService.getSeriesByAuthorId(10L);
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(100L);
    }

    @Test
    void testGetSeriesById_Success() {
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        SeriesResponse response = seriesService.getSeriesById(100L);
        assertThat(response.getTitle()).isEqualTo("Harry Potter");
    }

    @Test
    void testGetSeriesById_NotFound() {
        when(seriesRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> seriesService.getSeriesById(999L));
    }

    @Test
    void testGetSeriesById_NullListsAndAuthor() {
        Series emptySeries = new Series();
        emptySeries.setId(200L);
        emptySeries.setTitle("Empty");
        
        when(seriesRepository.findById(200L)).thenReturn(Optional.of(emptySeries));

        SeriesResponse response = seriesService.getSeriesById(200L);
        
        assertThat(response.getBookCount()).isZero();
        assertThat(response.getFollowerCount()).isZero();
        assertThat(response.getAuthor()).isNull();
    }

    @Test
    void testCreateSeries_Success() {
        mockSecurityContext();
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(seriesRepository.save(any(Series.class))).thenReturn(series);

        SeriesRequest request = new SeriesRequest();
        request.setTitle("Harry Potter");
        request.setDescription("Magic world");

        SeriesResponse response = seriesService.createSeries(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Harry Potter");
        verify(seriesRepository).save(any(Series.class));
    }

    @Test
    void testCreateSeries_UserNotAuthor() {
        mockSecurityContext();
        when(authorRepository.findByUser(user)).thenReturn(Optional.empty());

        SeriesRequest request = new SeriesRequest();
        request.setTitle("Fail");

        assertThrows(AccessDeniedException.class, () -> seriesService.createSeries(request));
    }

    @Test
    void testUpdateSeries_Success_AllFields() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(seriesRepository.save(any(Series.class))).thenReturn(series);

        SeriesRequest request = new SeriesRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Desc");

        SeriesResponse response = seriesService.updateSeries(100L, request);

        assertThat(response).isNotNull();
        verify(seriesRepository).save(series);
        assertThat(series.getTitle()).isEqualTo("Updated Title");
        assertThat(series.getDescription()).isEqualTo("Updated Desc");
    }

    @Test
    void testUpdateSeries_Success_PartialUpdate() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(seriesRepository.save(any(Series.class))).thenReturn(series);

        SeriesRequest request = new SeriesRequest();
        request.setDescription("Only Desc Updated");

        seriesService.updateSeries(100L, request);

        assertThat(series.getTitle()).isEqualTo("Harry Potter"); 
        assertThat(series.getDescription()).isEqualTo("Only Desc Updated");
    }

    @Test
    void testUpdateSeries_NotFound() {
        when(seriesRepository.findById(999L)).thenReturn(Optional.empty());
        SeriesRequest request = new SeriesRequest();
        assertThrows(EntityNotFoundException.class, () -> seriesService.updateSeries(999L, request));
    }

    @Test
    void testUpdateSeries_NotOwner() {
        mockSecurityContext();
        Author otherAuthor = new Author();
        otherAuthor.setId(99L);
        series.setAuthor(otherAuthor);

        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));

        SeriesRequest request = new SeriesRequest();
        assertThrows(AccessDeniedException.class, () -> seriesService.updateSeries(100L, request));
    }

    @Test
    void testUpdateSeries_SeriesAuthorNull() {
        mockSecurityContext();
        series.setAuthor(null);

        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));

        SeriesRequest request = new SeriesRequest();
        assertThrows(AccessDeniedException.class, () -> seriesService.updateSeries(100L, request));
    }

    @Test
    void testDeleteSeries_Success() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));

        series.getBooks().add(book);
        seriesService.deleteSeries(100L);

        assertThat(book.getSeries()).isNull();
        verify(bookRepository).save(book);
        verify(seriesRepository).delete(series);
    }

    @Test
    void testDeleteSeries_NotFound() {
        when(seriesRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> seriesService.deleteSeries(999L));
    }

    @Test
    void testAddBookToSeries_Success() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(bookRepository.findById("12345")).thenReturn(Optional.of(book));
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));

        seriesService.addBookToSeries(100L, "12345");

        verify(bookRepository).save(book);
        assertThat(book.getSeries()).isEqualTo(series);
    }

    @Test
    void testAddBookToSeries_BookNotFound() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(bookRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> seriesService.addBookToSeries(100L, "999"));
    }

    @Test
    void testRemoveBookFromSeries_Success() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(bookRepository.findById("12345")).thenReturn(Optional.of(book));
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));

        book.setSeries(series);
        seriesService.removeBookFromSeries(100L, "12345");

        verify(bookRepository).save(book);
        assertThat(book.getSeries()).isNull();
    }

    @Test
    void testRemoveBookFromSeries_BookNotLinked() {
        mockSecurityContext();
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(bookRepository.findById("12345")).thenReturn(Optional.of(book));
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));

        Series otherSeries = new Series();
        otherSeries.setId(200L);
        book.setSeries(otherSeries);

        seriesService.removeBookFromSeries(100L, "12345");

        verify(bookRepository, never()).save(book);
        assertThat(book.getSeries()).isEqualTo(otherSeries);
    }

    @Test
    void testGetSeriesBooks() {
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        series.getBooks().add(book);

        List<BookSummary> books = seriesService.getSeriesBooks(100L);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Sorcerer's Stone");
        assertThat(books.get(0).getImageUrl()).isEqualTo("http://book.img");
    }

    @Test
    void testIsAuthorOfSeries_True() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));

        boolean result = seriesService.isAuthorOfSeries("test@test.com", 100L);
        assertThat(result).isTrue();
    }

    @Test
    void testIsAuthorOfSeries_UserNotFound() {
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());
        boolean result = seriesService.isAuthorOfSeries("unknown", 100L);
        assertThat(result).isFalse();
    }

    @Test
    void testIsAuthorOfSeries_AuthorNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authorRepository.findByUser(user)).thenReturn(Optional.empty());
        boolean result = seriesService.isAuthorOfSeries("test@test.com", 100L);
        assertThat(result).isFalse();
    }

    @Test
    void testIsAuthorOfSeries_SeriesNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        when(seriesRepository.findById(999L)).thenReturn(Optional.empty());
        boolean result = seriesService.isAuthorOfSeries("test@test.com", 999L);
        assertThat(result).isFalse();
    }

    @Test
    void testIsAuthorOfSeries_SeriesNoAuthor() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author));
        series.setAuthor(null);
        when(seriesRepository.findById(100L)).thenReturn(Optional.of(series));
        
        boolean result = seriesService.isAuthorOfSeries("test@test.com", 100L);
        assertThat(result).isFalse();
    }

    @Test
    void testGetAuthenticatedUser_NotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("unknown@email.com");
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        SeriesRequest req = new SeriesRequest();
        assertThrows(UsernameNotFoundException.class, () -> seriesService.createSeries(req));
    }
}