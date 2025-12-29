package app.model;

import app.dto.User;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gère la logique de score, progression, combo et découpage de texte selon le mode de jeu.
 * Utilisée pendant une partie pour suivre l'état de jeu et appliquer les règles.
 */
public class Rules {
    private int indexPhrase = 0;
    private final List<String> phrases;
    private int correctLetterStreak = 0;
    private GameConfig config;

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final DoubleProperty combo = new SimpleDoubleProperty(1.0);
    /** @return propriété observable pour le score */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /** @return propriété observable pour le combo */
    public DoubleProperty comboProperty() {
        return combo;
    }

    /** @return score final calculé */
    public int getFinalScore() {
        return Math.round(scoreProperty().get() * (float) comboProperty().get());
    }
    /**
     * Initialise les règles du jeu avec la configuration et découpe le texte.
     */
    public Rules(GameConfig config) {
        this.config = config;
        this.phrases = decouperParPhrase(config.getText().content());
    }
    /** Réinitialise le score et le combo à leurs valeurs initiales. */
    public void resetStats() {
        score.set(0);
        combo.set(1.0);
    }
    /** Réinitialise la progression à la première phrase. */
    public void resetCurrentPhrase(){
       indexPhrase = 0;
    }
    /**
     * Retourne la phrase actuelle à taper.
     */
    public String getPhraseActuelle() {
        if (indexPhrase < phrases.size()) {
            return phrases.get(indexPhrase);
        }
        return null;
    }

    /**
     * Retourne la phrase actuelle à afficher à l'écran.
     * Dans le mode DEVINETTE, les accents sont masqués.
     */
    public String getPhraseToDisplay(){
        if (indexPhrase < phrases.size()) {
            if (config.getMode().equals(Mode.DEVINETTE)){
                return removeAccents(phrases.get(indexPhrase));
            }
            return phrases.get(indexPhrase);
        }
        return null;
    }
    /**
     * Met à jour le combo selon la validité du dernier caractère tapé.
     */
    public boolean updateCombo(String typed) {
        String expected = getPhraseActuelle();
        if (expected == null) return true;
        if ( isCorrectLetter(typed,typed.length()-1)){
            correctLetterStreak++;
            combo.set(Math.min(2.0, combo.get() + 0.01)); // ou -0.01 si erreur
            return true;
        }else {
            correctLetterStreak=0;
            combo.set(Math.max(0.0, combo.get() - 0.01)); // ou -0.01 si erreur
            return false;
        }
    }
    /**
     * Vérifie si la dernière lettre saisie est correcte.
     */
    public boolean isCorrectLetter(String typed,int index){
        if (index < 0 || index >= typed.length() || index >= getPhraseActuelle().length()) return false;
        return typed.charAt(index) == getPhraseActuelle().charAt(index);
    }
    /**
     * Passe à la phrase suivante si toutes les conditions sont remplies.
     */
    public boolean nextSentences(String typed){
         if (isClassicModeWithFullInput(typed)){
              if (hasNextPhrase()){
                  indexPhrase++;
                  return true;
              }
         }
        return false;
    }
    /** Vérifie si la phrase actuelle a été complètement tapée (pour les modes classiques). */
    private boolean isClassicModeWithFullInput(String typed) {
        return typed.length() == phrases.get(indexPhrase).length();
    }
    /** Vérifie s'il reste encore une phrase à jouer. */
    public boolean hasNextPhrase() {
        return indexPhrase < getTotalPhrases() - 1;
    }

    /** Met à jour le score selon le succès ou l'échec de la saisie. */
    public void updateScore(boolean success){
        int newScore = score.get() + (success ? 1 : -1);
        score.set(newScore);
    }
    /** Retourne le nombre total de phrases. */
    public int getTotalPhrases() {
        return phrases.size();
    }
    /**
     * Découpe le texte selon le mode : en mots ou en phrases/lignes.
     */
    public List<String> decouperParPhrase(String texte) {
        if (config.getMode() == Mode.MORTSUBITE) {
            return decouperParMot(texte);
        }

        List<String> phrases = extrairePhrases(texte);

        // Si on a une seule phrase ou aucune, on essaye par lignes
        if (phrases.size() <= 1) {
            phrases = extraireLignes(texte);
        }

        return phrases;
    }
    /** Sépare le texte par ponctuation (.!?). */
    private List<String> extrairePhrases(String texte) {
        return Arrays.stream(texte.split("[.!?]"))
                .map(s -> normalizeText(s.trim()))
                .filter(s -> !s.isEmpty())
                .toList();
    }
    /** Sépare le texte par lignes (retours à la ligne). */
    private List<String> extraireLignes(String texte) {
        return Arrays.stream(texte.split("\\R"))
                .map(s -> normalizeText(s.trim()))
                .filter(s -> !s.isEmpty())
                .toList();
    }
    /**
     * Découpe le texte en mots (utilisé pour MORTSUBITE).
     */
    public static List<String> decouperParMot(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return List.of();
        }

        // Split the phrase into words, normalize each word and collect them into a list
        return Arrays.asList(phrase.trim().split("\\s+"))
                .stream()
                .map(word -> normalizeText(word)) // Apply normalization
                .collect(Collectors.toList());
    }

    /** Normalise le texte en remplaçant certains caractères spéciaux. */
    public static String normalizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[‘’´`]", "'");
    }
    /** Supprime les accents d’un texte (pour le mode DEVINETTE). */
    public static String removeAccents(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
    /** Calcule la progression dans le texte. */
    public Double updateProgress() {
        return (double) indexPhrase / phrases.size();
    }

    public Mode getMode() {
        return  config.getMode();
    }
    public Difficulty  getDifficulty(){
        return config.getDifficulty();
    }
    public User getUser(){
        return this.config.getUser();
    }
    public int getTextId(){
        return config.getText().id();
    }
    public int getUserId(){
        return config.getUser().id();
    }


}
