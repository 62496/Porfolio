package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorService authorService;

    private Author author1;
    private Author author2;
    private User testUser;
    private Book testBook;
    private Series testSeries;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@test.com");
        testUser.setPicture("http://example.com/pic.jpg");
        testUser.setRoles(new HashSet<>());

        author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("John");
        author1.setLastName("Doe");
        author1.setUser(testUser);
        author1.setBooks(new HashSet<>());
        author1.setFollowers(new HashSet<>());

        author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Jane");
        author2.setLastName("Smith");
        author2.setBooks(new HashSet<>());
        author2.setFollowers(new HashSet<>());

        testBook = new Book();
        testBook.setIsbn("9781234567890");
        testBook.setTitle("Test Book");
        testBook.setAuthors(new HashSet<>(Set.of(author1)));

        testSeries = new Series();
        testSeries.setId(1L);
        testSeries.setTitle("Test Series");
        testSeries.setAuthor(author1);
        testSeries.setBooks(new LinkedHashSet<>());
        testSeries.setFollowers(new HashSet<>());
    }

    @Nested
    @DisplayName("getAllAuthors tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return all authors")
        void getAllAuthors_AuthorsExist_ReturnsAllAuthors() {
            List<Author> authors = Arrays.asList(author1, author2);
            when(authorRepository.findAll()).thenReturn(authors);

            Iterable<Author> result = authorService.getAllAuthors();

            assertNotNull(result);
            assertEquals(2, ((List<Author>) result).size());
            verify(authorRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no authors exist")
        void getAllAuthors_NoAuthorsExist_ReturnsEmptyList() {
            when(authorRepository.findAll()).thenReturn(Collections.emptyList());

            Iterable<Author> result = authorService.getAllAuthors();

            assertNotNull(result);
            assertEquals(0, ((List<Author>) result).size());
        }
    }

    @Nested
    @DisplayName("getAuthorById tests")
    class GetAuthorByIdTests {

        @Test
        @DisplayName("Should return author when found")
        void getAuthorById_AuthorExists_ReturnsAuthor() {
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));

            Optional<Author> result = authorService.getAuthorById(1L);

            assertTrue(result.isPresent());
            assertEquals("John", result.get().getFirstName());
            verify(authorRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when author not found")
        void getAuthorById_AuthorNotFound_ReturnsEmpty() {
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Author> result = authorService.getAuthorById(999L);

            assertFalse(result.isPresent());
            verify(authorRepository, times(1)).findById(999L);
        }
    }

    @Nested
    @DisplayName("save tests")
    class SaveTests {

        @Test
        @DisplayName("Should save and return author")
        void save_ValidAuthor_ReturnsSavedAuthor() {
            when(authorRepository.save(author1)).thenReturn(author1);

            Author result = authorService.save(author1);

            assertNotNull(result);
            assertEquals(author1.getId(), result.getId());
            verify(authorRepository).save(author1);
        }
    }

    @Nested
    @DisplayName("addAuthor tests")
    class AddAuthorTests {

        @Test
        @DisplayName("Should create author for user")
        void addAuthor_NewUser_CreatesAuthor() {
            Role authorRole = new Role();
            authorRole.setName("AUTHOR");

            when(authorRepository.findByUser(testUser)).thenReturn(Optional.empty());
            when(roleService.getRole("AUTHOR")).thenReturn(authorRole);
            when(imageService.createImage(any(Image.class))).thenAnswer(i -> i.getArgument(0));

            authorService.addAuthor(testUser);

            verify(authorRepository).save(any(Author.class));
            verify(userService).save(testUser);
        }

        @Test
        @DisplayName("Should not create duplicate author")
        void addAuthor_ExistingAuthor_DoesNotCreateDuplicate() {
            when(authorRepository.findByUser(testUser)).thenReturn(Optional.of(author1));

            authorService.addAuthor(testUser);

            verify(authorRepository, never()).save(any(Author.class));
        }
    }

    @Nested
    @DisplayName("findAllById tests")
    class FindAllByIdTests {

        @Test
        @DisplayName("Should return authors by IDs")
        void findAllById_ValidIds_ReturnsAuthors() {
            when(authorRepository.findAllById(Arrays.asList(1L, 2L)))
                    .thenReturn(Arrays.asList(author1, author2));

            Set<Author> result = authorService.findAllById(Arrays.asList(1L, 2L));

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty set for empty list")
        void findAllById_EmptyList_ReturnsEmptySet() {
            when(authorRepository.findAllById(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            Set<Author> result = authorService.findAllById(Collections.emptyList());

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByUserId tests")
    class FindByUserIdTests {

        @Test
        @DisplayName("Should return author for user ID")
        void findByUserId_UserExists_ReturnsAuthor() {
            when(authorRepository.findByUser_Id(1L)).thenReturn(author1);

            Author result = authorService.findByUserId(1L);

            assertNotNull(result);
            assertEquals(author1.getId(), result.getId());
        }

        @Test
        @DisplayName("Should return null when user has no author profile")
        void findByUserId_UserHasNoAuthor_ReturnsNull() {
            when(authorRepository.findByUser_Id(999L)).thenReturn(null);

            Author result = authorService.findByUserId(999L);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getAuthorDetails tests")
    class GetAuthorDetailsTests {

        @Test
        @DisplayName("Should return author details with books and series")
        void getAuthorDetails_AuthorExists_ReturnsDetails() {
            author1.setImage(new Image("http://example.com/author.jpg"));

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(List.of(testBook));
            when(seriesRepository.findByAuthorId(1L)).thenReturn(List.of(testSeries));

            AuthorDetailResponse result = authorService.getAuthorDetails(1L);

            assertNotNull(result);
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals(1, result.getBookCount());
            assertEquals(1, result.getSeriesCount());
        }

        @Test
        @DisplayName("Should throw exception when author not found")
        void getAuthorDetails_AuthorNotFound_ThrowsException() {
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> authorService.getAuthorDetails(999L));
        }
    }

    @Nested
    @DisplayName("deleteAuthor tests")
    class DeleteAuthorTests {

        @Test
        @DisplayName("Should delete author and cascade to books and series")
        void deleteAuthor_AuthorExists_DeletesWithCascade() throws Exception {
            author1.setImage(new Image("http://example.com/author.jpg"));

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(List.of(testBook));
            when(seriesRepository.findByAuthorId(1L)).thenReturn(List.of(testSeries));

            authorService.deleteAuthor(1L);

            verify(bookService).delete(testBook.getIsbn());
            verify(seriesRepository).deleteByAuthorId(1L);
            verify(authorRepository).delete(author1);
        }

        @Test
        @DisplayName("Should throw exception when author not found")
        void deleteAuthor_AuthorNotFound_ThrowsException() {
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> authorService.deleteAuthor(999L));

            verify(authorRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should remove author from followers")
        void deleteAuthor_WithFollowers_RemovesFromFollowers() throws Exception {
            User follower = new User();
            follower.setId(2L);
            follower.setFollowedAuthors(new HashSet<>(Set.of(author1)));
            author1.getFollowers().add(follower);

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(Collections.emptyList());
            when(seriesRepository.findByAuthorId(1L)).thenReturn(Collections.emptyList());

            authorService.deleteAuthor(1L);

            verify(userRepository).save(follower);
            assertFalse(follower.getFollowedAuthors().contains(author1));
        }

        @Test
        @DisplayName("Should delete author image")
        void deleteAuthor_WithImage_DeletesImage() throws Exception {
            author1.setImage(new Image("http://example.com/author.jpg"));

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(Collections.emptyList());
            when(seriesRepository.findByAuthorId(1L)).thenReturn(Collections.emptyList());

            authorService.deleteAuthor(1L);

            verify(fileStorageService).deleteAuthorImage(1L);
        }

        @Test
        @DisplayName("Should not fail if image deletion fails")
        void deleteAuthor_ImageDeletionFails_ContinuesSuccessfully() throws Exception {
            author1.setImage(new Image("http://example.com/author.jpg"));

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
            when(bookRepository.findByAuthors_Id(1L)).thenReturn(Collections.emptyList());
            when(seriesRepository.findByAuthorId(1L)).thenReturn(Collections.emptyList());
            doThrow(new RuntimeException("Storage error")).when(fileStorageService).deleteAuthorImage(1L);

            assertDoesNotThrow(() -> authorService.deleteAuthor(1L));
            verify(authorRepository).delete(author1);
        }
    }
}
