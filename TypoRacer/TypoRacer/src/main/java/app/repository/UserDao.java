package app.repository;


import app.dto.User;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
/**
 * DAO permettant l'accès aux utilisateurs dans la base de données.
 * Fournit des méthodes de recherche, d'insertion et de vérification.
 */
public class UserDao {
    private Connection connection;
    private DateTimeFormatter formatter;

     UserDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection,"connexion requise");
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    /**
     * Vérifie l'existence d'un utilisateur avec les informations données (login).
     *
     * @param user un objet User contenant les identifiants à vérifier
     * @return un Optional contenant l'utilisateur s'il existe, sinon Optional.empty()
     */
    Optional<User> checkData(User user){
        String sql = """
                SELECT 
                    * 
                FROM 
                    users 
                WHERE 
                    id = ? and 
                    name = ? and 
                    password = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.id());
            stmt.setString(2, user.name());
            stmt.setString(3, user.password());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(user.id(), user.name(),user.password()));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection des usersData impossible", e);
        }
        return Optional.empty();
    }
    /**
     * Récupère tous les utilisateurs présents dans la base.
     *
     * @return une liste d'utilisateurs
     */
     List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                User user = new User(id, name,password);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection  des users impossible", e);
        }
        return users;
    }

    /**
     * Insère un nouvel utilisateur dans la base.
     *
     * @param user l'utilisateur à sauvegarder
     * @return l'identifiant généré, ou -1 en cas d'échec
     */
     int saveUser(User user) {
        String sql = """
                INSERT INTO 
                    users (id,name,password) 
                VALUES 
                    (?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            System.out.println(user.id()+user.name()+user.password());
            stmt.setInt(1, user.id());
            stmt.setString(2, user.name());
            stmt.setString(3, user.password());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("insertion du user réussi");
                    return rs.getInt(1); // Retourne l'ID généré
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Insertion impossible", e);
        }
        return -1;
    }
    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
     void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Suppression impossible", e);
        }
    }
}
