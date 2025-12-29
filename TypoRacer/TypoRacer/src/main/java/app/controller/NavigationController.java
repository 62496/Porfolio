package app.controller;

import app.dto.User;
import app.dto.ViewWithController;
import app.service.GameConfigService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Contrôleur principal qui gère la navigation entre les vues internes du menu.
 * Toutes les vues s'affichent dans le `contentPane` du fichier Menu.fxml.
 */
public class NavigationController {
    /** Service de configuration de jeu partagé avec les vues filles */
    private GameConfigService configService;


    /** Conteneur principal dans lequel seront affichées les vues enfants */
    @FXML
    private StackPane contentPane;
    /** Label affichant dynamiquement le nom de l'utilisateur connecté */
    @FXML
    private Label Username;

    public void setUser(User user) {
        this.Username.setText(user.name());
        this.configService.setUser(user);
    }
    /**
     * Injecte le GameConfigService et initialise l'affichage du nom utilisateur.
     * Appel à chaque chargement de Menu.fxml.
     */
    public void setGameConfigService(GameConfigService service) {
        this.configService = service;
        setUser(configService.getUser());
    }

    public void initialize() {
        loadView("/Accueil.fxml");
    }

    @FXML
    private void loadAccueilView() {
        loadView("/Accueil.fxml");
    }
    /**
     * Charge la vue OCR permettant de charger un texte depuis une image ou un PDF.
     */
    @FXML
    private void loadOcrView() {
        var result = loadViewWithController("/UploadFile.fxml");
        if (result != null && result.controller() instanceof OcrController ocrController) {
            ocrController.setUserName(this.Username.getText());
        }
    }
    /**
     * Charge la vue des modes de jeu (Mode.fxml) dans le contentPane.
     */
    @FXML
    private void loadModeView() {
        var result = loadViewWithController("/Mode.fxml");
        if (result != null && result.controller() instanceof ModeController modeController) {
            modeController.setParentContainer(contentPane);
            modeController.setGameConfigService(configService);
        }
    }
    /**
     * Charge la vue du classement (Classement.fxml).
     * Affiche les scores par utilisateur et texte.
     */
    @FXML
    private void loadClassementView() {
        var result = loadViewWithController("/Classement.fxml");
        if (result != null && result.controller() instanceof ClassementController classementController) {
            classementController.setGameConfigService(configService);

        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue : " + fxmlPath);
        }
    }

    private ViewWithController loadViewWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();
            contentPane.getChildren().setAll(view);
            return new ViewWithController(view, controller);
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue : " + fxmlPath);
            return null;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
