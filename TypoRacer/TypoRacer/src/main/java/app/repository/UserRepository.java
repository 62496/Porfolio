package app.repository;
import app.dto.User;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Couche de service responsable de la gestion des utilisateurs via le DAO {@link UserDao}.
 * Fournit des fonctionnalités d'interrogation, d'enregistrement, de suppression et de cache mémoire.
 */
public class UserRepository {
    private final UserDao userDao;
    private final Map<Integer, User> userCache;
    /**
     * Constructeur par défaut utilisant la connexion via {@link ConnectionManager}.
     * Initialise également le cache local des utilisateurs.
     */
    public UserRepository() {
        Connection connection = ConnectionManager.getConnection();
        this.userDao = new UserDao(connection);
        this.userCache = new ConcurrentHashMap<>();
        loadCache();
    }
    /**
     * Constructeur avec injection explicite de {@link UserDao}.
     * Utile pour les tests ou l'injection manuelle.
     *
     * @param userDao DAO utilisateur à utiliser
     */
    UserRepository(UserDao userDao) {
        this.userDao = Objects.requireNonNull(userDao, "UserDao is required");
        this.userCache = new ConcurrentHashMap<>();
        loadCache();
    }
    /**
     * Charge les utilisateurs depuis la base de données dans un cache local.
     * Utilisé au démarrage pour éviter des requêtes répétées.
     */
    private void loadCache() {
        userDao.findAll().forEach(
                user -> userCache.put(user.id(), user));
    }
    /**
     * Vérifie si un utilisateur existe dans la base avec les identifiants fournis.
     *
     * @param user les informations à vérifier (id, nom, mot de passe)
     * @return l'utilisateur trouvé ou {@link Optional#empty()}
     */
    public Optional<User> checkData(User user){
        return userDao.checkData(user);
    }

    /**
     * Enregistre un nouvel utilisateur dans la base et le cache.
     *
     * @param user utilisateur à enregistrer
     * @return l'identifiant généré ou -1 si l'enregistrement échoue
     */
    public int saveUser(User user) {
        int generatedId = userDao.saveUser(user);
        if (generatedId != -1) {
            userCache.put(generatedId,
                    new User(generatedId, user.name(),user.password()));
        }
        return generatedId;
    }
    /**
     * Supprime un utilisateur de la base et du cache par son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
    public void delete(int id) {
        userDao.deleteById(id);
        userCache.remove(id);
    }
    /**
     * Ferme la connexion à la base de données via {@link ConnectionManager}.
     */
    public void close() {
        ConnectionManager.close();
    }
}
