package g62496.dev3.oxono.view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Messageview extends VBox  {
    /**
     * Constructs a Messageview with a given message and a VBox.
     * The message is displayed in a white font, and the content is wrapped if it exceeds the available space.
     * The message is placed below a label with the text "message".
     *
     * @param vBox The VBox that this Messageview will include.
     * @param msg The message content that will be displayed.
     */
    public Messageview(VBox vBox,String msg) {
        Label widthLabel = new Label(msg);
        widthLabel.setTextFill(Color.WHITE);
        widthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        widthLabel.setWrapText(true); // Permet le retour à la ligne
        Label message = new Label("message");
        message.setTextFill(Color.WHITE);
        message.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        this.getChildren().addAll(message,widthLabel);
        this.getChildren().add(vBox);
        this.setSpacing(10);
        this.setPrefSize(300,300);
    }

}
