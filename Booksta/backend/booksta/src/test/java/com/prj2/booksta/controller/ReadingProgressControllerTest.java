package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.ReadingProgress;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.UserRepository;
import com.prj2.booksta.service.ReadingProgressService;
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
import org.springframework.test.util.ReflectionTestUtils; // IMPORTANT
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReadingProgressControllerTest {

    @Mock
    private ReadingProgressService progressService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ReadingProgressController progressController;

    private MockMvc mockMvc;
    private User user;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        ReflectionTestUtils.setField(progressController, "userService", userService);

        mockUserDetails = mock(UserDetails.class);
        lenient().when(mockUserDetails.getUsername()).thenReturn("test@test.com");

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

        mockMvc = MockMvcBuilders.standaloneSetup(progressController)
                .setCustomArgumentResolvers(putPrincipal)
                .build();
    }

    @Test
    void testCreateProgress() throws Exception {
        String isbn = "12345";
        Long currentPage = 50L;

        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(200L);
        progress.setCurrentPage(currentPage);

        when(userService.getUserByEmail("test@test.com")).thenReturn(user);
        when(progressService.createProgress(user, isbn, currentPage)).thenReturn(progress);

        mockMvc.perform(post("/api/progress/create/{bookIsbn}", isbn)
                .param("currentPage", currentPage.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(50));

        verify(userService).getUserByEmail("test@test.com");
        verify(progressService).createProgress(user, isbn, currentPage);
    }

    @Test
    void testUpdateProgress() throws Exception {
        String isbn = "12345";
        Long currentPage = 100L;

        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(200L);
        progress.setCurrentPage(currentPage);

        when(userService.getUserByEmail("test@test.com")).thenReturn(user);
        when(progressService.updateProgress(user, isbn, currentPage)).thenReturn(progress);

        mockMvc.perform(post("/api/progress/update/{bookIsbn}", isbn)
                .param("currentPage", currentPage.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(100));

        verify(progressService).updateProgress(user, isbn, currentPage);
    }

    @Test
    void testGetUserProgress() throws Exception {
        ReadingProgress progress = new ReadingProgress();
        progress.setId(10L);

        progress.initializeTotalPages(100L);
        progress.setCurrentPage(10L);

        when(userService.getUserByEmail("test@test.com")).thenReturn(user);
        when(progressService.getUserProgress(user)).thenReturn(Collections.singletonList(progress));

        mockMvc.perform(get("/api/progress")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].progressPercent").value(10));

        verify(progressService).getUserProgress(user);
    }
}