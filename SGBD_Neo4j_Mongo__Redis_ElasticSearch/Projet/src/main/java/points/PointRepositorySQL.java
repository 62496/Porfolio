package points;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.DbConfig;

/**
 * SQL repository for sociability points.
 *
 * This class handles the persistence of points attributed to users
 * after meetings. Each entry represents a point transaction linked
 * to a specific meeting.
 */
public final class PointRepositorySQL {

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
    public PointRepositorySQL(DbConfig cfg) {
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
     * Inserts a new point transaction for a given user and meeting.
     *
     * @param userId identifier of the user
     * @param meetingId identifier of the meeting
     * @param amount number of points granted
     */
    public void addPoints(long userId, long meetingId, int amount) {
        String sql = """
            INSERT INTO points(user_id, meeting_id, amount)
            VALUES (?, ?, ?)
        """;

        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, meetingId);
            ps.setInt(3, amount);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("addPoints failed", e);
        }
    }

    /**
     * Clears all point data from the database.
     * Disables foreign key checks to allow truncation.
     */
    public void reset() {
        try (Connection c = conn(); Statement st = c.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            st.execute("TRUNCATE TABLE points");
            st.execute("SET FOREIGN_KEY_CHECKS=1");
        } catch (SQLException e) {
            throw new RuntimeException("resetDemo failed", e);
        }
    }

    /**
     * Computes the total number of points for a given user.
     *
     * @param userId identifier of the user
     * @return total amount of points
     */
    public int getTotalPoints(long userId) {
        String sql = """
            SELECT COALESCE(SUM(amount), 0)
            FROM points
            WHERE user_id = ?
        """;

        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("getTotalPoints failed", e);
        }
        return 0;
    }
}