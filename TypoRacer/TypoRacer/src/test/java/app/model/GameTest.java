package app.model;

import app.dto.Texte;
import app.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe Game adaptés aux propriétés JavaFX
 */
class GameTest {

    private Game game;
    private GameConfig gameConfig;

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
        gameConfig = new GameConfig();
        gameConfig.setUser(new User(1, "TestUser", "password"));
        gameConfig.setMode(Mode.CLASSIC);
        gameConfig.setDifficulty(Difficulty.NORMAL);

        // Créer un texte de test
        Texte texteTest = new Texte(1, "Titre", "Ceci est une phrase test. Voici une autre phrase.", "TestUser");
        gameConfig.setTexte(texteTest);

        game = new Game(gameConfig);
    }

    @Test
    @DisplayName("Test de la méthode typing pour une entrée correcte")
    void testTypingCorrect() {
        System.out.println("testTypingCorrect");
        // Score initial
        int initialScore = game.scoreProperty().get();

        // La lettre 'C' est la première lettre de "Ceci est une phrase test"
        boolean result = game.typing("C");

        assertTrue(result, "Typing devrait retourner true pour une lettre correcte");
        assertTrue(game.scoreProperty().get() > initialScore, "Le score devrait augmenter après une saisie correcte");
    }

    @Test
    @DisplayName("Test de la méthode typing pour une entrée incorrecte")
    void testTypingIncorrect() {
        System.out.println("testTypingIncorrect");
        // Score initial
        int initialScore = game.scoreProperty().get();

        // La lettre 'X' n'est pas la première lettre de "Ceci est une phrase test"
        boolean result = game.typing("X");

        assertFalse(result, "Typing devrait retourner false pour une lettre incorrecte");
        assertTrue(game.scoreProperty().get() < initialScore, "Le score devrait diminuer après une saisie incorrecte");
    }

    @Test
    @DisplayName("Test de la méthode isCorrectLetter")
    void testIsCorrectLetter() {
        System.out.println("testIsCorrectLetter");
        assertTrue(game.typing("C"), "La lettre C devrait être correcte à l'index 0");
        assertFalse(game.typing("X"), "La lettre X devrait être incorrecte à l'index 0");
        assertTrue(game.typing("Ce"), "La lettre C devrait être incorrecte à l'index 1");
    }

    @Test
    @DisplayName("Test de la méthode nextSentences")
    void testNextSentences() {
        System.out.println("testNextSentences");
        String completePhrase = "Ceci est une phrase test";

        assertTrue(game.nextSentences(completePhrase),
                "Devrait passer à la phrase suivante quand la phrase complète est saisie");
        assertEquals("Voici une autre phrase", game.getPhraseActuelle(),
                "La phrase actuelle devrait être mise à jour");
    }

    @Test
    @DisplayName("Test de la méthode getMaxTime en fonction de la difficulté")
    void testGetMaxTime() {
        System.out.println("testGetMaxTime");
        // Difficulté NORMAL (définie dans setUp)
        assertEquals(60, game.getMaxTime(), "Le temps maximum pour la difficulté NORMAL devrait être 60");

        // Changer la difficulté
        gameConfig.setDifficulty(Difficulty.EASY);
        assertEquals(90, game.getMaxTime(), "Le temps maximum pour la difficulté EASY devrait être 90");

        gameConfig.setDifficulty(Difficulty.HARDCORE);
        assertEquals(30, game.getMaxTime(), "Le temps maximum pour la difficulté HARDCORE devrait être 30");
    }

    @Test
    @DisplayName("Test de la méthode getProgress")
    void testGetProgress() {
        System.out.println("testGetProgress");
        // Au départ, nous sommes à la première phrase sur deux
        double expectedProgress = 0.0;
        assertEquals(expectedProgress, game.getProgress(), 0.01,
                "La progression initiale devrait être 0.0");

        // Passer à la phrase suivante
        game.nextSentences("Ceci est une phrase test");
        expectedProgress = 0.5; // 1 sur 2 phrases
        assertEquals(expectedProgress, game.getProgress(), 0.01,
                "La progression après une phrase devrait être 0.5");
    }

    @Test
    @DisplayName("Test des propriétés score et combo")
    void testScoreAndComboProperties() {
        System.out.println("testScoreAndComboProperties");
        // Valeurs initiales
        assertEquals(0, game.scoreProperty().get(), "Le score initial devrait être 0");
        assertEquals(1.0, game.comboProperty().get(), 0.01, "Le combo initial devrait être 1.0");

        // Après une saisie correcte
        game.typing("C");
        assertTrue(game.scoreProperty().get() > 0, "Le score devrait augmenter après une saisie correcte");
        assertTrue(game.comboProperty().get() > 1.0, "Le combo devrait augmenter après une saisie correcte");

        // Observer les changements de propriétés
        boolean[] scoreChanged = {false};
        boolean[] comboChanged = {false};

        game.scoreProperty().addListener((obs, oldVal, newVal) -> scoreChanged[0] = true);
        game.comboProperty().addListener((obs, oldVal, newVal) -> comboChanged[0] = true);

        // Provoquer des changements
        game.typing("Ce");

        assertTrue(scoreChanged[0], "La propriété score devrait notifier des changements");
        assertTrue(comboChanged[0], "La propriété combo devrait notifier des changements");
    }
    @Test
    @DisplayName("Test de la méthode restart")
    void testRestart() {
        game.typing("C");
        assertTrue(game.scoreProperty().get() > 0, "Le score devrait être > 0 avant le redémarrage");

        game.restart();

        assertEquals(0, game.scoreProperty().get(), "Le score devrait être réinitialisé après restart");
        assertEquals(1.0, game.comboProperty().get(), 0.01, "Le combo devrait être réinitialisé après restart");
        assertEquals("Ceci est une phrase test", game.getPhraseActuelle(), "Devrait revenir à la première phrase");
    }
    @Test
    @DisplayName("Test des méthodes getPhraseActuelle et getPhraseToDisplay")
    void testGetPhrases() {
        assertEquals("Ceci est une phrase test", game.getPhraseActuelle(), "Phrase actuelle incorrecte");
        assertEquals("Ceci est une phrase test", game.getPhraseToDisplay(), "Phrase affichée incorrecte");

        game.nextSentences("Ceci est une phrase test");

        assertEquals("Voici une autre phrase", game.getPhraseActuelle(), "Phrase suivante incorrecte");
    }

    @Test
    @DisplayName("Test de la méthode getFinalScore")
    void testGetFinalScore() {
        game.typing("Ceci est une phrase test");
        game.typing("Voici une autre phrase");

        assertTrue(game.getFinalScore() > 0, "Le score final devrait être positif");
    }
    @Test
    @DisplayName("Test des getters de configuration")
    void testGettersConfig() {
        assertEquals(Mode.CLASSIC, game.getMode(), "Le mode de jeu devrait être CLASSIC");
        assertEquals(Difficulty.NORMAL, game.getDifficulty(), "La difficulté devrait être NORMAL");
        assertEquals(1, game.getUser().id(), "L'utilisateur devrait avoir l'ID 1");
        assertEquals(1, game.getTextId(), "L'ID du texte devrait être 1");
    }

}

