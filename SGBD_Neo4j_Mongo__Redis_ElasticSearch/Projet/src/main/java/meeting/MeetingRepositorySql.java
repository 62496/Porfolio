package meeting;

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
 * SQL repository for meeting persistence.
 *
 * This class handles relational storage of meetings using MySQL.
 */
public final class MeetingRepositorySql {

    /**
     * The JDBC connection URL.
     */
    private final String url;
    
    /**
     * The database username.
     */
    private final String user;
    
    /**
     * The database password.
     */
    private final String password;

    /**
     * Constructor.
     *
     * @param cfg database configuration
     */
    public MeetingRepositorySql(DbConfig cfg) {
        this.url = cfg.mysqlUrl();
        this.user = cfg.mysqlUser();
        this.password = cfg.mysqlPassword();
    }

    /**
     * Establishes a connection to the SQL database.
     * * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    private Connection conn() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Creates a new meeting and returns its generated identifier.
     *
     * @param userA first user identifier
     * @param userB second user identifier
     * @param interest meeting interest
     * @return meeting identifier
     */
    public long createMeeting(long userA, long userB, String interest) {
        String sql = """
            INSERT INTO meetings(user_a, user_b, interest)
            VALUES (?, ?, ?)
        """;

        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, userA);
            ps.setLong(2, userB);
            ps.setString(3, interest);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            System.err.println("SQL ERROR (createMeeting): " + e.getMessage());
            throw new RuntimeException("createMeeting failed", e);
        }
    }

    /**
     * Retrieves all meetings involving a given user.
     *
     * @param userId user identifier
     * @return list of textual meeting descriptions
     */
    public List<String> findMeetingsForUser(long userId) {
        String sql = """
        SELECT m.id, m.interest, m.created_at,
               u1.username AS userA,
               u2.username AS userB
        FROM meetings m
        JOIN users u1 ON m.user_a = u1.id
        JOIN users u2 ON m.user_b = u2.id
        WHERE m.user_a = ? OR m.user_b = ?
        ORDER BY m.created_at DESC
        """;

        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<String> res = new ArrayList<>();
                while (rs.next()) {
                    res.add(
                            "Meeting #" + rs.getLong("id") +
                                    " | " + rs.getString("userA") +
                                    " - " + rs.getString("userB") +
                                    " | Interest: " + rs.getString("interest") +
                                    " | " + rs.getTimestamp("created_at")
                    );
                }
                return res;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes all meeting data.
     * Used for demo reset.
     */
    public void resetDemo() {
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.execute("TRUNCATE TABLE meetings");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}