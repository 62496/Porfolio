package app.service;
import app.repository.TextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TexteServiceTest {
    private TexteService texteService;
    private TextRepository mockRepository;

    @BeforeEach
    void setUp() throws Exception {
        mockRepository = mock(TextRepository.class);
        texteService = new TexteService();

        // Injecter le mock
        Field repoField = TexteService.class.getDeclaredField("textRepository");
        repoField.setAccessible(true);
        repoField.set(texteService, mockRepository);
    }

    @Test
    void isValidTextShouldReturnFalseForShortText() {
        boolean result = texteService.isValidText("Short");

        assertFalse(result);
    }

    @Test
    void isValidTextShouldReturnTrueForLongText() {
        String longText = "a".repeat(100);

        boolean result = texteService.isValidText(longText);

        assertTrue(result);
    }


    @Test
    void testIsValidTitle_EmptyTitle() throws SQLException {
        // Arrange - Titre vide

        // Act
        boolean result = texteService.isValidTitle("");

        // Assert
        assertFalse(result, "Un titre vide ne devrait pas être valide");
    }
    @Test
    void testIsValidTitle_DuplicateTitle() throws SQLException {
        // Arrange - Configurer le mock pour simuler un titre existant
        List<String> existingTitles = Arrays.asList("Titre Existant");
        when(mockRepository.getAllTitres()).thenReturn(existingTitles);

        // Act - Tenter de valider un titre qui existe déjà
        boolean result = texteService.isValidTitle("Titre Existant");

        // Assert
        assertFalse(result, "Un titre déjà existant ne devrait pas être valide");

        // Verify - Vérifier que la méthode getAllTitres a été appelée
        verify(mockRepository).getAllTitres();
    }

    @Test
    void testIsValidTitle_ValidTitle() throws SQLException {
        // Arrange - Nouveau titre valide

        // Act
        boolean result = texteService.isValidTitle("Nouveau Titre");

        // Assert
        assertTrue(result, "Un nouveau titre non vide devrait être valide");
    }

}
