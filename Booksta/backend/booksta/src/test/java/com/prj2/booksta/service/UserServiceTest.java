package com.prj2.booksta.service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserService userService;

    private final Long USER_ID = 1L;
    private final String BOOK_ISBN = "100";  // Updated: ISBN is String

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("firstlast@email.com");
        user.setFavoriteList(new HashSet<>());

        book = new Book();
        book.setIsbn(BOOK_ISBN);   // Updated
        book.setTitle("BookTest");
    }

    // addFavorite - success
    @Test
    void addFavorite_success_addsBookAndSaves() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.addFavorite(USER_ID, BOOK_ISBN);

        assertTrue(user.getFavoriteList().contains(book), "Book should have been added to favorites");
        verify(userRepository, times(1)).save(user);
    }

    // addFavorite - user not found
    @Test
    void addFavorite_userNotFound_throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.addFavorite(USER_ID, BOOK_ISBN));

        assertTrue(ex.getMessage().toLowerCase().contains("utilisateur non trouvé")
                || ex.getMessage().toLowerCase().contains("user not found"));

        verify(userRepository, never()).save(any());
    }

    // addFavorite - book not found
    @Test
    void addFavorite_bookNotFound_throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.addFavorite(USER_ID, BOOK_ISBN));

        assertTrue(ex.getMessage().toLowerCase().contains("livre non trouvé")
                || ex.getMessage().toLowerCase().contains("book not found"));

        verify(userRepository, never()).save(any());
    }

    // Adding same book twice should throw exception (not idempotent - by design)
    @Test
    void addFavorite_whenCalledTwice_throwsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // First call succeeds
        userService.addFavorite(USER_ID, BOOK_ISBN);
        assertEquals(1, user.getFavoriteList().size());
        verify(userRepository, times(1)).save(user);

        // Second call throws exception because book is already in favorites
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.addFavorite(USER_ID, BOOK_ISBN));

        assertTrue(ex.getMessage().contains("déjà dans vos favoris")
                || ex.getMessage().contains("already in favorites"));
        // Save should still only have been called once (from the first successful add)
        verify(userRepository, times(1)).save(user);
    }

    // removeFavorite - success
    @Test
    void removeFavorite_success_removesBookAndSaves() {
        user.getFavoriteList().add(book);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.removeFavorite(USER_ID, BOOK_ISBN);

        assertFalse(user.getFavoriteList().contains(book), "Book should have been removed from favorites");
        verify(userRepository, times(1)).save(user);
    }

    // removeFavorite - user not found
    @Test
    void removeFavorite_userNotFound_throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.removeFavorite(USER_ID, BOOK_ISBN));

        assertTrue(ex.getMessage().toLowerCase().contains("utilisateur")
                || ex.getMessage().toLowerCase().contains("user"));

        verify(userRepository, never()).save(any());
    }

    // removeFavorite - book not found
    @Test
    void removeFavorite_bookNotFound_throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRepository.findById(BOOK_ISBN)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.removeFavorite(USER_ID, BOOK_ISBN));

        assertTrue(ex.getMessage().toLowerCase().contains("livre")
                || ex.getMessage().toLowerCase().contains("book"));

        verify(userRepository, never()).save(any());
    }

    // getFavorites - success
    @Test
    void getFavorites_success_returnsFavorites() {
        Set<Book> favorites = new HashSet<>();
        favorites.add(book);
        user.setFavoriteList(favorites);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Set<Book> returned = userService.getFavorites(USER_ID);
        assertSame(favorites, returned);
    }

    @Test
    void getFavorites_userNotFound_throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.getFavorites(USER_ID));

        assertTrue(ex.getMessage().toLowerCase().contains("utilisateur")
                || ex.getMessage().toLowerCase().contains("user"));
    }
    // getUserById
    @Test
    void getUserById_returnsOptional() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Optional<User> opt = userService.getUserById(USER_ID);
        assertTrue(opt.isPresent());
        assertEquals(user, opt.get());
    }

    @Test
    void getUserById_emptyOptional() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Optional<User> opt = userService.getUserById(USER_ID);
        assertFalse(opt.isPresent());
    }
}
