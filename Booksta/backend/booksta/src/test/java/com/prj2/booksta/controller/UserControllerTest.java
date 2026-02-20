package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final Long USER_ID = 1L;
    private final String EMAIL = "john.doe@example.com";
    private final String BOOK_ISBN = "9783161484100";
    private final Long AUTHOR_ID = 50L;
    private final Long SERIES_ID = 90L;

    private User user;
    private Book book;
    private Author author;
    private Series series;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(EMAIL);

        book = new Book();
        book.setIsbn(BOOK_ISBN);
        book.setTitle("Test Book");

        author = new Author();
        author.setId(AUTHOR_ID);
        author.setFirstName("Victor");

        series = new Series();
        series.setId(SERIES_ID);
        series.setTitle("Harry Potter");

        mockUserDetails = mock(UserDetails.class);
        lenient().when(mockUserDetails.getUsername()).thenReturn(EMAIL);

        HandlerMethodArgumentResolver putPrincipal = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(UserDetails.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockUserDetails;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(putPrincipal)
                .build();
    }

    @Test
    void testAddFavorite_Success() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).addFavorite(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/favorites/{bookIsbn}", BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("ajouté")))
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).addFavorite(USER_ID, BOOK_ISBN);
    }

    @Test
    void testAddFavorite_BadRequest() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doThrow(new IllegalArgumentException("Livre déjà ajouté"))
                .when(userService).addFavorite(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/favorites/{bookIsbn}", BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Livre déjà ajouté")))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRemoveFavorite_Success() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).removeFavorite(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/favorites/{bookIsbn}", BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("retiré")))
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).removeFavorite(USER_ID, BOOK_ISBN);
    }

    @Test
    void testRemoveFavorite_BadRequest() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doThrow(new IllegalArgumentException("Livre non trouvé dans les favoris"))
                .when(userService).removeFavorite(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/favorites/{bookIsbn}", BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Livre non trouvé")))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetFavorites_Success() throws Exception {
        Set<Book> favorites = Collections.singleton(book);
        
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFavoritesOptimized(USER_ID)).thenReturn(favorites);

        mockMvc.perform(get("/api/users/favorites")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn").value(BOOK_ISBN));
    }

    @Test
    void testGetFavorites_NotFound() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFavoritesOptimized(USER_ID))
                .thenThrow(new IllegalArgumentException("Utilisateur introuvable"));

        mockMvc.perform(get("/api/users/favorites"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFollowAuthor() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).followAuthor(USER_ID, AUTHOR_ID);

        mockMvc.perform(post("/api/users/follow/author/{authorId}", AUTHOR_ID))
                .andExpect(status().isOk());

        verify(userService).followAuthor(USER_ID, AUTHOR_ID);
    }

    @Test
    void testUnfollowAuthor() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).unfollowAuthor(USER_ID, AUTHOR_ID);

        mockMvc.perform(delete("/api/users/follow/author/{authorId}", AUTHOR_ID))
                .andExpect(status().isNoContent());

        verify(userService).unfollowAuthor(USER_ID, AUTHOR_ID);
    }

    @Test
    void testIsFollowingAuthor() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.isFollowingAuthor(USER_ID, AUTHOR_ID)).thenReturn(true);

        mockMvc.perform(get("/api/users/follow/author/{authorId}", AUTHOR_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testGetFollowedAuthors_Success() throws Exception {
        Set<Author> authors = Collections.singleton(author);
        
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFollowedAuthors(USER_ID)).thenReturn(authors);

        mockMvc.perform(get("/api/users/followed-authors")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Victor"));
    }

    @Test
    void testGetFollowedAuthors_NotFound() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFollowedAuthors(USER_ID))
                .thenThrow(new RuntimeException("Erreur inattendue"));

        mockMvc.perform(get("/api/users/followed-authors"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFollowSeries() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).followSeries(USER_ID, SERIES_ID);

        mockMvc.perform(post("/api/users/follow/series/{seriesId}", SERIES_ID))
                .andExpect(status().isOk());

        verify(userService).followSeries(USER_ID, SERIES_ID);
    }

    @Test
    void testUnfollowSeries() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        doNothing().when(userService).unfollowSeries(USER_ID, SERIES_ID);

        mockMvc.perform(delete("/api/users/follow/series/{seriesId}", SERIES_ID))
                .andExpect(status().isNoContent());

        verify(userService).unfollowSeries(USER_ID, SERIES_ID);
    }

    @Test
    void testIsFollowingSeries() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.isFollowingSeries(USER_ID, SERIES_ID)).thenReturn(false);

        mockMvc.perform(get("/api/users/follow/series/{seriesId}", SERIES_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testGetFollowedSeries_Success() throws Exception {
        Set<Series> seriesList = Collections.singleton(series);
        
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFollowedSeries(USER_ID)).thenReturn(seriesList);

        mockMvc.perform(get("/api/users/followed-series")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));
    }

    @Test
    void testGetFollowedSeries_NotFound() throws Exception {
        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getFollowedSeries(USER_ID))
                .thenThrow(new RuntimeException("Erreur"));

        mockMvc.perform(get("/api/users/followed-series"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchUsers() throws Exception {
        when(userService.searchUsers("john")).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/users/search")
                .param("query", "john")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    void testSearchGoogleUsers() throws Exception {
        when(userService.searchGoogleUsers("john", 99L)).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/users/search-google")
                .param("query", "john")
                .param("excludeId", "99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    void testAddOwnedBook_Success() throws Exception {
        doNothing().when(userService).addOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("ajouté")))
                .andExpect(jsonPath("$.success").value(true));
                
        verify(userService).addOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void testAddOwnedBook_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Livre déjà possédé"))
                .when(userService).addOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(post("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Livre déjà possédé")))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRemoveOwnedBook_Success() throws Exception {
        doNothing().when(userService).removeOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("retiré")))
                .andExpect(jsonPath("$.success").value(true));
                
        verify(userService).removeOwnedBook(USER_ID, BOOK_ISBN);
    }

    @Test
    void testRemoveOwnedBook_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Livre non possédé"))
                .when(userService).removeOwnedBook(USER_ID, BOOK_ISBN);

        mockMvc.perform(delete("/api/users/{userId}/owned-books/{bookIsbn}", USER_ID, BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Livre non possédé")))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetOwnedBooks_Success() throws Exception {
        Set<Book> owned = Collections.singleton(book);
        when(userService.getOwnedBooksOptimized(USER_ID)).thenReturn(owned);

        mockMvc.perform(get("/api/users/{userId}/owned-books", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetOwnedBooks_NotFound() throws Exception {
        when(userService.getOwnedBooksOptimized(USER_ID))
                .thenThrow(new IllegalArgumentException("Utilisateur introuvable"));

        mockMvc.perform(get("/api/users/{userId}/owned-books", USER_ID))
                .andExpect(status().isNotFound());
    }
}