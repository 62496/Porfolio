package app.model;
/**
 * Classe d'authentification contenant des règles de validation simples pour les formulaires.
 */
public class Auth {
    /**
     * Vérifie que tous les champs requis pour un formulaire sont remplis.
     *
     * @param name nom d'utilisateur
     * @param id identifiant de connexion
     * @param password mot de passe
     * @return true si tous les champs sont valides
     */
    public boolean validateForm(String name, String id, String password) {
        if (name.isEmpty() ||
                id.isEmpty() ||
                password.isEmpty()) {
            return false;
        }
        return true;
    }
}
