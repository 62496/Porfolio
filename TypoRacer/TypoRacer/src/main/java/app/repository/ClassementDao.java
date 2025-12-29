package app.repository;

import app.dto.Classement;
import app.dto.User;
import app.model.Difficulty;
import app.model.Mode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * DAO (Data Access Object) pour gérer l'accès aux classements depuis la base de données.
 */
public class ClassementDao {
    private final Connection connection;

    public ClassementDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Récupère le classement des meilleurs scores pour un texte et une difficulté donnés.
     */
    public List<Classement> getClassement(Mode mode, int texteId, Difficulty difficulty) {
        List<Classement> classement = new ArrayList<>();
        String tableName;

        // Déterminer la table en fonction du mode
        switch (mode) {

            case CLASSIC -> tableName = "Score_Classic";
            case DEVINETTE -> tableName = "Score_Devinette";
            case MORTSUBITE -> tableName = "Score_Mort_Subite";

            default -> throw new IllegalArgumentException("Mode non supporté");
        }

        String sql = """
        SELECT u.Name, s.Score AS score
        FROM %s s
        JOIN Users u ON u.Id = s.User
        WHERE s.TexteId = ? AND s.Difficulty = ?
        ORDER BY s.Score DESC
        LIMIT 20
        """.formatted(tableName);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, texteId);
            stmt.setString(2, difficulty.name().toLowerCase()); // 'easy', 'normale', etc.
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int score = rs.getInt("score");
                    String name = rs.getString("Name");
                    classement.add(new Classement(score, name));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Sélection du classement impossible", e);
        }

        return classement;
    }
    /**
     * Récupère le meilleur score (record) d'un utilisateur pour un texte et une difficulté.
     */
    public int getRecord(Mode mode, int id, int texteId, Difficulty difficulty) {
        String tableName;

        // Déterminer la table en fonction du mode
        switch (mode) {
            case CLASSIC -> tableName = "Score_Classic";
            case DEVINETTE -> tableName = "Score_Devinette";
            case MORTSUBITE -> tableName = "Score_Mort_Subite";

            default -> throw new IllegalArgumentException("Mode non supporté");
        }

        String sql = """
SELECT MAX(s.Score) AS score
FROM %s s
WHERE s.User = ? AND s.TexteId = ? AND s.Difficulty = ?
""".formatted(tableName);


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, texteId);
            stmt.setString(3, difficulty.name().toLowerCase()); // "easy", "normale", "hardcore"

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Sélection du record impossible", e);
        }

        return 0; // Aucun score trouvé
    }

    /**
     * Ajoute un nouveau score à la base de données.
     */
    public boolean addScore(Mode mode, Difficulty difficulty, User user, int score, int textId) {
        String tableName;

        switch (mode) {
            case CLASSIC -> tableName = "Score_Classic";
            case DEVINETTE -> tableName = "Score_Devinette";
            case MORTSUBITE -> tableName = "Score_Mort_Subite";

            default -> throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        String sql = "INSERT INTO " + tableName + " (User, TexteId, Difficulty, Score) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.id());
            stmt.setInt(2, textId);
            stmt.setString(3, difficulty.name().toLowerCase()); // Exemple : "easy", "normal", "hardcore"
            stmt.setInt(4, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Insertion du score impossible", e);
        }

        return true;
    }

}