package app.service;

import app.dto.Classement;
import app.dto.User;
import app.model.Difficulty;
import app.model.Mode;
import app.repository.ClassementRepository;

import java.util.List;
/**
 * Service métier responsable des opérations sur les classements (score, record, ajout).
 * Sert d’interface entre les contrôleurs et la couche repository.
 */
public class ClassementService {

    /** Accès aux données de classement via le repository */
    private final ClassementRepository repo = new ClassementRepository();

    public ClassementService() {

    }
    /**
     * Récupère les meilleurs scores pour un texte donné, selon un mode et une difficulté.
     * @param mode Le mode de jeu (CLASSIC, DEVINETTE, MORTSUBITE)
     * @param texteId L'identifiant du texte
     * @param diff Le niveau de difficulté
     * @return Liste des meilleurs scores (limite à 20 par défaut dans le DAO)
     */
    public List<Classement> getTopScores(Mode mode, int texteId, Difficulty diff) {
        return repo.getClassement(mode, texteId, diff);
    }
    /**
     * Récupère le meilleur score personnel d’un utilisateur pour un texte et une difficulté donnés.
     * @param mode Le mode concerné
     * @param userId L’identifiant utilisateur
     * @param texteId Le texte joué
     * @param difficulty La difficulté du jeu
     * @return Le score maximum enregistré ou 0 s’il n’y en a pas
     */
    public int getUserRecord(Mode mode, int userId, int texteId, Difficulty difficulty) {
        return repo.getRecord(mode, userId, texteId, difficulty);
    }
    /**
     * Enregistre un nouveau score pour un utilisateur si celui-ci est supérieur à l'ancien.
     * @param mode Mode de jeu
     * @param diff Difficulté choisie
     * @param score Score réalisé
     * @param user Utilisateur concerné
     * @param textId Identifiant du texte joué
     * @return true si l'ajout a réussi, false sinon
     */
    public void addScore(Mode mode, Difficulty diff, int score, User user, int textId) {
        repo.addScore(mode, diff, user, score, textId); // à adapter avec `User`
    }
}
