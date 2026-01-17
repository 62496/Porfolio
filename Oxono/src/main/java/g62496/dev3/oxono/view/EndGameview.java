package g62496.dev3.oxono.view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
/**
 * The EndGameview class represents the end game screen that displays the
 * result of the game. It shows the winner or a draw message depending on
 * the game outcome.
 */
public class EndGameview extends VBox{
    /**
     * Constructs an EndGameview that displays the winner's color.
     * The winner's color is passed as a parameter and displayed in either
     * pink (for PINK) or black (for BLACK).
     *
     * @param winColor The color of the winning player.
     */
    public EndGameview( g62496.dev3.oxono.model.Color winColor) {
            Label win = new Label("The WINNER IS");
            win.setFont(Font.font("Arial", FontWeight.BOLD, 50));
            win.setTextFill(Color.WHITE);
            Label winnerplayer = new Label(winColor.toString());
            if (winColor == g62496.dev3.oxono.model.Color.BLACK) {
                winnerplayer.setTextFill(Color.BLACK);
            } else {
                winnerplayer.setTextFill(Color.DEEPPINK);
            }
            winnerplayer.setFont(Font.font("Arial", FontWeight.BOLD, 50));
            this.getChildren().addAll(win, winnerplayer);
        }
    /**
     * Constructs an EndGameview that displays a draw message when there is
     * no winner.
     */
    public EndGameview() {
        Label drawMatch = new Label("DRAW MATCH");
        drawMatch.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        drawMatch.setTextFill(Color.WHITE);
        this.getChildren().add(drawMatch);
    }

}
