package app.service;
import app.model.Difficulty;
import app.model.GameConfig;
import app.model.Mode;
import app.dto.Texte;
import app.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameConfigServiceTest {

    private GameConfigService configService;
    private GameConfig mockConfig;
    private User testUser;
    private Texte testTexte;

    @BeforeEach
    void setUp() throws Exception {
        configService = new GameConfigService();
        mockConfig = mock(GameConfig.class);
        testUser = new User(1, "TestUser", "password");
        testTexte = new Texte(1, "Titre", "Contenu", "Auteur");

        // Injection du mock
        java.lang.reflect.Field configField = GameConfigService.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configService, mockConfig);
    }

    @Test
    @DisplayName("isReady devrait vérifier tous les éléments nécessaires")
    void testTexteIsReady() {

        // Arrange
        when(mockConfig.getUser()).thenReturn(testUser);
        when(mockConfig.getMode()).thenReturn(Mode.CLASSIC);
        when(mockConfig.getDifficulty()).thenReturn(Difficulty.NORMAL);

        // Act & Assert
        assertTrue(configService.modeIsReady());

        // Tester avec un élément manquant
        when(mockConfig.getText()).thenReturn(null);
        assertFalse(configService.texteIsReady());
    }

    @Test
    @DisplayName("modeIsReady devrait vérifier mode et difficulté")
    void testModeIsReady() {
        // Arrange
        when(mockConfig.getMode()).thenReturn(Mode.CLASSIC);
        when(mockConfig.getDifficulty()).thenReturn(Difficulty.NORMAL);

        // Act & Assert
        assertTrue(configService.modeIsReady());

        // Tester avec un élément manquant
        when(mockConfig.getMode()).thenReturn(null);
        assertFalse(configService.modeIsReady());
    }


    @Test
    @DisplayName("setters devraient appeler les méthodes correspondantes sur config")
    void testSetters() {
        // Act
        configService.setUser(testUser);
        configService.setMode(Mode.CLASSIC);
        configService.setDifficulty(Difficulty.NORMAL);
        configService.setTexte(testTexte);

        // Assert
        verify(mockConfig).setUser(testUser);
        verify(mockConfig).setMode(Mode.CLASSIC);
        verify(mockConfig).setDifficulty(Difficulty.NORMAL);
        verify(mockConfig).setTexte(testTexte);
    }

    @Test
    @DisplayName("getters devraient appeler les méthodes correspondantes sur config")
    void testGetters() {
        // Arrange
        when(mockConfig.getMode()).thenReturn(Mode.CLASSIC);
        when(mockConfig.getDifficulty()).thenReturn(Difficulty.NORMAL);
        when(mockConfig.getUser()).thenReturn(testUser);
        when(mockConfig.getText()).thenReturn(testTexte);

        // Act
        Mode mode = configService.getMode();
        Difficulty difficulty = configService.getDifficulty();
        User user = configService.getUser();
        Texte texte = configService.getText();

        // Assert
        assertEquals(Mode.CLASSIC, mode);
        assertEquals(Difficulty.NORMAL, difficulty);
        assertEquals(testUser, user);
        assertEquals(testTexte, texte);
    }
}

