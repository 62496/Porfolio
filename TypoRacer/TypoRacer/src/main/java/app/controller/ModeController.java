package app.controller;

import app.model.Difficulty;
import app.model.Mode;
import app.service.GameConfigService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Contrôleur responsable de la sélection du mode de jeu et de la difficulté.
 * Gère l'affichage dynamique et l'accès à la vue de sélection de texte.
 */
public class ModeController {

    /** Conteneur principal dans lequel sera injectée la prochaine vue */
    private StackPane parentContainer;

    /** Service de configuration du jeu (partagé entre les vues) */
    private GameConfigService configService;

    // === Composants FXML ===
    @FXML private Button classiqueBtn, mortSubiteBtn, devinetteBtn; // Boutons des modes de jeu
    @FXML private ChoiceBox<Difficulty> difficultyChoiceBox;       // Choix de la difficulté
    @FXML private Label modeDescriptionLabel;                       // Description du mode sélectionné

    /**
     * Méthode appelée automatiquement après le chargement de la vue.
     * Initialise les éléments de l'interface.
     */
    @FXML
    public void initialize() {
        difficultyChoiceBox.getItems().addAll(EnumSet.allOf(Difficulty.class));
        difficultyChoiceBox.setOnAction(this::handleDifficultyChange);
        updateSelectionStyles(); // Mise à jour visuelle initiale
    }

    /** Setter appelé depuis l'extérieur pour injecter le GameConfigService */
    public void setGameConfigService(GameConfigService service) {
        this.configService = service;
    }

    /** Setter pour définir dynamiquement le parent container */
    public void setParentContainer(StackPane parentContainer) {
        this.parentContainer = parentContainer;
    }

    /** Gestion du changement de difficulté */
    private void handleDifficultyChange(ActionEvent event) {
        configService.setDifficulty(difficultyChoiceBox.getValue());
        updateSelectionStyles();
    }

    // === Gestion des boutons de mode ===

    @FXML
    private void handleClassique() {
        configService.setMode(Mode.CLASSIC);
        modeDescriptionLabel.setText("Mode Classique : Tape le texte à ton rythme avec possibilité d’erreurs.");
        updateSelectionStyles();
    }

    @FXML
    private void handleMortSubite() {
        configService.setMode(Mode.MORTSUBITE);
        modeDescriptionLabel.setText("Mort Subite : Une seule erreur et c’est fini ! Réflexes et précision sont essentiels.");
        updateSelectionStyles();
    }

    @FXML
    private void handleDevinette() {
        configService.setMode(Mode.DEVINETTE);
        modeDescriptionLabel.setText("Devinette : Des accents ont disparu ! Retrouve-les en complétant le texte correctement.");
        updateSelectionStyles();
    }

    /**
     * Redirige vers la vue de choix du texte une fois un mode et une difficulté choisis.
     */
    @FXML
    private void redirectToChoiceText() throws IOException {
        if (configService.modeIsReady()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TextChoice.fxml"));
            VBox view = loader.load();
            TextChoiceController controller = loader.getController();
            controller.setGameConfigService(configService);
            parentContainer.getChildren().setAll(view);
        } else {
            showError("Veuillez sélectionner un mode et une difficulté avant de continuer.");
        }
    }

    // === Style dynamique ===

    private void updateSelectionStyles() {
        removeStyle(classiqueBtn, "selected-mode");
        removeStyle(mortSubiteBtn, "selected-mode");
        removeStyle(devinetteBtn, "selected-mode");

        if (configService != null && configService.getMode() != null) {
            addStyle(getButtonByMode(configService.getMode()), "selected-mode");
        }

        difficultyChoiceBox.getStyleClass().remove("selected-difficulty");
        if (difficultyChoiceBox.getValue() != null) {
            difficultyChoiceBox.getStyleClass().add("selected-difficulty");
        }
    }

    private void removeStyle(Button btn, String style) {
        btn.getStyleClass().removeIf(s -> s.equals(style));
    }

    private void addStyle(Button btn, String style) {
        if (!btn.getStyleClass().contains(style)) {
            btn.getStyleClass().add(style);
        }
    }

    private Button getButtonByMode(Mode mode) {
        return switch (mode) {
            case CLASSIC -> classiqueBtn;
            case MORTSUBITE -> mortSubiteBtn;
            case DEVINETTE -> devinetteBtn;
        };
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Sélection incomplète");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
