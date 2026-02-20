package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import com.prj2.booksta.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private SeriesRepository seriesRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private UserService userService;

    private final Long USER_ID = 1L;
    private final String BOOK_ISBN = "100";
    private final Long AUTHOR_ID = 50L;
    private final Long SERIES_ID = 90L;

    private User user;
    private Book book;
    private Author author;
    private Series series;
    private Role userRole;
    private Role adminRole;
    private Role authorRole;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("test@email.com");
        user.setPicture("http://pic.jpg");
        user.setFavoriteList(new HashSet<>());
        user.setFollowedAuthors(new HashSet<>());
        user.setFollowedSeries(new HashSet<>());
        user.setOwnedBooks(new HashSet<>());
        user.setRoles(new HashSet<>());

        book = new Book();
        book.setIsbn(BOOK_ISBN);
        book.setTitle("BookTest");

        author = new Author();
        author.setId(AUTHOR_ID);
        author.setFirstName("Victor");

        series = new Series();
        series.setId(SERIES_ID);
        series.setTitle("Harry Potter");

        userRole = new Role();
        userRole.setName("USER");
        adminRole = new Role();
        adminRole.setName("ADMIN");
        authorRole = new Role();
        authorRole.setName("AUTHOR");
    }

    @Test
    void testGetUserByIdFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(USER_ID);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertFalse(userService.getUserById(USER_ID).isPresent());
    }

    @Test
    void testSaveUser() {
        userService.save(user);
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserByEmail_Found() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        User result = userService.getUserByEmail("test@email.com");
        assertEquals(user, result);
    }

    @Test
    void testGetUserByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail("unknown@email.com"));
    }

    @Test
    void testGetUserByEmailOrThrow_NotFound_ThrowsRuntimeException() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.getUserByEmailOrThrow("unknown@email.com"));
        assertTrue(ex.getMessage().contains("Utilisateur non trouvé"));
    }

    // --- Tests FAVORIS ---

    @Test
    void testAddFavorite_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        userService.addFavorite(USER_ID, BOOK_ISBN);
        assertTrue(user.getFavoriteList().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void testAddFavorite_AlreadyExists() {
        user.getFavoriteList().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        assertThrows(IllegalArgumentException.class, () -> userService.addFavorite(USER_ID, BOOK_ISBN));
        verify(userRepository, never()).save(user);
    }

    @Test
    void testAddFavorite_BookNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.addFavorite(USER_ID, BOOK_ISBN));
    }

    @Test
    void testRemoveFavorite_Success() {
        user.getFavoriteList().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        userService.removeFavorite(USER_ID, BOOK_ISBN);
        assertFalse(user.getFavoriteList().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveFavorite_NotPresent() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        assertThrows(IllegalArgumentException.class, () -> userService.removeFavorite(USER_ID, BOOK_ISBN));
    }

    @Test
    void testGetFavorites() {
        user.getFavoriteList().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Set<Book> result = userService.getFavorites(USER_ID);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFavoritesOptimized() {
        user.getFavoriteList().add(book);
        when(userRepository.findByIdWithFavorites(USER_ID)).thenReturn(Optional.of(user));
        Set<Book> result = userService.getFavoritesOptimized(USER_ID);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFavoritesOptimized_NotFound() {
        when(userRepository.findByIdWithFavorites(USER_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getFavoritesOptimized(USER_ID));
    }

    // --- Tests AUTEURS ---

    @Test
    void testFollowAuthor_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        userService.followAuthor(USER_ID, AUTHOR_ID);
        assertTrue(user.getFollowedAuthors().contains(author));
    }

    @Test
    void testFollowAuthor_AuthorNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.followAuthor(USER_ID, AUTHOR_ID));
    }

    @Test
    void testUnfollowAuthor_Success() {
        user.getFollowedAuthors().add(author);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        userService.unfollowAuthor(USER_ID, AUTHOR_ID);
        assertFalse(user.getFollowedAuthors().contains(author));
        verify(userRepository).save(user);
    }

    @Test
    void testIsFollowingAuthor() {
        user.getFollowedAuthors().add(author);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        assertTrue(userService.isFollowingAuthor(USER_ID, AUTHOR_ID));
        assertFalse(userService.isFollowingAuthor(USER_ID, 999L));
    }

    @Test
    void testGetFollowedAuthors() {
        user.getFollowedAuthors().add(author);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Set<Author> result = userService.getFollowedAuthors(USER_ID);
        assertEquals(1, result.size());
    }

    // --- Tests SERIES ---

    @Test
    void testFollowSeries_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(seriesRepository.findById(SERIES_ID)).thenReturn(Optional.of(series));
        userService.followSeries(USER_ID, SERIES_ID);
        assertTrue(user.getFollowedSeries().contains(series));
    }

    @Test
    void testUnfollowSeries_Success() {
        user.getFollowedSeries().add(series);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(seriesRepository.findById(SERIES_ID)).thenReturn(Optional.of(series));
        userService.unfollowSeries(USER_ID, SERIES_ID);
        assertFalse(user.getFollowedSeries().contains(series));
        verify(userRepository).save(user);
    }

    @Test
    void testUnfollowSeries_NotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(seriesRepository.findById(SERIES_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.unfollowSeries(USER_ID, SERIES_ID));
    }

    @Test
    void testIsFollowingSeries() {
        user.getFollowedSeries().add(series);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        assertTrue(userService.isFollowingSeries(USER_ID, SERIES_ID));
    }

    @Test
    void testGetFollowedSeries() {
        user.getFollowedSeries().add(series);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Set<Series> result = userService.getFollowedSeries(USER_ID);
        assertEquals(1, result.size());
    }

    // --- Tests SEARCH ---

    @Test
    void testSearchUsers_EmptyQuery() {
        List<User> result = userService.searchUsers("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchUsers_ValidQuery() {
        when(userRepository.searchByNameOrEmail("john")).thenReturn(List.of(user));
        List<User> result = userService.searchUsers(" john ");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchGoogleUsers() {
        when(userRepository.searchGoogleUsers("john", 1L)).thenReturn(List.of(user));
        List<User> result = userService.searchGoogleUsers("john", 1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllGoogleUsers() {
        when(userRepository.searchGoogleUsers(null, 1L)).thenReturn(List.of(user));
        List<User> result = userService.getAllGoogleUsers(1L);
        assertEquals(1, result.size());
    }

    // --- Tests GOOGLE USER & ROLES ---

    @Test
    void testFindOrCreateGoogleUser_ExistingGoogleId() {
        String googleId = "google-123";
        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(user));
        User result = userService.findOrCreateGoogleUser("email", "Fn", "Ln", googleId, "pic");
        assertEquals(user, result);
    }

    @Test
    void testFindOrCreateGoogleUser_ExistingEmail_LinkAccount() {
        String googleId = "google-123";
        String email = "test@email.com";
        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        User result = userService.findOrCreateGoogleUser(email, "Fn", "Ln", googleId, "pic");
        assertEquals(user, result);
    }

    @Test
    void testFindOrCreateGoogleUser_NewUser_FirstAdmin() {
        String googleId = "new-google-id";
        String email = "new@email.com";

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Mock Roles
        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(roleRepository.findByName("ADMIN")).thenReturn(adminRole);
        // Mock No Admin exists
        when(userRepository.existsUserWithRole("ADMIN")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(999L);
            return u;
        });

        User result = userService.findOrCreateGoogleUser(email, "New", "User", googleId, "pic.jpg");

        assertNotNull(result);
        assertTrue(result.getRoles().contains(userRole));
        assertTrue(result.getRoles().contains(adminRole));
    }

    @Test
    void testFindOrCreateGoogleUser_NewUser_NotFirstAdmin() {
        String googleId = "new-google-id";
        String email = "new@email.com";

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        when(roleRepository.findByName("USER")).thenReturn(userRole);
        // Mock Admin already exists
        when(userRepository.existsUserWithRole("ADMIN")).thenReturn(true);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.findOrCreateGoogleUser(email, "New", "User", googleId, "pic.jpg");

        assertTrue(result.getRoles().contains(userRole));
        assertFalse(result.getRoles().contains(adminRole));
    }

    // --- Tests ADMIN METHODS ---

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testAddRoleToUser_Success_SimpleRole() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(adminRole);

        userService.addRoleToUser(USER_ID, "ADMIN");

        assertTrue(user.getRoles().contains(adminRole));
        verify(userRepository).save(user);
    }

    @Test
    void testAddRoleToUser_Success_AuthorRole_CreatesAuthor() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("AUTHOR")).thenReturn(authorRole);
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());

        userService.addRoleToUser(USER_ID, "AUTHOR");

        assertTrue(user.getRoles().contains(authorRole));
        verify(authorRepository).save(any(Author.class)); // Verifie qu'un auteur est créé
        verify(userRepository).save(user);
    }

    @Test
    void testAddRoleToUser_RoleNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("UNKNOWN")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.addRoleToUser(USER_ID, "UNKNOWN"));
    }

    @Test
    void testAddRoleToUser_AlreadyHasRole() {
        user.getRoles().add(adminRole);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(adminRole);

        assertThrows(IllegalArgumentException.class, () -> userService.addRoleToUser(USER_ID, "ADMIN"));
    }

    @Test
    void testRemoveRoleFromUser_Success() {
        user.getRoles().add(adminRole);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.removeRoleFromUser(USER_ID, "ADMIN");

        assertFalse(user.getRoles().contains(adminRole));
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveRoleFromUser_NotAssigned() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.removeRoleFromUser(USER_ID, "ADMIN"));
    }

    // --- Tests OWNED BOOKS ---

    @Test
    void testGetOwnedBooks() {
        user.getOwnedBooks().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Set<Book> result = userService.getOwnedBooks(USER_ID);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOwnedBooksOptimized() {
        user.getOwnedBooks().add(book);
        when(userRepository.findByIdWithOwnedBooks(USER_ID)).thenReturn(Optional.of(user));
        Set<Book> result = userService.getOwnedBooksOptimized(USER_ID);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOwnedBooksOptimized_NotFound() {
        when(userRepository.findByIdWithOwnedBooks(USER_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getOwnedBooksOptimized(USER_ID));
    }

    @Test
    void testAddOwnedBook_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        userService.addOwnedBook(USER_ID, BOOK_ISBN);
        assertTrue(user.getOwnedBooks().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void testAddOwnedBook_AlreadyExists() {
        user.getOwnedBooks().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        assertThrows(IllegalArgumentException.class, () -> userService.addOwnedBook(USER_ID, BOOK_ISBN));
    }

    @Test
    void testRemoveOwnedBook_Success() {
        user.getOwnedBooks().add(book);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        userService.removeOwnedBook(USER_ID, BOOK_ISBN);
        assertFalse(user.getOwnedBooks().contains(book));
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveOwnedBook_NotPresent() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        assertThrows(IllegalArgumentException.class, () -> userService.removeOwnedBook(USER_ID, BOOK_ISBN));
    }

    @Test
    void testUserOwnsBook_True() {
        user.getOwnedBooks().add(book);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        assertTrue(userService.userOwnsBook("test@email.com", BOOK_ISBN));
    }

    @Test
    void testUserOwnsBook_False() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        assertFalse(userService.userOwnsBook("test@email.com", BOOK_ISBN));
    }

    @Test
    void testUserOwnsBook_UserNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());
        assertFalse(userService.userOwnsBook("unknown@email.com", BOOK_ISBN));
    }

    @Test
    void testGetOwnedBooksWithLatestReadingEvent() {
        when(userRepository.findOwnedBooksWithLatestReadingEventView(USER_ID))
                .thenReturn(Collections.emptyList());
        List<BookWithLatestReadingEvent> result = userService.getOwnedBooksWithLatestReadingEvent(USER_ID);
        assertNotNull(result);
        verify(userRepository).findOwnedBooksWithLatestReadingEventView(USER_ID);
    }

    @Test
    void testGetOwnedBooksWithReadingEvent() {
        when(userRepository.findOwnedBooksWithReadingEvent(USER_ID))
                .thenReturn(Collections.emptyList());
        List<BookWithLatestReadingEvent> result = userService.getOwnedBooksWithReadingEvent(USER_ID);
        assertNotNull(result);
        verify(userRepository).findOwnedBooksWithReadingEvent(USER_ID);
    }
}