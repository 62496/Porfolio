package g62496.dev3.oxono.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
/**
 * The Controlview class represents the control panel that provides buttons
 * to give up, restart the game, or quit the application.
 * This class is used to manage the game control actions for the user.
 */
public class Controlview extends HBox {
    private Button giveUpButton;
    private Button restartButton;
    /**
     * Constructs a Controlview instance with three buttons:
     * "Give up", "Restart", and "Leave".
     * It sets up the action for the "Quit" button to exit the application
     * and defines visual feedback for button presses and releases.
     */
    public Controlview() {
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(20,20);
        giveUpButton = new Button("Give up");
         restartButton = new Button("Restart");
        Button quitButton = new Button("Leave");

        quitButton.setOnAction(e -> System.exit(0));
        this.getChildren().addAll(giveUpButton,restartButton, quitButton);

        giveUpButton.setOnMousePressed(e -> giveUpButton.setStyle(
                "-fx-background-color: darkgray; -fx-translate-y: 2px;"));
        giveUpButton.setOnMouseReleased(e -> giveUpButton.setStyle(
                "-fx-background-color: lightgray; -fx-translate-y: 0px;"));
        quitButton.setOnMousePressed(e -> quitButton.setStyle(
                "-fx-background-color: darkgray; -fx-translate-y: 2px;"));
        quitButton.setOnMouseReleased(e -> quitButton.setStyle(
                "-fx-background-color: lightgray; -fx-translate-y: 0px;"));
        restartButton.setOnMousePressed(e -> restartButton.setStyle(
                "-fx-background-color: darkgray; -fx-translate-y: 2px;"));
        restartButton.setOnMouseReleased(e -> restartButton.setStyle(
                "-fx-background-color: lightgray; -fx-translate-y: 0px;"));
    }
    /**
     * Returns the "Give up" button.
     *
     * @return The giveUpButton.
     */
    public Button getGiveUpButton() {
        return giveUpButton;
    }
    /**
     * Returns the "Restart" button.
     *
     * @return The restartButton.
     */
    public Button getRestartButton() {
        return restartButton;
    }
}
