package app.repository;

import app.dto.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour UserDao
 */
class UserDaoTest {

    private static Connection connection;
    private UserDao instance;

    // Utilisateur de test
    private final User testUser1 = new User(1, "TestUser1", "password1");
    private final User testUser2 = new User(2, "TestUser2", "password2");

    @BeforeAll
    static void setupDatabase() throws SQLException {
        // Créer une connexion à la base de données SQLite en mémoire
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Créer la table users pour les tests
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE users (
                        id INTEGER PRIMARY KEY,
                        name TEXT NOT NULL,
                        password TEXT NOT NULL
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        // Initialiser l'instance de UserDao avec la connexion
        instance = new UserDao(connection);

        // Insérer des données de test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO users (id, name, password) VALUES
                    (1, 'TestUser1', 'password1'),
                    (2, 'TestUser2', 'password2');
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        // Nettoyer les données après chaque test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users");
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
        List<User> result = instance.findAll();

        // Assert
        assertEquals(expectedCount, result.size(), "Le nombre d'utilisateurs récupérés devrait être 2");
        assertEquals(1, result.get(0).id(), "L'ID du premier utilisateur devrait être 1");
        assertEquals("TestUser1", result.get(0).name(), "Le nom du premier utilisateur devrait être TestUser1");
        assertEquals("password1", result.get(0).password(), "Le mot de passe du premier utilisateur devrait être password1");
    }

    @Test
    void testCheckDataExist() {
        User user = new User(1, "TestUser1", "password1");
        Optional<User> expected = Optional.of(user);

        Optional<User> result = instance.checkData(user);

        assertTrue(result.isPresent(), "L'utilisateur devrait être trouvé");
        assertEquals(expected.get().id(), result.get().id(), "L'ID de l'utilisateur devrait correspondre");
        assertEquals(expected.get().name(), result.get().name(), "Le nom de l'utilisateur devrait correspondre");
        assertEquals(expected.get().password(), result.get().password(), "Le mot de passe de l'utilisateur devrait correspondre");
    }

    @Test
    void testCheckDataDoesNotExist() {
        // Arrange
        User user = new User(99, "NonExistant", "wrongPassword");

        // Action
        Optional<User> result = instance.checkData(user);

        // Assert
        assertFalse(result.isPresent(), "L'utilisateur ne devrait pas être trouvé");
    }

    @Test
    void testSaveUser() {
        System.out.println("testSaveUser");
        // Arrange
        User newUser = new User(3, "NewUser", "newPassword");

        // Action
        int generatedId = instance.saveUser(newUser);

        // Assert
        assertTrue(generatedId > 0, "L'ID généré devrait être positif");

        // Vérifier que l'utilisateur a bien été ajouté
        List<User> allUsers = instance.findAll();
        assertEquals(3, allUsers.size(), "Le nombre d'utilisateurs après insertion devrait être 3");

        boolean found = false;
        for (User user : allUsers) {
            if (user.id() == generatedId) {
                found = true;
                assertEquals("NewUser", user.name(), "Le nom de l'utilisateur devrait correspondre");
                assertEquals("newPassword", user.password(), "Le mot de passe de l'utilisateur devrait correspondre");
                break;
            }
        }
        assertTrue(found, "L'utilisateur ajouté devrait être présent dans la liste");
    }

    @Test
    void testDeleteById() {
        System.out.println("testDeleteById");
        // Arrange
        int userId = 1;
        int initialCount = instance.findAll().size();

        // Action
        instance.deleteById(userId);

        // Assert
        List<User> afterDelete = instance.findAll();
        assertEquals(initialCount - 1, afterDelete.size(), "Le nombre d'utilisateurs après suppression devrait être réduit de 1");

        boolean found = false;
        for (User user : afterDelete) {
            if (user.id() == userId) {
                found = true;
                break;
            }
        }
        assertFalse(found, "L'utilisateur supprimé ne devrait plus être présent");
    }
}

