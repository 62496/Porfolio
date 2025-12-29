package g62496.dev3.oxono.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Homeview extends VBox {

    private TextField width;
    private TextField height;
    private ChoiceBox<String> level;
    private Button StartGame;

    public Homeview() {
        Label widthLabel = new Label("Entrez le nombre de lignes");
        widthLabel.setTextFill(Color.WHITE);
        Label heightLabel = new Label("Entrez le nombre de colonnes");
        heightLabel.setTextFill(Color.WHITE);
        Label levelLabel = new Label("Entrez le niveau");
        levelLabel.setTextFill(Color.WHITE);

        width = new TextField("6");
        height = new TextField("6");
        level = new ChoiceBox<>();
        level.getItems().addAll("easy", "normal");
        StartGame = new Button("Start");
        this.getChildren().addAll(widthLabel, height, heightLabel, width, levelLabel, level, StartGame);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(200);
    }

    public TextField getHeightBoard() {
        return height;
    }

    public TextField getWith() {
        return width;
    }

    public ChoiceBox<String> getLevel() {
        return level;
    }

    public Button getStartGame() {
        return StartGame;
    }

}
