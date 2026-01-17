package g62496.dev3.oxono.view;

import g62496.dev3.oxono.controller.Controller;
import g62496.dev3.oxono.model.Piece;
import g62496.dev3.oxono.model.Symbol;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
/**
 * The BoardDetailview class provides a detailed view of the game board,
 * displaying information about the current state of the game, such as
 * the number of available pieces, undo/redo buttons, and the current state
 * of the board (pieces of both players).
 * This class is a container that adds a grid of labels and buttons to show
 * the current game details.
 */
public class BoardDetailview extends VBox {
    private GridPane gridPane;

    /**
     * Constructs a BoardDetailview instance with a given board view and controller.
     * It initializes the game details section, including the undo and redo buttons,
     * the number of pieces for both players, and the current game status.
     *
     * @param boardview The board view that displays the game grid.
     * @param controller The controller used to manage game logic and actions.
     */
    public BoardDetailview(GridPane boardview, Controller controller) {
        gridPane = new GridPane();
        intialiseBoard();

        for (int col = 0; col < 6; col++) {
            StackPane cell = new StackPane();
            if (col==0) {
                Label freeBox = new Label(String.valueOf(controller.getFreeBox()));
                freeBox.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                freeBox.setTextFill(Color.WHITE);
                cell.getChildren().add(freeBox);
            }
            gridPane.add(cell, col, 0);
        }
        for (int col = 0; col < 6; col++) {
            StackPane cell;
            if (col==0){
                Button button1 = new Button();
                button1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button1.setStyle("-fx-background-color: ;");
                List<Integer> Coordone = new ArrayList<>();
                Coordone.add(col);
                Coordone.add(1);
                button1.setUserData(Coordone);
                Label undo = new Label("UNDO");
                undo.setMouseTransparent(true);
                undo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                undo.setTextFill(Color.WHITE);
                cell = new StackPane(button1, undo);
                button1.setOnAction(e->controller.undo());
            }else if ( col <= 2) {
                Label blackPiece = col==1 ? new Label(String.valueOf(16 - controller.getBlackOPieces())) :
                        new Label(String.valueOf(16 - controller.getBlackXPieces())) ;
                blackPiece.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                blackPiece.setTextFill(Color.BLACK);
                cell = new StackPane(blackPiece);
            } else if ( col <= 4) {
                Label pinkPiece = col == 3 ?  new Label(String.valueOf(16 - controller.getPinkOPieces())):
                        new Label(String.valueOf(16 - controller.getPinkXPieces()));
                pinkPiece.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                pinkPiece.setTextFill(Color.PINK);
                cell = new StackPane(pinkPiece);
            } else {
                Button button1 = new Button();
                button1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button1.setStyle("-fx-background-color: ;");
                List<Integer> Coordone = new ArrayList<>();
                Coordone.add(col);
                Coordone.add(1);
                button1.setUserData(Coordone);
                Label redo = new Label("REDO");
                redo.setMouseTransparent(true);
                redo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                redo.setTextFill(Color.WHITE);
                cell = new StackPane(button1, redo);
                button1.setOnAction(e->controller.redo());
            }
            cell.setPrefSize(80, 80);
            //cell.setStyle("-fx-border-color: #FFFFFF;");
            gridPane.add(cell, col, 1);
        }

        this.getChildren().addAll(boardview,gridPane);
        this.setSpacing(10);
    }

    private void intialiseBoard() {
        Tokenview tokenview = new Tokenview();
        ///EST CE QU ON PEUT CREER UN GRIDPANE OU ALORS FAIRE DANS UNE NOUVELLE CLASSE
        Piece blackpieceO= new Piece( g62496.dev3.oxono.model.Color.BLACK, Symbol.O);
        Piece blackpieceX= new Piece( g62496.dev3.oxono.model.Color.BLACK,Symbol.X);
        StackPane cell = new StackPane(tokenview.createPiece(blackpieceO));
        cell.setPrefSize(80, 80);
        StackPane cell2 = new StackPane(tokenview.createPiece(blackpieceX));
        cell2.setPrefSize(80, 80);


        gridPane.add(cell,1,0);
        gridPane.add(cell2,2,0);

        Piece PinkpieceO= new Piece( g62496.dev3.oxono.model.Color.PINK,Symbol.O);
        Piece PinkpieceX= new Piece( g62496.dev3.oxono.model.Color.PINK,Symbol.X);

        StackPane cell3 = new StackPane(tokenview.createPiece(PinkpieceO));
        cell3.setPrefSize(80, 80);
        StackPane cell4 = new StackPane(tokenview.createPiece(PinkpieceX));
        cell4.setPrefSize(80, 80);


        gridPane.add(cell3,3,0);
        gridPane.add(cell4,4,0);
        gridPane.setAlignment(Pos.CENTER);
    }

}
