package g62496.dev3.oxono.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
/**
 * The Titleview class represents the title section of the game.
 * It displays the game title "OXONO" in a bold, large font.
 */
public class Titleview extends StackPane {
    /**
     * Constructs a Titleview instance and sets up the title label.
     */
    public Titleview()  {
        Label title = new Label("OXONO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        this.getChildren().add(title);
        this.setPrefHeight(80);
    }
}
