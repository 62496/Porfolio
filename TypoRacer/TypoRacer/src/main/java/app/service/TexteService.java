package app.service;

import app.dto.Texte;
import app.repository.TextRepository;

import java.sql.SQLException;
import java.util.List;
/**
 * Service métier chargé de la gestion des textes à utiliser pour les parties.
 * Sert d’interface entre les contrôleurs et la couche repository.
 */
public class TexteService {

    /** Accès aux textes via un repository (couche DAO) */
    private final TextRepository textRepository = new TextRepository();

    public TexteService() {
    }
    /**
     * Récupère l’ensemble des textes disponibles.
     * @return Liste des textes avec leur ID, titre, et contenu
     */
    public List<Texte> getTextes () throws SQLException {
       return textRepository.getTextes();
    }
    /**
     * Vérifie si un titre de texte est valide pour l'ajout :
     * - il ne doit pas être vide
     * - il ne doit pas déjà exister dans la base
     *
     * @param title Le titre à vérifier
     * @return true si le titre est unique et non vide, false sinon
     * @throws SQLException si une erreur survient lors de l'accès aux titres en base
     */
    public boolean isValidTitle(String title) throws SQLException {
        List<String> titles = textRepository.getAllTitres();
        return !title.isEmpty() && !titles.contains(title);
    }
    /**
     * Vérifie si le contenu du texte est suffisant pour être utilisé dans le jeu.
     *
     * @param texte Le contenu du texte à valider
     * @return true si le texte contient au moins 100 caractères, false sinon
     */
    public boolean isValidText(String texte) {
        if (texte.length() < 100) {
            return false;
        }
        return true;
    }
    /**
     * Enregistre un nouveau texte dans la base de données.
     *
     * @param texte Objet texte à sauvegarder
     * @throws SQLException si l'insertion échoue
     */
    public void saveTexte(Texte texte) throws SQLException {
        textRepository.addTexte(texte);
    }
}
