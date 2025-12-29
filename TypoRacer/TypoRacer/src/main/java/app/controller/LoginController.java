package app.controller;

import app.dto.User;
import app.repository.UserRepository;
import app.service.GameConfigService;
import app.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * Contrôleur de la vue Login.fxml.
 * Gère la connexion et l'inscription d’un utilisateur.
 * Transmet les données utilisateur et de configuration aux autres vues.
 */
public class LoginController {
    private UserService userService;
    private GameConfigService configService;

    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;

    /**
     * Injecte le service utilisateur à utiliser (appelé depuis Main).
     * @param service instance de UserService
     */
    public void setUserService(UserService service) {
        this.userService = service;
    }
    /**
     * Injecte le GameConfigService partagé entre les vues.
     * @param service instance de GameConfigService
     */
    public void setGameConfigService(GameConfigService service) {
        this.configService = service;
    }

    /**
     * Gère la tentative de connexion utilisateur.
     * Vérifie les identifiants saisis et redirige vers le menu en cas de succès.
     */
    @FXML
    private void handleConnexion(ActionEvent event) {
        String id = idField.getText();
        String name = nameField.getText();
        String password = passwordField.getText();

        if (userService.login(name, id, password).isPresent()) {
            redirectToMenu(event);
        } else {
            showAlert("Connexion échouée", "Veuillez vérifier vos identifiants.");
        }
    }
    /**
     * Gère la tentative d'inscription d’un nouvel utilisateur.
     * Redirige vers le menu si l'inscription réussit.
     */
    @FXML
    private void handleInscription(ActionEvent event) {
        String id = idField.getText();
        String name = nameField.getText();
        String password = passwordField.getText();

        int result = userService.register(name, id, password);
        if (result != -1) {
            redirectToMenu(event);
        } else {
            showAlert("Erreur", "Inscription échouée.");
        }
    }
    /**
     * Redirige vers la vue Menu après une connexion ou inscription réussie.
     * Transmet également l’utilisateur connecté via GameConfigService.
     */
    private void redirectToMenu(ActionEvent event) {
        try {
            Stage stage = (Stage) idField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
            Parent root = loader.load();
            NavigationController navigationController = loader.getController();
            configService.setUser(new User(Integer.parseInt(idField.getText()),nameField.getText(),
                    passwordField.getText()));
            navigationController.setGameConfigService(configService);
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger le menu");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}