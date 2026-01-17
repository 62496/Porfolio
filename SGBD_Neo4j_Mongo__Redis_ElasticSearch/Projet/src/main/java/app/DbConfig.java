package app;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class that centralizes database configuration.
 * It reads the application.properties file located in src/main/resources.
 *
 * This class avoids hardcoding URLs, usernames, and passwords directly in the code.
 */
public final class DbConfig {

    /** Properties object containing all values read from the file. */
    private final Properties props = new Properties();

    /**
     * Constructor.
     * Loads the application.properties file during application startup.
     */
    public DbConfig() {
        try (InputStream in = DbConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            // If the file does not exist or is not found
            if (in == null) {
                throw new IllegalStateException(
                        "application.properties not found in src/main/resources"
                );
            }

            // Loading properties into the props object
            props.load(in);

        } catch (Exception e) {
            // Critical error: the application cannot function without configuration
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    // ----- MySQL Configuration -----

    /** @return MySQL connection URL */
    public String mysqlUrl() {
        return props.getProperty("mysql.url");
    }

    /** @return MySQL username */
    public String mysqlUser() {
        return props.getProperty("mysql.user");
    }

    /** @return MySQL password */
    public String mysqlPassword() {
        return props.getProperty("mysql.password");
    }

    // ----- MongoDB Configuration -----

    /** @return MongoDB connection URI */
    public String mongoUri() {
        return props.getProperty("mongo.uri");
    }

    /** @return Name of the MongoDB database used */
    public String mongoDb() {
        return props.getProperty("mongo.db");
    }

    // ----- Neo4j Configuration -----

    /** @return Neo4j connection URI */
    public String neo4jUri() {
        return props.getProperty("neo4j.uri");
    }

    /** @return Neo4j username */
    public String neo4jUser() {
        return props.getProperty("neo4j.user");
    }

    /** @return Neo4j password */
    public String neo4jPassword() {
        return props.getProperty("neo4j.password");
    }

    /** @return Redis host */
    public String redisHost() { return "localhost"; }
    
    /** @return Redis port */
    public int redisPort() { return 6379; }

    /** @return Elasticsearch connection URL */
    public String elasticsearchUrl() {
        return props.getProperty("elasticsearch.url");
    }
}