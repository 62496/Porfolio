package app;

import app.controller.LoginController;
import app.model.Auth;
import app.repository.UserRepository;
import app.service.GameConfigService;
import app.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Auth auth = new Auth();
        UserRepository repo = new UserRepository();
        UserService service = new UserService(repo, auth);
        GameConfigService gameConfigService = new GameConfigService();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        VBox root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setUserService(service);
        loginController.setGameConfigService(gameConfigService);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Application de Jeu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}