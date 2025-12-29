// UserService.java
package app.service;

import app.dto.User;
import app.model.Auth;
import app.repository.UserRepository;

import java.util.Optional;
/**
 * Service métier chargé de la gestion des utilisateurs :
 * - Connexion
 * - Inscription
 * - Validation des identifiants
 */
public class UserService {

    /** Accès aux opérations de persistance des utilisateurs */
    private final UserRepository repository;
    private final Auth auth;

    public UserService(UserRepository repository, Auth auth) {
        this.repository = repository;
        this.auth = auth;
    }

    public boolean validateFields(String name, String id, String password) {
        return auth.validateForm(name,id,password);
    }
    /**
     * Tente de connecter un utilisateur avec les identifiants fournis.
     * @param name Nom d'utilisateur
     * @param id Identifiant utilisateur (sous forme de chaîne)
     * @param password Mot de passe
     * @return Un Optional contenant l'utilisateur s'il est trouvé et valide, sinon vide
     */
    public Optional<User> login(String name, String id, String password) {
        if (!validateFields(name, id, password)) return Optional.empty();
        return repository.checkData(new User(Integer.parseInt(id), name, password));
    }
    /**
     * Inscrit un nouvel utilisateur dans le système.
     * @param name Nom choisi
     * @param id Identifiant utilisateur (sous forme texte)
     * @param password Mot de passe
     * @return L’ID enregistré si succès, ou -1 si erreur
     */
    public int register(String name, String id, String password) {
        if (!validateFields(name, id, password)) return -1;
        return repository.saveUser(new User(Integer.parseInt(id), name, password));
    }
}
