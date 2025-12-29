package g62496.dev3.oxono.view;


import g62496.dev3.oxono.model.Position;
import g62496.dev3.oxono.controller.Controller;
import g62496.dev3.oxono.model.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
/**
 * The Boardview class is responsible for rendering and updating the game board's view.
 * It displays the grid and interacts with the controller to update the board's state.
 */
public class Boardview  extends GridPane  {
    private Board board;
    private Tokenview tokenView;
    private List<Position> posList = new ArrayList<>();
    private Controller controller;

    /**
     * Constructs a Boardview instance with a given board and controller.
     *
     * @param board The board instance representing the game grid.
     * @param controller The controller used to manage game actions.
     */
    public Boardview(Board board,Controller controller ) {
        this.setId("Gridpane");
        this.setAlignment(Pos.CENTER);
        this.board = board;
        intialiseTotemBoard();
        tokenView = new Tokenview();
        this.controller = controller;
    }

    private void createCenterBoard() {
        for (int row = 0; row < board.getHeight() ; row++) {
            for (int col = 0; col < board.getWidth() ; col++) {
                Button button1 = new Button();
                List<Integer> Coordone = new ArrayList<>();
                Coordone.add(col);
                Coordone.add(row);
                button1.setUserData(Coordone);
                button1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button1.setStyle("-fx-background-color: transparent;");
                StackPane cell = new StackPane(button1);
                cell.setPrefSize(80, 80);
                cell.setStyle("-fx-border-color: #FFFFFF;");
                this.add(cell, col, row);
                button1.setOnAction(e -> checkFirstButton(button1));
            }
        }
    }
    private void intialiseTotemBoard() {
        Totem totemx = new Totem(Symbol.X, new Position((board.getWidth() / 2), (board.getHeight() / 2)));
        board.initialiseTotem(totemx);

        Totem totemO = new Totem(Symbol.O, new Position((board.getWidth() / 2) - 1, (board.getHeight() / 2) - 1));
        board.initialiseTotem(totemO);
    }

    private void checkFirstButton(Button button) {
        Position posClick= new Position(((List<Integer>) button.getUserData()).getFirst(),
                ((List<Integer>) button.getUserData()).getLast());
        posList.add(posClick);
        controller.checkclick(posList);
    }
    /**
     * Adds new tokens (pieces or totems) to the board view.
     *
     * @param positionList A list of positions where the tokens should be placed.
     */
    public void addRectangle(List<Position> positionList) {
        if (positionList!=null){
            if (positionList.getFirst().getDx()<0) {
                positionList.removeFirst();
                flashColor(positionList.getFirst().getDx(), positionList.getFirst().getDy());
                positionList.removeFirst();
            }
            while (!positionList.isEmpty()) {
                    int dy = positionList.getLast().getDy();
                    int dx = positionList.removeLast().getDx();
                    Rectangle rectangle = new Rectangle();
                    rectangle.setFill(Color.GREEN);
                    StackPane green = new StackPane(rectangle);
                    green.setMouseTransparent(true);
                    green.setPrefSize(80, 80);
                    green.setStyle("-fx-background-color: #7CD38B; -fx-border-color: #FFFFFF;");
                    this.add(green, dx, dy);
            }
        }
    }
    private void flashColor( int col, int row) {
        Rectangle redRectangle = new Rectangle(80,80,Color.RED);
        this.add(redRectangle, col, row);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.2));

        pause.setOnFinished(event -> this.getChildren().remove(redRectangle));

        pause.play();


    }
    /**
     * Displays the game board view in the user interface.
     */
    public void DisplayBoardView() {
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                removeGrid(i, j);
            }
        }
        createCenterBoard();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getTokenAt(j,i) != null) {
                    this.add(tokenView.addGrid(board.getTokenAt(j,i)),i,j);
                }
            }
        }
        DisplayBoard();
    }
    private void removeGrid(int col, int row) {
        for (int i =0;i<2;i++){
            for (Node node : this.getChildren()) {
                // Vérifie si le Node est à la colonne et la rangée spécifiées
                if (this.getColumnIndex(node) != null && this.getRowIndex(node) != null
                        && this.getColumnIndex(node) == col
                        && this.getRowIndex(node) == row) {

                    this.getChildren().remove(node);
                    break; // Important pour éviter des erreurs de modification concurrente
                }
            }
        }
    }
    /**
     * Displays the current state of the game board.
     */
    public void DisplayBoard() {
        // todo: bouger ça dans la vue
        System.out.println("-------------------------------------------");
        for (int i = 0; i <board.getHeight() ; i++) {
            for (int j = 0; j <board.getWidth(); j++) {
                if (board.getTokenAt(i,j) != null) {
                    System.out.print(board.getTokenAt(i,j) + "|");
                } else {
                    System.out.print(".|");
                }
            }
            System.out.println();
        }
    }
    /**
     * Returns the board details such as how much free case leaft,how muck token has been place for each color and each symbol.
     * have also the undo and redo button.
     * @return A VBox containing the board details.
     */
    public BoardDetailview boardDetail(){
        BoardDetailview boardDetailview = new BoardDetailview(this,controller);
        return  boardDetailview;
    }

}

