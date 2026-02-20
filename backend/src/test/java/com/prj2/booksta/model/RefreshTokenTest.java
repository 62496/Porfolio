package com.prj2.booksta.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void testIsExpired_True() {
        Instant pastDate = Instant.now().minus(1, ChronoUnit.HOURS);
        
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(pastDate);

        assertTrue(token.isExpired(), "Le token devrait être expiré");
    }

    @Test
    void testIsExpired_False() {
        Instant futureDate = Instant.now().plus(1, ChronoUnit.HOURS);
        
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(futureDate);

        assertFalse(token.isExpired(), "Le token ne devrait pas être expiré");
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        Instant now = Instant.now();

        RefreshToken token = new RefreshToken();
        token.setId(10L);
        token.setToken("uuid-token-123");
        token.setUser(user);
        token.setExpiryDate(now);

        assertThat(token.getId()).isEqualTo(10L);
        assertThat(token.getToken()).isEqualTo("uuid-token-123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        Instant now = Instant.now();
        
        RefreshToken token = new RefreshToken(10L, "token-abc", user, now);

        assertThat(token.getId()).isEqualTo(10L);
        assertThat(token.getToken()).isEqualTo("token-abc");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(1L);
        Instant now = Instant.now();

        RefreshToken token1 = new RefreshToken(1L, "token", user, now);
        RefreshToken token2 = new RefreshToken(1L, "token", user, now);
        RefreshToken token3 = new RefreshToken(2L, "other", user, now);

        assertThat(token1).isEqualTo(token2);
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());

        assertThat(token1).isNotEqualTo(token3);
    }

    @Test
    void testToString() {
        RefreshToken token = new RefreshToken();
        token.setToken("secret-token");
        
        String result = token.toString();
        
        assertThat(result).isNotNull();
        assertThat(result).contains("RefreshToken");
        assertThat(result).contains("secret-token");
    }
}