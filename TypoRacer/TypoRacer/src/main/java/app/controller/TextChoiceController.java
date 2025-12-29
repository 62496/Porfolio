package app.controller;

import app.dto.Texte;
import app.repository.TextRepository;
import app.service.GameConfigService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur de la vue TextChoice.fxml.
 * Permet à l'utilisateur de choisir un texte avant de commencer une partie.
 */
public class TextChoiceController {
    TextRepository textRepository = new TextRepository();
    private GameConfigService configService;
    /** Table affichant les textes disponibles */
    @FXML
    private TableView<Texte> tableTextes;

    /** Colonne affichant les titres des textes */
    @FXML
    private TableColumn<Texte, String> colTitre;

    @FXML
    private TableColumn<Texte, String> colCreateur;
    @FXML
    private Button btnVoirTexte;
    @FXML
    private Button btnStart;
    /**
     * Initialise les composants de la table et charge les données au lancement de la vue.
     */
    public void initialize() throws SQLException {
        colTitre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().title()));

        colCreateur.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().creator()));
       List <Texte> Textes = textRepository.getTextes();
       tableTextes.getItems().addAll(Textes);

        tableTextes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        configService.setTexte(newSelection);
                        btnVoirTexte.setDisable(false);
                        btnStart.setDisable(false);
                    }
                });
        btnStart.setOnAction(event -> {
            try {
                startGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void startGame() throws IOException {
        try {
            validateGameConfig();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Game.fxml"));
            Parent gameView = loader.load();
            GameController gameController = loader.getController();
            gameController.initGame(configService);
            tableTextes.getScene().setRoot(gameView);
        }catch (IllegalStateException e){
            showErrorAlert("Configuration incomplète", e.getMessage());
        }
    }
    private void validateGameConfig() throws IllegalStateException {
        if (!configService.texteIsReady()) {
            throw new IllegalStateException("Mode, difficulté ou texte non sélectionné");
        }
    }
    public void setGameConfigService(GameConfigService service) {
        this.configService = service;
    }
    public void ShowTexte(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(configService.getText().title());
        alert.setHeaderText(null);
        alert.setContentText(configService.getText().content());
        alert.showAndWait();
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
