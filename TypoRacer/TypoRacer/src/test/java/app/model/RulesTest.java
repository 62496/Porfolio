package app.model;

import app.dto.Texte;
import app.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe Rules adaptés aux propriétés JavaFX
 */
class RulesTest {

    private Rules rules;
    private GameConfig gameConfig;

    @BeforeEach
    void setUp() {
        // Création des objets nécessaires
        gameConfig = new GameConfig();
        gameConfig.setUser(new User(1, "TestUser", "password"));
        gameConfig.setMode(Mode.CLASSIC);
        gameConfig.setDifficulty(Difficulty.NORMAL);

        // Création d'un texte de test avec plusieurs phrases
        Texte texteTest = new Texte(1, "Titre",
                "Ceci est une phrase test. Voici une autre phrase.",
                "TestUser");
        gameConfig.setTexte(texteTest);

        rules = new Rules(gameConfig);
    }

    @Test
    @DisplayName("Test du découpage par phrase")
    void testDecouperParPhrase() {
        System.out.println("testDecouperParPhrase");
        // Le texte contient 2 phrases, donc il devrait y avoir 2 phrases
        assertEquals(2, rules.getTotalPhrases(), "Le texte devrait être découpé en 2 phrases");
        assertEquals("Ceci est une phrase test", rules.getPhraseActuelle(),
                "La première phrase devrait être correctement extraite");
    }

    @Test
    @DisplayName("Test de la normalisation de texte")
    void testNormalizeText() {
        System.out.println("testNormalizeText");
        String input = "C'est l'été";
        String expectedOutput = "C'est l'été"; // Remplacement de l'apostrophe typographique

        assertEquals(expectedOutput, Rules.normalizeText(input),
                "Le texte normalisé devrait remplacer les apostrophes typographiques");
    }

    @Test
    @DisplayName("Test de suppression des accents")
    void testRemoveAccents() {
        System.out.println("testRemoveAccents");
        String input = "Éléphant à l'école";
        String expectedOutput = "Elephant a l'ecole";

        assertEquals(expectedOutput, Rules.removeAccents(input),
                "Les accents devraient être supprimés du texte");
    }

    @Test
    @DisplayName("Test du découpage par mot")
    void testDecouperParMot() {
        System.out.println("testDecouperParMot");
        String phrase = "Ceci est un test";
        var mots = Rules.decouperParMot(phrase);

        assertEquals(4, mots.size(), "La phrase devrait être découpée en 4 mots");
        assertEquals("Ceci", mots.get(0), "Le premier mot devrait être correctement extrait");
        assertEquals("est", mots.get(1), "Le deuxième mot devrait être correctement extrait");
    }

    @Test
    @DisplayName("Test de la mise à jour du combo pour une lettre correcte")
    void testUpdateComboCorrectLetter() {
        System.out.println("testUpdateComboCorrectLetter");
        // Valeur initiale du combo
        double initialCombo = rules.comboProperty().get();

        // Saisie d'une lettre correcte
        boolean result = rules.updateCombo("C");

        assertTrue(result, "Le résultat devrait être true pour une lettre correcte");
        assertTrue(rules.comboProperty().get() > initialCombo, "Le multiplicateur de combo devrait augmenter");
    }

    @Test
    @DisplayName("Test de la mise à jour du combo pour une lettre incorrecte")
    void testUpdateComboIncorrectLetter() {
        System.out.println("testUpdateComboIncorrectLetter");
        // Valeur initiale du combo
        double initialCombo = rules.comboProperty().get();

        // Saisie d'une lettre incorrecte
        boolean result = rules.updateCombo("X");

        assertFalse(result, "Le résultat devrait être false pour une lettre incorrecte");
        assertTrue(rules.comboProperty().get() < initialCombo, "Le multiplicateur de combo devrait diminuer");
    }

    @Test
    @DisplayName("Test de passage à la phrase suivante")
    void testNextSentences() {
        System.out.println("testNextSentences");
        // Simuler la saisie d'une phrase complète
        String completePhrase = "Ceci est une phrase test";

        assertTrue(rules.nextSentences(completePhrase), "Devrait passer à la phrase suivante");
        assertEquals("Voici une autre phrase", rules.getPhraseActuelle(), "L'index de la phrase devrait être incrémenté");
        assertEquals("Voici une autre phrase", rules.getPhraseActuelle(),
                "La phrase actuelle devrait être la deuxième phrase");
    }

    @Test
    @DisplayName("Test de mise à jour du score")
    void testUpdateScore() {
        System.out.println("testUpdateScore");
        // Score initial
        int initialScore = rules.scoreProperty().get();

        // Mise à jour pour une frappe correcte
        rules.updateScore(true);
        assertEquals(initialScore + 1, rules.scoreProperty().get(),
                "Le score devrait augmenter de 1 pour une frappe correcte");

        // Mise à jour pour une frappe incorrecte
        rules.updateScore(false);
        assertEquals(initialScore, rules.scoreProperty().get(),
                "Le score devrait diminuer de 1 pour une frappe incorrecte");
    }

    @Test
    @DisplayName("Test de la mise à jour de la progression")
    void testUpdateProgress() {
        System.out.println("testUpdateProgress");
        // Au départ, nous sommes à la première phrase sur deux
        double expectedProgress = 0.0;
        assertEquals(expectedProgress, rules.updateProgress(), 0.01,
                "La progression initiale devrait être 0.0");

        // Méthode nextPhrase n'est pas publique, on utilise nextSentences à la place
        rules.nextSentences("Ceci est une phrase test");
        expectedProgress = 0.5; // 1 sur 2 phrases
        assertEquals(expectedProgress, rules.updateProgress(), 0.01,
                "La progression après une phrase devrait être 0.5");
    }

    @Test
    @DisplayName("Test mode Mort Subite")
    void testModesMortSubite() {
        System.out.println("testModesMortSubite");
        // Créer une config pour le mode mort subite
        GameConfig mortSubiteConfig = new GameConfig();
        mortSubiteConfig.setUser(new User(1, "TestUser", "password"));
        mortSubiteConfig.setMode(Mode.MORTSUBITE);
        mortSubiteConfig.setDifficulty(Difficulty.NORMAL);
        mortSubiteConfig.setTexte(new Texte(1, "Test", "Mot1 Mot2 Mot3 Mot4", "TestUser"));

        Rules mortSubiteRules = new Rules(mortSubiteConfig);

        // En mode mort subite, le texte est découpé par mot
        assertEquals("Mot1", mortSubiteRules.getPhraseActuelle(),
                "En mode MORTSUBITE, la phrase actuelle devrait être un mot");

        // Passer au mot suivant
        mortSubiteRules.nextSentences("Mot1");
        assertEquals("Mot2", mortSubiteRules.getPhraseActuelle(), "Le mot suivant devrait être Mot2");
    }

    @Test
    @DisplayName("Test mode Devinette avec accents")
    void testModeDevinetteAccents() {
        System.out.println("testModeDevinetteAccents");
        // Créer une config pour le mode devinette
        GameConfig devinetteConfig = new GameConfig();
        devinetteConfig.setUser(new User(1, "TestUser", "password"));
        devinetteConfig.setMode(Mode.DEVINETTE);
        devinetteConfig.setDifficulty(Difficulty.NORMAL);
        devinetteConfig.setTexte(new Texte(1, "Test", "Éléphant à l'école. Français été.", "TestUser"));

        Rules devinetteRules = new Rules(devinetteConfig);

        // En mode devinette, les accents sont supprimés
        assertEquals("Elephant a l'ecole", devinetteRules.getPhraseToDisplay(),
                "En mode DEVINETTE, les accents devraient être supprimés");

        // Passer à la phrase suivante
        devinetteRules.nextSentences("Elephant a l'ecole");
        assertEquals("Francais ete", devinetteRules.getPhraseToDisplay(),
                "La phrase suivante devrait aussi être sans accents");
    }

    @Test
    @DisplayName("Test mode CLASSIC")
    void testPhraseModeClassic(){
        String commonText = "Première phrase avec accent. Deuxième mot.";
        Texte texteTest = new Texte(1, "Test", commonText, "TestUser");
        User testUser = new User(1, "TestUser", "password");

        GameConfig classicConfig = new GameConfig();


        classicConfig.setUser(testUser);
        classicConfig.setMode(Mode.CLASSIC);
        classicConfig.setTexte(texteTest);
        Rules classicRules = new Rules(classicConfig);


        assertEquals("Première phrase avec accent", classicRules.getPhraseActuelle(),
                "En mode CLASSIC, la phrase complète devrait être utilisée");
        assertEquals(2, classicRules.getTotalPhrases(), "Devrait avoir 2 phrases");

    }

    @Test
    @DisplayName("Test des propriétés score et combo")
    void testScoreAndComboProperties() {
        System.out.println("testScoreAndComboProperties");
        // Valeurs initiales
        assertEquals(0, rules.scoreProperty().get(), "Le score initial devrait être 0");
        assertEquals(1.0, rules.comboProperty().get(), 0.01, "Le combo initial devrait être 1.0");

        // Modification des propriétés
        rules.scoreProperty().set(10);
        rules.comboProperty().set(1.5);

        assertEquals(10, rules.scoreProperty().get(), "Le score devrait être mis à jour");
        assertEquals(1.5, rules.comboProperty().get(), 0.01, "Le combo devrait être mis à jour");
    }

    @Test
    @DisplayName("Test du mode et difficulté")
    void testGetModeAndDifficulty() {
        System.out.println("testGetModeAndDifficulty");
        assertEquals(Mode.CLASSIC, rules.getMode(), "Le mode devrait être CLASSIC");
        assertEquals(Difficulty.NORMAL, rules.getDifficulty(), "La difficulté devrait être NORMAL");
    }

    @Test
    @DisplayName("Test de hasNextPhrase")
    void testHasNextPhrase() {
        // Initial state - has next phrase
        assertTrue(rules.hasNextPhrase(), "Devrait avoir une phrase suivante au début");

        // Move to last phrase
        rules.nextSentences("Ceci est une phrase test");
        assertFalse(rules.hasNextPhrase(), "Ne devrait plus avoir de phrase suivante à la fin");
    }

    @Test
    @DisplayName("Test des méthodes de récupération des IDs")
    void testGetIdsAndUser() {
        assertEquals(1, rules.getTextId(), "L'ID du texte devrait être correct");
        assertEquals(1, rules.getUserId(), "L'ID de l'utilisateur devrait être correct");
        assertEquals(gameConfig.getUser(), rules.getUser(), "L'utilisateur retourné devrait être celui de la config");
    }
}

