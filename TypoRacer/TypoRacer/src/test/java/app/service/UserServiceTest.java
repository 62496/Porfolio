package app.service;

import app.model.Auth;
import app.dto.User;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository mockRepository;
    private Auth mockAuth;

    @BeforeEach
    void setUp() {
        mockRepository = mock(UserRepository.class);
        mockAuth = mock(Auth.class);
        userService = new UserService(mockRepository, mockAuth);
    }

    @Test
    void loginShouldReturnEmptyWhenValidationFails() {
        when(mockAuth.validateForm(anyString(), anyString(), anyString())).thenReturn(false);

        Optional<User> result = userService.login("user", "1", "pass");

        assertTrue(result.isEmpty());
        verify(mockRepository, never()).checkData(any());
    }

    @Test
    void loginShouldReturnUserWhenValidationPasses() {
        User user = new User(1, "user", "pass");
        when(mockAuth.validateForm(anyString(), anyString(), anyString())).thenReturn(true);
        when(mockRepository.checkData(any())).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("user", "1", "pass");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void validateFieldsShouldDelegateToAuth() {
        // Arrange
        when(mockAuth.validateForm("user", "1", "pass")).thenReturn(true);

        // Act
        boolean result = userService.validateFields("user", "1", "pass");

        // Assert
        assertTrue(result);
        verify(mockAuth).validateForm("user", "1", "pass");
    }

    @Test
    void registerShouldReturnMinusOneWhenValidationFails() {
        // Arrange
        when(mockAuth.validateForm(anyString(), anyString(), anyString())).thenReturn(false);

        // Act
        int result = userService.register("user", "1", "pass");

        // Assert
        assertEquals(-1, result);
        verify(mockRepository, never()).saveUser(any());
    }

    @Test
    void registerShouldReturnUserIdWhenValidationPasses() {
        // Arrange
        User user = new User(1, "user", "pass");
        when(mockAuth.validateForm(anyString(), anyString(), anyString())).thenReturn(true);
        when(mockRepository.saveUser(any(User.class))).thenReturn(1);

        // Act
        int result = userService.register("user", "1", "pass");

        // Assert
        assertEquals(1, result);
        verify(mockRepository).saveUser(any(User.class));
    }

    @Test
    void loginShouldHandleNumericFormatException() {
        // Arrange - ID non numérique
        when(mockAuth.validateForm("user", "non_numeric", "pass")).thenReturn(true);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            userService.login("user", "non_numeric", "pass");
        });
    }

    @Test
    void loginShouldReturnEmptyWhenUserNotFound() {
        // Arrange
        when(mockAuth.validateForm(anyString(), anyString(), anyString())).thenReturn(true);
        when(mockRepository.checkData(any(User.class))).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.login("user", "1", "pass");

        // Assert
        assertTrue(result.isEmpty());
    }
}

