package app.model;

import app.dto.Texte;
import app.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe GameConfig
 */
class GameConfigTest {

    private GameConfig gameConfig;
    private User testUser;
    private Texte testTexte;

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
        gameConfig = new GameConfig();
        testUser = new User(1, "TestUser", "password");
        testTexte = new Texte(1, "Test", "Ceci est un texte test.", "TestUser");

        gameConfig.setUser(testUser);
        gameConfig.setTexte(testTexte);
    }

    @Test
    @DisplayName("Test des limites de temps pour la difficulté EASY")
    void testTimeLimitEasy() {
        System.out.println("testTimeLimitEasy");
        gameConfig.setDifficulty(Difficulty.EASY);
        assertEquals(90, gameConfig.getTimeLimit(), "La limite de temps pour EASY devrait être 90 secondes");
    }

    @Test
    @DisplayName("Test des limites de temps pour la difficulté NORMAL")
    void testTimeLimitNormal() {
        System.out.println("testTimeLimitNormal");
        gameConfig.setDifficulty(Difficulty.NORMAL);
        assertEquals(60, gameConfig.getTimeLimit(), "La limite de temps pour NORMAL devrait être 60 secondes");
    }

    @Test
    @DisplayName("Test des limites de temps pour la difficulté HARDCORE")
    void testTimeLimitHardcore() {
        System.out.println("testTimeLimitHardcore");
        gameConfig.setDifficulty(Difficulty.HARDCORE);
        assertEquals(30, gameConfig.getTimeLimit(), "La limite de temps pour HARDCORE devrait être 30 secondes");
    }

    @Test
    @DisplayName("Test des getters et setters")
    void testGettersAndSetters() {
        System.out.println("testGettersAndSetters");
        Mode mode = Mode.CLASSIC;
        Difficulty difficulty = Difficulty.NORMAL;

        gameConfig.setMode(mode);
        gameConfig.setDifficulty(difficulty);

        assertEquals(mode, gameConfig.getMode(), "Le mode devrait être CLASSIC");
        assertEquals(difficulty, gameConfig.getDifficulty(), "La difficulté devrait être NORMAL");
        assertEquals(testUser, gameConfig.getUser(), "L'utilisateur devrait être celui défini dans le setUp");
        assertEquals(testTexte, gameConfig.getText(), "Le texte devrait être celui défini dans le setUp");
    }

    @ParameterizedTest
    @EnumSource(Mode.class)
    @DisplayName("Test des modes de jeu")
    void testAllModes(Mode mode) {
        System.out.println("testAllModes: " + mode);
        gameConfig.setMode(mode);
        assertEquals(mode, gameConfig.getMode(), "Le mode devrait correspondre à celui défini");
    }
}