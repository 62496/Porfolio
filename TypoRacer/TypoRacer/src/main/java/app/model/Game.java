package app.model;

import app.dto.User;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
/**
 * Classe centrale contenant la logique métier du jeu.
 * Gère l’état de la partie, le score, la progression, le texte à taper, etc.
 */
public class Game {
    private final GameConfig config;
    private Rules rules;
    /**
     * Initialise une nouvelle instance du jeu à partir de la configuration utilisateur.
     * @param config Configuration de jeu (mode, difficulté, texte, utilisateur)
     */
    public Game(GameConfig config) {
        this.config = config;
        this.rules = new Rules(this.config);
    }
    /**
     * Redémarre une partie à zéro.
     */
    public void restart() {
        rules.resetStats();
        rules.resetCurrentPhrase();
    }
    /**
     * Traite la saisie utilisateur et met à jour le score si le mot est correctement saisi.
     * @param typed La saisie en cours
     * @return true si la saisie est correcte jusqu'à présent
     */
    public boolean typing(String typed){

        typed = rules.normalizeText(typed);
        if (rules.updateCombo(typed)){
            rules.updateScore(true);
            return true;
        }else {
            rules.updateScore(false);
        };
        return false;
    }
    /**
     * Passe à la phrase suivante si elle existe.
     * @param typed La phrase que l’utilisateur vient de taper
     * @return true si une autre phrase est disponible
     */
    public boolean nextSentences(String typed) {
        return rules.nextSentences(typed);
    }

    /**
     * Détermine la durée maximale d'une partie selon la difficulté.
     * @return Durée en secondes
     */
    public int getMaxTime() {
        return config.getTimeLimit();
    }



    /**
     * Calcule l'avancement de la partie (progression de l'utilisateur).
     * @return Une valeur entre 0 et 1 représentant l'avancement
     */
    public Double getProgress(){
        return rules.updateProgress();
    }

    /**
     * Récupère la phrase actuelle à taper.
     * @return La phrase courante
     */
    public String getPhraseActuelle() {
        return rules.getPhraseActuelle();
    }

    /**
     * Renvoie une version formatée de la phrase courante à afficher dans l'interface.
     * @return La phrase affichable selon le mode
     */
    public String getPhraseToDisplay(){
        return rules.getPhraseToDisplay();
    }
    /**
     * Renvoie le score final calculé à la fin de la partie.
     * @return Score final
     */
    public int getFinalScore() {
        return rules.getFinalScore();
    }
    public Mode getMode(){
        return rules.getMode();
    }
    public Difficulty  getDifficulty(){
        return rules.getDifficulty();
    }
    public User getUser(){
        return rules.getUser();
    }
    public int getTextId(){
        return rules.getTextId();
    }

    /** @return propriété JavaFX du score pour le binding UI */
    public IntegerProperty scoreProperty() {
        return rules.scoreProperty();
    }

    /** @return propriété JavaFX du combo pour le binding UI */
    public DoubleProperty comboProperty() {
        return rules.comboProperty();
    }

}


