package app.controller;

import app.dto.User;
import app.model.Difficulty;
import app.model.Game;
import app.model.GameConfig;
import app.model.Mode;
import app.service.ClassementService;
import app.service.GameConfigService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Contrôleur de la vue Game.fxml.
 * Gère toute la logique de l’interface du jeu en cours :
 * - Affichage du texte
 * - Saisie utilisateur
 * - Gestion du score, combo, progression et timer
 */
public class GameController {
    private Game game;
    private GameConfigService configService;
    private GameConfig config;
    private ClassementService classementService = new ClassementService();
    private final StringBuilder typedSoFar = new StringBuilder();
    private boolean isFlashing = false;
    private Thread timerThread;
    private int timeLeft;

    @FXML
    Label score;
    @FXML
    Label combo;
    @FXML private Label timerLabel;
    @FXML
    ProgressBar progressBar;
    @FXML
    ProgressBar timeBar;
    @FXML
    TextFlow textDisplay;
    @FXML
    TextField input;

    /**
     * Initialise le jeu avec la configuration fournie.
     * Appelé dès que la vue est chargée.
     */
    public void initGame(GameConfigService configService) {
        this.configService =configService;
        this.config = new GameConfig(configService.getMode(),configService.getDifficulty(),configService.getText(),configService.getUser());
        this.game = new Game(config);
        score.textProperty().bind(game.scoreProperty().asString());
        combo.textProperty().bind(game.comboProperty().asString("x%.2f"));
        resetGameUI();
        setupInputHandlers();
    }
    /** Gère les événements de saisie utilisateur */
    private void setupInputHandlers() {
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                event.consume();
                handleBackspace();
            }
        });

        input.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > oldVal.length()) {
                if (game.typing(newVal)) {
                    handleValidTyping(newVal);
                } else {
                    handleInvalidTyping();
                    updateTextColor();
                }
                input.setText(typedSoFar.toString());
                input.positionCaret(typedSoFar.length());
                updateTextColor();
                updateUI();
            } else {
                input.setText(typedSoFar.toString());
                input.positionCaret(typedSoFar.length());
                updateTextColor();
                updateUI();
            }
        });
    }

    private void handleBackspace() {
        if (typedSoFar.length() > 0) {
            typedSoFar.deleteCharAt(typedSoFar.length() - 1);
            input.setText(typedSoFar.toString());
            input.positionCaret(typedSoFar.length());
            updateTextColor();
        }
    }
    private void handleValidTyping(String newValue) {
        typedSoFar.append(newValue.charAt(newValue.length() - 1));
        if (typedSoFar.length() == game.getPhraseActuelle().length()) {
            nextSentence(input.getText());
        }
    }

    private void handleInvalidTyping() {
        if (!isLastCharValid()) return;
        if (game.getMode().equals(Mode.MORTSUBITE)) {
            loseGame();
        } else {
            flashError(input);
        }
    }

    private boolean isLastCharValid() {
        int index = typedSoFar.length();
        String expected = game.getPhraseActuelle();
        if (index == 0 || index > expected.length()) return true;
        return typedSoFar.charAt(index - 1) == expected.charAt(index - 1);
    }

    private void flashError(TextField field) {
        if (isFlashing) return;
        isFlashing = true;
        String original = field.getStyle();
        field.setStyle("-fx-background-color: #ffcccc;");
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                field.setStyle(original);
                isFlashing = false;
            });
        }).start();
    }

    private void resetGameUI() {
        updateUI();
        displayCurrentPhrase();
        startTimer();
    }

    private void displayCurrentPhrase() {
        textDisplay.getChildren().clear();
        textDisplay.setTextAlignment(TextAlignment.CENTER);
        String phrase = game.getPhraseToDisplay();
        for (char c : phrase.toCharArray()) {
            Text t = new Text(String.valueOf(c));
            t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20)); // Taille + lisibilité
            t.setFill(Color.WHITE); // Couleur claire sur fond sombre
            textDisplay.getChildren().add(t);
        }
    }

    private void updateUI() {
        progressBar.setProgress(game.getProgress());
    }

    private void updateTextColor() {
        String typed = input.getText();
        for (int i = 0; i < typed.length() ; i++) {
            Text t = (Text) textDisplay.getChildren().get(i);
            if (i < typed.length()) {
                if (typed.charAt(i) == game.getPhraseActuelle().charAt(i)) {
                    t.setStyle("-fx-fill: green;");
                } else {
                    t.setStyle("-fx-fill: red;");
                }
            } else {
                t.setStyle("-fx-fill: black;");
            }
        }
    }
    private void nextSentence(String typed) {
        if (game.nextSentences(typed)) {
            typedSoFar.setLength(0);
            displayCurrentPhrase();
            updateUI();
        } else {
            winGame();
        }
    }
    private void loseGame() {
        typedSoFar.setLength(0);
        stopTimer();
        handleGameEnd("Vous avez perdu");
    }

    private void winGame() {
        stopTimer();
        handleGameEnd("Vous avez gagné");
    }

    private void endTime() {
        stopTimer();
        handleGameEnd("Temps écoulé !");
    }
    /**
     * Affiche le score final + enregistre s'il s'agit d'un nouveau record.
     */
    private void handleGameEnd(String messagePrefix) {
        int finalScore = game.getFinalScore();
        String baseMessage = messagePrefix + " Score = " + finalScore;

        if (finalScore > 0) {
            boolean newRecord = isRecord();
            if (newRecord) {
                baseMessage = messagePrefix + ". Nouveau record personnel ! Score = " + finalScore;
            }
            showEndGameAlert(baseMessage);
            addScore(configService.getMode(), configService.getDifficulty(), finalScore, configService.getUser(), game.getTextId());
        } else {
            showEndGameAlert(baseMessage);
        }
    }
    /** Lance le timer principal en arrière-plan */

    private void startTimer() {
        int maxTime = game.getMaxTime();
        timeLeft = maxTime;
        timeBar.setProgress(1.0);
        timerLabel.setText(timeLeft + "s");

        timerThread = new Thread(() -> {
            while (timeLeft > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                timeLeft--;
                Platform.runLater(() -> {
                    timerLabel.setText(timeLeft + "s");
                    timeBar.setProgress((double) timeLeft / maxTime);
                });
            }
            Platform.runLater(() -> {
                endTime();
            });
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }


    private void stopTimer() {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }

    private void showEndGameAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partie");
        alert.setHeaderText(message);
        alert.setContentText("Que voulez-vous faire ?");

        ButtonType retry = new ButtonType("Recommencer");
        ButtonType menu = new ButtonType("Menu principal");
        alert.getButtonTypes().setAll(retry, menu);
        alert.show();

        alert.setOnHidden(e -> {
            if (alert.getResult() == retry) {
                restartGame();
            } else {
                goToMenu();
            }
        });
    }
    private void addScore(Mode mode, Difficulty difficulty, int finalScore , User user,int textid){
        classementService.addScore(mode, difficulty, finalScore, user, textid
        );
    }

    private boolean isRecord() {
        int record = classementService.getUserRecord(
                configService.getMode(),
                configService.getUser().id(),
                configService.getText().id(),
                configService.getDifficulty()
        );
        return record < game.getFinalScore();
    }

    private void restartGame() {
        typedSoFar.setLength(0);
        input.clear();
        input.positionCaret(0);
        isFlashing = false;
        stopTimer();
        game.restart();
        displayCurrentPhrase();
        updateUI();
        startTimer();
    }
    @FXML
    private void handleForfait() {
        stopTimer();
        showEndGameAlert("Vous avez déclaré forfait !");
    }

    private void goToMenu() {
        try {
            Stage stage = (Stage) progressBar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
            Parent menuRoot = loader.load();

            NavigationController navigationController = loader.getController();
            configService.setUser(config.getUser());
            navigationController.setGameConfigService(configService);


            stage.getScene().setRoot(menuRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}