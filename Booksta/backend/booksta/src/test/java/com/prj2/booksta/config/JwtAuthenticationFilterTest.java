package com.prj2.booksta.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ShouldContinueChain_WhenNoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void testDoFilterInternal_ShouldAuthenticate_WhenTokenIsValid() throws Exception {
        String token = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        lenient().when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(request.getSession(false)).thenReturn(null);

        Authentication authResult = mock(Authentication.class);

        lenient().when(authenticationManager.authenticate(any(JwtAuthenticationToken.class)))
                .thenReturn(authResult);

        jwtFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "L'authentification ne doit pas être null");

        assertEquals(authResult, SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ShouldReturn401_WhenTokenIsInvalid() throws Exception {
        String token = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        lenient().when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(request.getSession(false)).thenReturn(null);

        when(authenticationManager.authenticate(any(JwtAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Token invalide"));

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }
}