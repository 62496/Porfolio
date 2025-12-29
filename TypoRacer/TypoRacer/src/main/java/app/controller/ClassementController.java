// ClassementController.java (refactoré)
package app.controller;

import app.dto.Classement;
import app.dto.Texte;
import app.model.Difficulty;
import app.model.Mode;
import app.service.ClassementService;
import app.service.GameConfigService;
import app.service.TexteService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
/**
 * Contrôleur de la vue Classement.fxml.
 * Gère l'affichage des meilleurs scores par mode, difficulté et texte.
 */
public class ClassementController {
    /** Choix du texte concerné (par titre) */
    @FXML private ChoiceBox<Texte> texteChoiceBox;
    /** Choix du mode de jeu (Classic, Devinette, Mort Subite) */
    @FXML private ChoiceBox<Mode> modeChoiceBox;
    @FXML private ChoiceBox<Difficulty> difficultyChoiceBox;
    /** TableView affichant les lignes de classement (nom + score) */
    @FXML private TableView<Classement> classementTable;
    @FXML private TableColumn<Classement, Number> colPlace;
    /** Colonne affichant les noms d'utilisateurs */
    @FXML private TableColumn<Classement, String> colJoueur;
    /** Colonne affichant les scores */
    @FXML private TableColumn<Classement, Integer> colScore;

    /** Service contenant les infos utilisateur + config de partie */
    private ClassementService classementService = new ClassementService();

    /** Service pour charger la liste des textes disponibles */
    private TexteService texteService = new TexteService();
    private GameConfigService configService;

    /**
     * Méthode appelée automatiquement au chargement de la vue.
     * Initialise les composants.
     */
    public void initialize() throws SQLException {
        setupTableColumns();
        setupChoiceBoxes();
        setupRowHighlighting();
        setupListeners();
    }

    private void setupTableColumns() {
        colPlace.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(classementTable.getItems().indexOf(cellData.getValue()) + 1));
        colJoueur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        colScore.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().score()).asObject());
        colPlace.setSortable(false);
        colJoueur.setSortable(false);
        colScore.setSortable(false);
    }

    private void setupChoiceBoxes() throws SQLException {
        texteChoiceBox.getItems().addAll(texteService.getTextes());
        modeChoiceBox.getItems().addAll(Mode.values());
        difficultyChoiceBox.getItems().addAll(Difficulty.values());
    }
    /**
     * Applique un style visuel aux lignes pour un meilleur contraste.
     */
    private void setupRowHighlighting() {
        classementTable.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(Classement item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty || configService == null) {
                    setStyle("");
                } else if (item.name().equals(configService.getUser().name())) {
                    setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: black; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setupListeners() {
        texteChoiceBox.setOnAction(e -> refreshClassement());
        modeChoiceBox.setOnAction(e -> refreshClassement());
        difficultyChoiceBox.setOnAction(e -> refreshClassement());
    }

    private void refreshClassement() {
        Texte texte = texteChoiceBox.getValue();
        Mode mode = modeChoiceBox.getValue();
        Difficulty difficulty = difficultyChoiceBox.getValue();

        if (texte == null || mode == null || difficulty == null) {
            return;
        }

        List<Classement> top20 = classementService.getTopScores(mode, texte.id(), difficulty);
        classementTable.getItems().setAll(top20);
    }
    /**
     * Injection du GameConfigService (depuis NavigationController).
     */
    public void setGameConfigService(GameConfigService service) {
        this.configService = service;
    }

    @FXML
    private void handleUserScore() {
        Texte texte = texteChoiceBox.getValue();
        Mode mode = modeChoiceBox.getValue();
        Difficulty difficulty = difficultyChoiceBox.getValue();

        if (texte == null || mode == null || difficulty == null ) {
            showAlert("Veuillez sélectionner un texte, un mode et une difficulté.");
            return;
        }

        int record = classementService.getUserRecord(mode, configService.getUser().id(), texte.id(), difficulty);
        showAlert("Votre meilleur score : " + record);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mon Score");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}