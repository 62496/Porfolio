package app.model;

import app.dto.Texte;
import app.dto.User;
/**
 * Contient les informations de configuration d'une partie : mode, difficulté, texte et utilisateur.
 * Sert de passerelle entre les vues et la logique de jeu.
 */
public class GameConfig {
    private  Mode mode;
    private  Difficulty difficulty;
    private  Texte texte;
    private  User user;

    public GameConfig() {

    }
    public GameConfig(Mode mode, Difficulty difficulty, Texte texte, User user) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.texte = texte;
        this.user = user;
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }
    /**
     * Retourne la limite de temps en fonction de la difficulté.
     */
    public int getTimeLimit() {

        switch (difficulty) {
            case EASY -> {
                return 90;
            }
            case NORMAL -> {
                return 60;
            }
            case HARDCORE -> {
                return 30;
            }
        }
        return 0;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setTexte(Texte texte) {
        this.texte = texte;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mode getMode() {
        return mode;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Texte getText() {
        return texte;
    }

    public User getUser() {
        return new User(user.id(),user.name(), user.password());
    }
}

