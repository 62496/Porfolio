package com.prj2.booksta.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.AuthorRepository; 
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthorAccessCheckerTest {

    @Mock
    private UserService userService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorAccessChecker accessChecker;

    private Authentication authentication;
    private UserDetails userDetails;
    private User authenticatedUser;
    private Book book;
    
    private final String EMAIL = "author@test.com";
    private final String ISBN = "12345";
    private final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);
        
        authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);

        book = new Book();
        book.setIsbn(ISBN);
        book.setAuthors(new HashSet<>());
    }

    @Test
    void testIsAuthorOfBook_NullAuthentication() {
        assertFalse(accessChecker.isAuthorOfBook(null, ISBN));
    }

    @Test
    void testIsAuthorOfBook_NullIsbn() {
        assertFalse(accessChecker.isAuthorOfBook(authentication, null));
    }

    @Test
    void testIsAuthorOfBook_PrincipalNotUserDetails() {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        assertFalse(accessChecker.isAuthorOfBook(authentication, ISBN));
    }

    @Test
    void testIsAuthorOfBook_BookNotFound() {
        setupSecurityContext();
        
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

        assertFalse(accessChecker.isAuthorOfBook(authentication, ISBN));
    }

    @Test
    void testIsAuthorOfBook_Success() {
        setupSecurityContext();

        Author author = new Author();
        author.setUser(authenticatedUser);
        book.getAuthors().add(author);

        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));

        assertTrue(accessChecker.isAuthorOfBook(authentication, ISBN));
    }

    @Test
    void testIsAuthorOfBook_NotTheAuthor() {
        setupSecurityContext();

        User otherUser = new User();
        otherUser.setId(999L);
        
        Author author = new Author();
        author.setUser(otherUser);
        book.getAuthors().add(author);

        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));

        assertFalse(accessChecker.isAuthorOfBook(authentication, ISBN));
    }

    @Test
    void testIsAuthorOfBook_AuthorHasNoUserAccount() {
        setupSecurityContext();

        Author author = new Author();
        author.setUser(null);
        book.getAuthors().add(author);

        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));

        assertFalse(accessChecker.isAuthorOfBook(authentication, ISBN));
    }
    
    @Test
    void testIsAuthorOfBook_BookHasNoAuthors() {
        setupSecurityContext();
        
        book.setAuthors(Collections.emptySet()); 
        
        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));

        assertFalse(accessChecker.isAuthorOfBook(authentication, ISBN));
    }
    
    private void setupSecurityContext() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userService.getUserByEmail(EMAIL)).thenReturn(authenticatedUser);
    }
}