package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.User;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final Long USER_ID = 1L;
    private final String BOOK_ISBN = "9783161484100";

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setId(USER_ID);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setOwnedBooks(new HashSet<>());

        book = new Book();
        book.setIsbn(BOOK_ISBN);
        book.setTitle("Test Book");
    }

    // ==================== SEARCH TESTS ====================

    @Test
    void searchUsers_success_returns200AndList() throws Exception {
        List<User> users = List.of(user);
        when(userService.searchUsers("John")).thenReturn(users);

        mockMvc.perform(get("/api/users/search")
                        .param("query", "John")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(userService, times(1)).searchUsers("John");
    }

    @Test
    void searchGoogleUsers_success_returns200AndList() throws Exception {
        List<User> users = List.of(user);
        when(userService.searchGoogleUsers("john", null)).thenReturn(users);

        mockMvc.perform(get("/api/users/search-google")
                        .param("query", "john")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(userService, times(1)).searchGoogleUsers("john", null);
    }

    @Test
    void searchGoogleUsers_withExcludeId_returns200() throws Exception {
        when(userService.searchGoogleUsers("john", 5L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/search-google")
                        .param("query", "john")
                        .param("excludeId", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).searchGoogleUsers("john", 5L);
    }

    // ==================== OWNED BOOKS TESTS ====================

    @Test
    void addOwnedBook_success_returns200_andMessage() throws Exception {
        doNothing().when(userService).addOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService, times(1)).addOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void addOwnedBook_serviceThrows_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Ce livre est déjà dans votre bibliothèque personnelle"))
                .when(userService).addOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService, times(1)).addOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void removeOwnedBook_success_returns200_andMessage() throws Exception {
        doNothing().when(userService).removeOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService, times(1)).removeOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void removeOwnedBook_serviceThrows_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Ce livre n'est pas dans votre bibliothèque personnelle"))
                .when(userService).removeOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService, times(1)).removeOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void getOwnedBooks_success_returns200_andJsonArray() throws Exception {
        Set<Book> ownedBooks = new HashSet<>();
        ownedBooks.add(book);

        when(userService.getOwnedBooksOptimized(USER_ID)).thenReturn(ownedBooks);

        mockMvc.perform(get("/api/users/{userId}/owned-books", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn").value(BOOK_ISBN))
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(userService, times(1)).getOwnedBooksOptimized(USER_ID);
    }

    @Test
    void getOwnedBooks_userNotFound_returns404() throws Exception {
        when(userService.getOwnedBooksOptimized(USER_ID))
                .thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));

        mockMvc.perform(get("/api/users/{userId}/owned-books", USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getOwnedBooksOptimized(USER_ID);
    }
}
