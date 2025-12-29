package g62496.dev3.oxono.view;

import g62496.dev3.oxono.controller.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
/**
 * The PlayerInfoview class is responsible for displaying information about the players.
 * It shows the current player and their associated color.
 */
public class PlayerInfoview extends VBox {
    /**
     * Constructs a PlayerInfoview instance and sets up the labels for player information.
     *
     * @param controller The controller used to determine the current player.
     */
    public PlayerInfoview(Controller controller) {
        this.setAlignment(Pos.CENTER);
        Label player1Label = new Label("Joueur 1 : Pions Roses");
        player1Label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        player1Label.setTextFill(Color.DEEPPINK);

        Label player2Label = new Label("Joueur 2 : Pions Noirs");
        player2Label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        player2Label.setTextFill(Color.BLACK);
        Label currentPlayerLabel = new Label("Joueur actuelle :");
        if (controller.getCurrentPlayer().getColor()== g62496.dev3.oxono.model.Color.PINK){
            currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            currentPlayerLabel.setTextFill(Color.DEEPPINK);
            this.getChildren().addAll(player1Label, player2Label, currentPlayerLabel);
        }else {
            currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            currentPlayerLabel.setTextFill(Color.BLACK);
            this.getChildren().addAll(player1Label, player2Label, currentPlayerLabel);
        }
    }

}
