package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.prj2.booksta.model.RefreshToken;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.GoogleLoginRequest;
import com.prj2.booksta.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthorService authorService;

    @Mock
    private RoleService roleService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGoogleLoginSuccessExistingUser() throws Exception {
        String googleToken = "valid-google-token";
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setToken(googleToken);

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("test@gmail.com");
        payload.setSubject("google-id-123");
        payload.set("given_name", "John");
        payload.set("family_name", "Doe");
        payload.set("picture", "http://pic.jpg");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@gmail.com");
        existingUser.setGoogleId("google-id-123");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock-refresh-token");

        when(googleTokenVerifier.verify(googleToken)).thenReturn(payload);

        when(userService.findOrCreateGoogleUser(
                eq("test@gmail.com"),
                anyString(),
                anyString(),
                eq("google-id-123"),
                anyString())).thenReturn(existingUser);

        when(jwtService.generateToken(existingUser)).thenReturn("fake-jwt-token");

        when(refreshTokenService.createRefreshToken(existingUser)).thenReturn(mockRefreshToken);
        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@gmail.com"));

        verify(authorService).findByUserId(existingUser.getId());
    }

    @Test
    void testGoogleLoginSuccessNewUserCreatesAccount() throws Exception {
        String googleToken = "valid-token-new-user";
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setToken(googleToken);

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("new@gmail.com");
        payload.setSubject("new-google-id");
        payload.set("name", "New User");

        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("new@gmail.com");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock-refresh-token-2");

        when(googleTokenVerifier.verify(googleToken)).thenReturn(payload);

        when(userService.findOrCreateGoogleUser(
                eq("new@gmail.com"),
                anyString(),
                anyString(),
                eq("new-google-id"),
                any())).thenReturn(newUser);

        when(jwtService.generateToken(newUser)).thenReturn("new-jwt");

        when(refreshTokenService.createRefreshToken(newUser)).thenReturn(mockRefreshToken);

        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-jwt"));

        verify(authorService).findByUserId(newUser.getId());
    }

    @Test
    void testGoogleLoginInvalidToken() throws Exception {
        String badToken = "bad-token";
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setToken(badToken);

        when(googleTokenVerifier.verify(badToken)).thenReturn(null);

        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid Google token")));
    }

    @Test
    void testGoogleLoginMissingToken() throws Exception {
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setToken("");

        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing Google token")));
    }

    @Test
    void testGetCurrentUserSuccess() throws Exception {
        String token = "valid-jwt";
        String email = "test@email.com";
        User user = new User();
        user.setEmail(email);

        when(jwtService.extractEmail(token)).thenReturn(email);

        when(userService.getUserByEmailOrThrow(email)).thenReturn(user);

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testGetCurrentUserMissingHeader() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Missing or invalid Authorization header")));
    }

    @Test
    void testGetCurrentUserInvalidHeaderFormat() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Basic 12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCurrentUserInvalidToken() throws Exception {
        String token = "corrupted-token";
        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("Token invalide"));

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid or expired token")));
    }
}