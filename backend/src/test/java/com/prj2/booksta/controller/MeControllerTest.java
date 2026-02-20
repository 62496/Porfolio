package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private MeController meController;

    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private User user;

    private final String EMAIL = "user@test.com";
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(meController).build();

        user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);

        mockAuthentication = mock(Authentication.class);
        lenient().when(mockAuthentication.getName()).thenReturn(EMAIL);
    }

    @Test
    void testGetOwnedBooks() throws Exception {
        BookWithLatestReadingEvent dto = new BookWithLatestReadingEvent(
                "123", "Title", 2020, 300L, "img.jpg", null
        );
        List<BookWithLatestReadingEvent> dtoList = Collections.singletonList(dto);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getOwnedBooksWithLatestReadingEvent(USER_ID)).thenReturn(dtoList);

        mockMvc.perform(get("/api/me/books")
                .principal(mockAuthentication)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn").value("123"))
                .andExpect(jsonPath("$[0].title").value("Title"));

        verify(userService).getUserByEmail(EMAIL);
        verify(userService).getOwnedBooksWithLatestReadingEvent(USER_ID);
    }

    @Test
    void testGetOwnedBooksWithProgress() throws Exception {
        BookWithLatestReadingEvent dto = new BookWithLatestReadingEvent(
                "456", "Other Book", 2021, 150L, "cover.png", null
        );
        List<BookWithLatestReadingEvent> dtoList = Collections.singletonList(dto);

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);
        when(userService.getOwnedBooksWithReadingEvent(USER_ID)).thenReturn(dtoList);

        mockMvc.perform(get("/api/me/reading-progress/books")
                .principal(mockAuthentication)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn").value("456"));

        verify(userService).getUserByEmail(EMAIL);
        verify(userService).getOwnedBooksWithReadingEvent(USER_ID);
    }
}