package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj2.booksta.model.User;

import io.jsonwebtoken.security.WeakKeyException;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String SECRET = "mySuperSecretKeyForTestingPurposes123456789";
    private final long EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "validityInMs", EXPIRATION);
        jwtService.init();
    }

    @Test
    void testGenerateToken() {
        User user = new User();
        user.setEmail("test@example.com");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(2, token.split("\\.").length - 1);
    }

    @Test
    void testExtractEmail() {
        User user = new User();
        String email = "john.doe@email.com";
        user.setEmail(email);

        String token = jwtService.generateToken(user);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail, "L'email extrait doit correspondre à celui du token");
    }

@Test
    void testInitWithWeakSecretThrowsException() {
        JwtService serviceWithWeakSecret = new JwtService();

        ReflectionTestUtils.setField(serviceWithWeakSecret, "secret", ""); 
        ReflectionTestUtils.setField(serviceWithWeakSecret, "validityInMs", EXPIRATION);

        assertThrows(WeakKeyException.class, () -> {
            serviceWithWeakSecret.init();
        });
    }
}
