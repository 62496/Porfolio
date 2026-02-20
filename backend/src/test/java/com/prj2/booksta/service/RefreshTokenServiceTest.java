package com.prj2.booksta.service;

import com.prj2.booksta.model.RefreshToken;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private final long DURATION_MS = 60000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", DURATION_MS);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
    }

    @Test
    void testCreateRefreshToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken createdToken = refreshTokenService.createRefreshToken(user);

        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getUser()).isEqualTo(user);
        assertThat(createdToken.getToken()).isNotNull();
        assertThat(createdToken.getExpiryDate()).isAfter(Instant.now());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testFindByToken_Found() {
        String tokenString = "uuid-token";
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);

        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(tokenString);
    }

    @Test
    void testFindByToken_NotFound() {
        String tokenString = "missing-token";
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        assertThat(result).isEmpty();
    }

    @Test
    void testVerifyExpiration_ValidToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        token.setToken("valid-token");

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertThat(result).isEqualTo(token);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpiration_ExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minus(1, ChronoUnit.HOURS));
        token.setToken("expired-token");

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void testDeleteByUser() {
        refreshTokenService.deleteByUser(user);
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void testDeleteByToken_Exists() {
        String tokenString = "existing-token";
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);

        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        refreshTokenService.deleteByToken(tokenString);

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void testDeleteByToken_NotExists() {
        String tokenString = "missing-token";
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        refreshTokenService.deleteByToken(tokenString);

        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }
}