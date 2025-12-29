package app.repository;

import app.dto.Texte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * DAO responsable de l'accès aux textes depuis la base de données.
 */
public class TextDao {
    private Connection connection;

    TextDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection,"connexion requise");
    }
    /**
     * Récupère tous les textes disponibles.
     */
    List<Texte> findAll() {
        List<Texte> Textes = new ArrayList<>();
        String sql = "SELECT * FROM Textes";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String creator = rs.getString("creator");
                Texte texte = new Texte(id,title,content,creator);
                Textes.add(texte);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection  des users impossible", e);
        }
        return Textes;
    }
    /**
     * Insère un nouveau texte dans la base.
     */
    public void insert(Texte texte){
        String sql = "INSERT INTO Textes (title,content,creator) VALUES(?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, texte.title());
            stmt.setString(2, texte.content());
            stmt.setString(3, texte.creator());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Insertion du texte impossible", e);
        }
    }
    /**
     * Récupère tous les titres existants.
     */
    public List<String> getAllTitres() throws SQLException {
        List<String> titres = new ArrayList<>();
        String sql = "SELECT title FROM Textes";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                titres.add(rs.getString("title"));
            }
        }
        return titres;
    }
    public Connection getConnection() {
        return connection;
    }
}
