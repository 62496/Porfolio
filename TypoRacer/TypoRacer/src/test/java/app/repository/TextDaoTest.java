package app.repository;


import app.dto.Texte;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour TextDao
 */
class TextDaoTest {

    private static Connection connection;
    private TextDao instance;

    // Texte de test
    private final Texte texte1 = new Texte(1, "Titre Test", "Contenu de test", "TestAuteur");

    @BeforeAll
    static void setupDatabase() throws SQLException {
        // Créer une connexion à la base de données SQLite en mémoire
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Créer la table Textes pour les tests
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE Textes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        creator TEXT NOT NULL
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        // Initialiser l'instance de TextDao avec la connexion
        instance = new TextDao(connection);

        // Insérer des données de test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO Textes (title, content, creator) VALUES
                    ('Titre Test', 'Contenu de test', 'TestAuteur'),
                    ('Titre Test 2', 'Contenu de test 2', 'TestAuteur2');
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        // Nettoyer les données après chaque test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Textes");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        // Fermer la connexion à la base de données
        connection.close();
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        // Arrange
        int expectedCount = 2;

        // Action
        List<Texte> result = instance.findAll();

        // Assert
        assertEquals(expectedCount, result.size(), "Le nombre de textes récupérés devrait être 2");
        assertEquals("Titre Test", result.get(0).title(), "Le titre du premier texte devrait correspondre");
        assertEquals("Contenu de test", result.get(0).content(), "Le contenu du premier texte devrait correspondre");
        assertEquals("TestAuteur", result.get(0).creator(), "Le créateur du premier texte devrait correspondre");
    }

    @Test
    void testInsert() {
        System.out.println("testInsert");
        // Arrange
        Texte nouveauTexte = new Texte(0, "Nouveau Titre", "Nouveau Contenu", "NouvelAuteur");

        // Action
        instance.insert(nouveauTexte);

        // Assert
        List<Texte> allTextes = instance.findAll();
        assertEquals(3, allTextes.size(), "Le nombre de textes après insertion devrait être 3");

        boolean found = false;
        for (Texte texte : allTextes) {
            if ("Nouveau Titre".equals(texte.title())) {
                found = true;
                assertEquals("Nouveau Contenu", texte.content(), "Le contenu du nouveau texte devrait correspondre");
                assertEquals("NouvelAuteur", texte.creator(), "L'auteur du nouveau texte devrait correspondre");
                break;
            }
        }
        assertTrue(found, "Le nouveau texte devrait être présent dans la liste");
    }

    @Test
    void testGetConnection() {
        System.out.println("testGetConnection");
        // Arrange
        Connection expectedConnection = connection;

        // Action
        Connection result = instance.getConnection();

        // Assert
        assertSame(expectedConnection, result, "La connexion retournée devrait être celle fournie au constructeur");
    }
    @Test
    void testGetAllTitres() throws SQLException {
        System.out.println("testGetAllTitres");

        // Action
        List<String> titres = instance.getAllTitres();

        // Assert
        assertEquals(2, titres.size(), "Il devrait y avoir 2 titres extraits");
        assertTrue(titres.contains("Titre Test"), "Le titre 'Titre Test' devrait être présent");
        assertTrue(titres.contains("Titre Test 2"), "Le titre 'Titre Test 2' devrait être présent");
    }

}

