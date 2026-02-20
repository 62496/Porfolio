package com.prj2.booksta.config;

import com.prj2.booksta.service.CustomUserDetailsService;
import com.prj2.booksta.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationProvider authProvider;

    @Mock
    private UserDetails userDetails;

    @Test
    void testAuthenticateValidToken() {
        String token = "valid.token.here";
        String email = "test@example.com";

        Authentication incomingAuth = new JwtAuthenticationToken(token);

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(null);

        Authentication result = authProvider.authenticate(incomingAuth);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(userDetails, result.getPrincipal());
    }

    @Test
    void testAuthenticateInvalidToken() {
        String token = "invalid.token";
        String email = "hacker@example.com";
        Authentication incomingAuth = new JwtAuthenticationToken(token);

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authProvider.authenticate(incomingAuth);
        });
    }

    @Test
    void testSupports() {
        assertTrue(authProvider.supports(JwtAuthenticationToken.class));
        assertFalse(authProvider.supports(UsernamePasswordAuthenticationToken.class));
    }
}