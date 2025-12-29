package app.service;


import static org.junit.jupiter.api.Assertions.*;

import app.model.Difficulty;
import app.model.Mode;
import app.dto.Classement;
import app.dto.User;
import app.repository.ClassementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class ClassementServiceTest {

    private ClassementService classementService;
    private ClassementRepository mockRepo;

    @BeforeEach
    void setUp() throws Exception {
        mockRepo = mock(ClassementRepository.class);
        classementService = new ClassementService();

        // Injection du mock
        java.lang.reflect.Field repoField = ClassementService.class.getDeclaredField("repo");
        repoField.setAccessible(true);
        repoField.set(classementService, mockRepo);
    }

    @Test
    @DisplayName("getTopScores devrait déléguer au repository")
    void testGetTopScores() {
        // Arrange
        Mode mode = Mode.CLASSIC;
        int texteId = 1;
        Difficulty diff = Difficulty.NORMAL;
        List<Classement> expectedScores = Arrays.asList(
                new Classement(100, "User1"),
                new Classement(80, "User2")
        );
        when(mockRepo.getClassement(mode, texteId, diff)).thenReturn(expectedScores);

        // Act
        List<Classement> result = classementService.getTopScores(mode, texteId, diff);

        // Assert
        assertEquals(expectedScores, result);
        verify(mockRepo).getClassement(mode, texteId, diff);
    }

    @Test
    @DisplayName("getUserRecord devrait déléguer au repository")
    void testGetUserRecord() {
        // Arrange
        Mode mode = Mode.CLASSIC;
        int userId = 1;
        int texteId = 1;
        Difficulty diff = Difficulty.NORMAL;
        int expectedRecord = 100;
        when(mockRepo.getRecord(mode, userId, texteId, diff)).thenReturn(expectedRecord);

        // Act
        int result = classementService.getUserRecord(mode, userId, texteId, diff);

        // Assert
        assertEquals(expectedRecord, result);
        verify(mockRepo).getRecord(mode, userId, texteId, diff);
    }

    @Test
    @DisplayName("addScore devrait déléguer au repository")
    void testAddScore() {
        // Arrange
        Mode mode = Mode.CLASSIC;
        Difficulty diff = Difficulty.NORMAL;
        int score = 100;
        User user = new User(1, "TestUser", "password");
        int texteId = 1;

        // Act
        classementService.addScore(mode, diff, score, user, texteId);

        // Assert
        verify(mockRepo).addScore(mode, diff, user, score, texteId);
    }
}

