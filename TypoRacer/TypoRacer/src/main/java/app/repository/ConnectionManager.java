package app.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
/**
 * Gère la connexion à la base de données SQLite.
 * Utilise un fichier de configuration `database.properties` pour charger dynamiquement le chemin selon l'OS.
 */
class ConnectionManager {
    private static Connection connection;

    private static Properties properties = null;

    /**
     * Charge les propriétés à partir du fichier `database.properties`.
     */
    private static Properties loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("database.properties")) {
                properties.load(input);
            } catch (IOException e) {
                throw new RepositoryException("Properties illisible", e);
            }
        }
        return properties;
    }
    /**
     * Récupère une connexion à la base de données.
     */
    static Connection getConnection() {
        if (connection == null) {
            try {
                Properties config = loadProperties();
                // Détection de l'OS
                String os = System.getProperty("os.name").toLowerCase();
                String key = os.contains("win") ? "db.pathWindows" : "db.pathMac";

                String dbPath = config.getProperty(key);

                if (dbPath == null || dbPath.isBlank()) {
                    throw new RepositoryException("Chemin de la base manquant dans database.properties (clÃ© : db.path)",new Exception());
                }

                File file = new File(dbPath);
                String absolutePath = file.getPath();
                connection = DriverManager.getConnection("jdbc:sqlite:" +absolutePath);
            } catch (SQLException ex) {
                throw  new RepositoryException("Connexion impossible",ex);
            }
        }
        return connection;
    }

    /**
     * Ferme la connexion ouverte si elle existe.
     */
    static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Fermeture impossible", ex);
        }
    }
}
