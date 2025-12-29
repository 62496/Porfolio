package app.service;

import app.model.GameConfig;
import app.model.Mode;
import app.model.Difficulty;
import app.dto.User;
import app.dto.Texte;
/**
 * Service centralisé permettant de stocker et partager la configuration actuelle du jeu.
 * Utilisé entre les différentes vues (mode, texte, jeu, etc.)
 */
public class GameConfigService {

    /** Objet de configuration contenant le mode, difficulté, texte et utilisateur */
    private final GameConfig config = new GameConfig();

    public void setUser(User user) {
        config.setUser(user);
    }

    public void setMode(Mode mode) {
        config.setMode(mode);
    }

    public void setDifficulty(Difficulty difficulty) {
        config.setDifficulty(difficulty);
    }

    public void setTexte(Texte texte) {
        config.setTexte(texte);
    }

    /**
     * Vérifie si le mode et la difficulté sont définis.
     * @return true si les deux sont présents
     */
    public boolean modeIsReady(){
        return  (config.getMode()!=null && config.getDifficulty()!=null);
    }

    /**
     * Vérifie si toutes les informations nécessaires au jeu sont prêtes.
     * @return true si le mode, la difficulté et le texte sont définis
     */
    public boolean texteIsReady(){
        return  (config.getMode()!=null && config.getDifficulty()!=null && config.getText() !=null);
    }
    public Mode getMode(){
        return config.getMode();
    }
    public Difficulty getDifficulty(){
        return config.getDifficulty();
    }
    public User getUser(){
        return config.getUser();
    }
    public  Texte getText(){
        return  config.getText();
    }

}