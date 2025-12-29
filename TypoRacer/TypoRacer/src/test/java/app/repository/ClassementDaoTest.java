package app.repository;

import app.model.Difficulty;
import app.model.Mode;
import app.dto.Classement;
import app.dto.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ClassementDao
 * Utilise une base de données SQLite en mémoire
 */
class ClassementDaoTest {

    private static Connection connection;
    private ClassementDao instance;

    private final User testUser = new User(1, "TestUser", "password");

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE Users (
                    Id INTEGER PRIMARY KEY,
                    Name TEXT NOT NULL,
                    Password TEXT NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE Score_Classic (
                    User INTEGER NOT NULL,
                    TexteId INTEGER NOT NULL,
                    Difficulty TEXT NOT NULL,
                    Score INTEGER,
                    PRIMARY KEY(User, TexteId, Difficulty, Score),
                    FOREIGN KEY(User) REFERENCES Users(Id)
                )
            """);

            stmt.execute("""
                CREATE TABLE Score_Devinette (
                    User INTEGER NOT NULL,
                    TexteId INTEGER NOT NULL,
                    Difficulty TEXT NOT NULL,
                    Score INTEGER,
                    PRIMARY KEY(User, TexteId, Difficulty, Score),
                    FOREIGN KEY(User) REFERENCES Users(Id)
                )
            """);

            stmt.execute("""
                CREATE TABLE Score_Mort_Subite (
                    User INTEGER NOT NULL,
                    TexteId INTEGER NOT NULL,
                    Difficulty TEXT NOT NULL,
                    Score INTEGER,
                    PRIMARY KEY(User, TexteId, Difficulty, Score),
                    FOREIGN KEY(User) REFERENCES Users(Id)
                )
            """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new ClassementDao(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Users (Id, Name, Password) VALUES (1, 'TestUser', 'password')");

            stmt.execute("""
                INSERT INTO Score_Classic (User, TexteId, Difficulty, Score)
                VALUES (1, 1, 'easy', 100)
            """);

            stmt.execute("""
                INSERT INTO Score_Devinette (User, TexteId, Difficulty, Score)
                VALUES (1, 1, 'easy', 90)
            """);

            stmt.execute("""
                INSERT INTO Score_Mort_Subite (User, TexteId, Difficulty, Score)
                VALUES (1, 1, 'easy', 95)
            """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Score_Classic");
            stmt.execute("DELETE FROM Score_Devinette");
            stmt.execute("DELETE FROM Score_Mort_Subite");
            stmt.execute("DELETE FROM Users");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    @Test
    void testGetClassementClassic() {
        Mode mode = Mode.CLASSIC;
        int texteId = 1;
        Difficulty difficulty = Difficulty.EASY;

        List<Classement> result = instance.getClassement(mode, texteId, difficulty);

        assertFalse(result.isEmpty(), "Le classement ne devrait pas être vide");
        assertEquals("TestUser", result.get(0).name(), "Le nom du joueur devrait correspondre");
        assertEquals(100, result.get(0).score(), "Le score devrait correspondre");
    }

    @Test
    void testGetClassementDevinette() {
        Mode mode = Mode.DEVINETTE;
        int texteId = 1;
        Difficulty difficulty = Difficulty.EASY;

        List<Classement> result = instance.getClassement(mode, texteId, difficulty);

        assertFalse(result.isEmpty(), "Le classement ne devrait pas être vide");
        assertEquals("TestUser", result.get(0).name(), "Le nom du joueur devrait correspondre");
        assertEquals(90, result.get(0).score(), "Le score devrait correspondre");
    }

    @Test
    void testGetRecord() {
        Mode mode = Mode.CLASSIC;
        int userId = 1;
        int texteId = 1;
        Difficulty difficulty = Difficulty.EASY;

        int result = instance.getRecord(mode, userId, texteId, difficulty);

        assertEquals(100, result, "Le record devrait correspondre");
    }

    @Test
    void testAddScore() {
        Mode mode = Mode.CLASSIC;
        Difficulty difficulty = Difficulty.EASY;
        User user = testUser;
        int score = 150;
        int texteId = 2;

        boolean result = instance.addScore(mode, difficulty, user, score, texteId);

        assertTrue(result, "L'ajout du score devrait réussir");

        int recordAfter = instance.getRecord(mode, user.id(), texteId, difficulty);
        assertEquals(150, recordAfter, "Le nouveau record devrait correspondre");
    }
}
