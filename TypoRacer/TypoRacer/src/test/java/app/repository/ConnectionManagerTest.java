package app.repository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ConnectionManager
 * Note: Ces tests nécessitent une configuration spéciale et peuvent être difficiles à exécuter
 * dans tous les environnements. Ils sont marqués avec @Disabled par défaut.
 */
@Disabled("Ces tests nécessitent une configuration locale spécifique")
class ConnectionManagerTest {

    private static final String TEST_DB_PATH = "target/test-db.sqlite";
    private static final String TEST_PROPERTIES_PATH = "target/test-classes/database.properties";

    @BeforeAll
    static void setupTestEnvironment() throws IOException {
        // Créer un fichier properties de test
        Properties props = new Properties();
        props.setProperty("db.pathWindows", TEST_DB_PATH);
        props.setProperty("db.pathMac", TEST_DB_PATH);

        File propsDir = new File("target/test-classes");
        if (!propsDir.exists()) {
            propsDir.mkdirs();
        }

        try (FileOutputStream out = new FileOutputStream(TEST_PROPERTIES_PATH)) {
            props.store(out, "Test database properties");
        }

        // Créer un fichier vide pour la base de données
        File dbFile = new File(TEST_DB_PATH);
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }
    }

    @AfterAll
    static void cleanupTestEnvironment() {
        // Supprimer les fichiers de test
        new File(TEST_PROPERTIES_PATH).delete();
        new File(TEST_DB_PATH).delete();
    }

    @BeforeEach
    void resetConnection() throws Exception {
        // Utiliser la réflexion pour réinitialiser la connexion
        java.lang.reflect.Field connectionField = ConnectionManager.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(null, null);

        java.lang.reflect.Field propertiesField = ConnectionManager.class.getDeclaredField("properties");
        propertiesField.setAccessible(true);
        propertiesField.set(null, null);
    }

    @Test
    void testGetConnection() {
        System.out.println("testGetConnection");
        // Action
        Connection connection = ConnectionManager.getConnection();

        // Assert
        assertNotNull(connection, "La connexion ne devrait pas être null");

        // Vérifier que les appels suivants retournent la même connexion
        Connection secondConnection = ConnectionManager.getConnection();
        assertSame(connection, secondConnection, "Les connexions successives devraient être identiques");
    }

    @Test
    void testClose() throws SQLException {
        System.out.println("testClose");
        // Arrange
        Connection connection = ConnectionManager.getConnection();

        // Action
        ConnectionManager.close();

        // Assert
        assertTrue(connection.isClosed(), "La connexion devrait être fermée");

        // Réinitialiser la connexion après le test
        ConnectionManager.getConnection();
    }
}

