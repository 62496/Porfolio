package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import app.DbConfig;

/**
 * MySQL repository for core user data.
 *
 * Responsibilities:
 * - create users (if not already existing)
 * - list users
 * - lookup user ids/usernames
 */
public final class UserRepositorySql {

    /** The JDBC URL for MySQL. */
    private final String url;

    /** The database username. */
    private final String user;
 
    /** The database password. */
    private final String password;

    /**
     * Creates the repository using {@link DbConfig}.
     *
     * @param cfg application database configuration
     */
    public UserRepositorySql(DbConfig cfg) {
        this.url = cfg.mysqlUrl();
        this.user = cfg.mysqlUser();
        this.password = cfg.mysqlPassword();
    }

    /**
     * Helper to open a SQL connection.
     * @return a new Connection
     * @throws SQLException on connection error
     */
    private Connection conn() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Resets user tables for a clean demo scenario.
     * Temporarily disables foreign key checks to allow truncation.
     */
    public void resetDemo() {
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            st.execute("TRUNCATE TABLE users");
            st.execute("SET FOREIGN_KEY_CHECKS=1");
        } catch (SQLException e) {
            throw new RuntimeException("resetDemo failed", e);
        }
    }

    /**
     * Persists a new user. Returns existing ID if username is taken.
     *
     * @param username the unique username
     * @return the generated or existing user ID
     */
    public long createUser(String username) {
        Long existing = findUserIdByUsername(username);
        if (existing != null) {
            return existing;
        }

        String sql = "INSERT INTO users(username) VALUES (?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new IllegalStateException("No generated key returned");
            }

        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
    }

    /**
     * Retrieves all user IDs stored in the SQL database.
     *
     * @return list of user identifiers
     */
    public List<Long> findAllUserIds() {
        String sql = "SELECT id FROM users";
        try (Connection c = conn();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }
            return ids;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user exists by their ID.
     * @param id user identifier
     * @return true if found
     */
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsById failed", e);
        }
    }


    /**
     * Finds a user ID based on their username.
     *
     * @param username username to search for
     * @return the user ID, or null if not found
     */
    public Long findUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("findUserIdByUsername failed", e);
        }
    }

    /**
     * Finds a username based on the user ID.
     *
     * @param id user ID
     * @return the username or "Unknown user" if not found
     */
    public String findUsernameById(long id) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in findUsernameById: " + e.getMessage());
        }
        return "Unknown user";
    }
}