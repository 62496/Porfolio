package app.repository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.model.Difficulty;
import app.model.Mode;
import app.dto.Classement;
import app.dto.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests unitaires pour ClassementRepository
 * Utilise Mockito pour mocker ClassementDao selon l'approche du cours
 */
class ClassementRepositoryTest {

    private ClassementDao classementDao;
    private ClassementRepository classementRepository;

    private final User testUser = new User(1, "TestUser", "password");
    private final Classement testClassement1 = new Classement(100, "TestUser");
    private final Classement testClassement2 = new Classement(80, "AnotherUser");

    @BeforeEach
    void setUp() {
        // Créer le mock avant instanciation de l'objet testé
        classementDao = mock(ClassementDao.class);

        // Configurer les comportements du mock
        when(classementDao.getClassement(Mode.CLASSIC, 1, Difficulty.EASY))
                .thenReturn(Arrays.asList(testClassement1, testClassement2));
        when(classementDao.getRecord(Mode.CLASSIC, 1, 1, Difficulty.EASY))
                .thenReturn(100);
        when(classementDao.addScore(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(true);

        // Instancier le repository avec le mock (via la réflexion)
        classementRepository = new ClassementRepository();
        try {
            java.lang.reflect.Field daoField = ClassementRepository.class.getDeclaredField("classementDao");
            daoField.setAccessible(true);
            daoField.set(classementRepository, classementDao);
        } catch (Exception e) {
            fail("Impossible d'injecter le mock: " + e.getMessage());
        }
    }

    @Test
    void testGetClassement() {
        System.out.println("testGetClassement");
        // Arrange
        Mode mode = Mode.CLASSIC;
        int texteId = 1;
        Difficulty difficulty = Difficulty.EASY;

        // Action
        List<Classement> result = classementRepository.getClassement(mode, texteId, difficulty);

        // Assert
        assertEquals(2, result.size(), "Le classement devrait contenir 2 entrées");
        assertEquals(testClassement1, result.get(0), "La première entrée devrait correspondre");
        assertEquals(testClassement2, result.get(1), "La deuxième entrée devrait correspondre");

        verify(classementDao).getClassement(mode, texteId, difficulty);
    }

    @Test
    void testGetRecord() {
        System.out.println("testGetRecord");
        // Arrange
        Mode mode = Mode.CLASSIC;
        int userId = 1;
        int texteId = 1;
        Difficulty difficulty = Difficulty.EASY;

        // Action
        int result = classementRepository.getRecord(mode, userId, texteId, difficulty);

        // Assert
        assertEquals(100, result, "Le record devrait correspondre");

        verify(classementDao).getRecord(mode, userId, texteId, difficulty);
    }

    @Test
    void testAddScore() {
        System.out.println("testAddScore");
        // Arrange
        Mode mode = Mode.CLASSIC;
        Difficulty difficulty = Difficulty.EASY;
        int score = 150;
        int texteId = 2;

        // Action
        boolean result = classementRepository.addScore(mode, difficulty, testUser, score, texteId);

        // Assert
        assertTrue(result, "L'ajout du score devrait réussir");

        verify(classementDao).addScore(mode, difficulty, testUser, score, texteId);
    }
}

