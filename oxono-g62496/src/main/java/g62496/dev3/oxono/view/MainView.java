package g62496.dev3.oxono.view;

import g62496.dev3.oxono.controller.Controller;
import g62496.dev3.oxono.model.*;
import g62496.dev3.oxono.util.Observer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * MainView class is responsible for displaying the main game view. It initializes the home screen and sets up the game.
 */
public class MainView extends BorderPane implements Observer {
    private Boardview boardview =null ;
    private Board board;
    private Facade facade;
    private Controller controller;
    private BorderPane mainLayout;
    /**
     * Constructs a MainView and sets up the initial scene and home view.
     *
     * @param stage The primary stage for this application.
     */
    public MainView(Stage stage) {
        this.setId("rootHomeView");
        Scene scene = new Scene(this, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());
        stage.setTitle("home");
        stage.setScene(scene);
        setHomeView();
        stage.show();
    }
    /**
     * Sets up the home view with input fields for the game configuration.
     */
    public void setHomeView() {
        Homeview homeview = new Homeview();
        this.setMargin(homeview, new Insets(10, 0, 30, 150));
        this.setBottom(homeview);
        homeview.getStartGame().setOnAction(e -> setGame(homeview.getWith(), homeview.getHeightBoard()));
    }

    /**
     * Initializes the game board with the specified width and height
     * and the detail of the Game (Title,currentplayer,message,count the token and freebox).
     *
     * @param width The width (number of columns) of the game board.
     * @param height The height (number of rows) of the game board.
     */
    public void setGame(TextField width, TextField height) {
        int rows = Integer.parseInt(height.getText());
        int cols = Integer.parseInt(width.getText());
        board= new Board(cols,rows);
        facade = new Facade(board);
        facade.registerObserver(this);
        this.controller = new Controller(facade,boardview);
        boardview = new Boardview(board,controller);
        mainLayout = new BorderPane();
        this.mainLayout.setId("mainLayout");


        Scene boardScene = new Scene(mainLayout, 1200, 800);
        boardScene.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());
        Stage boardStage = new Stage();
        boardStage.setTitle("Oxono - Jeu de Plateau");
        boardStage.setScene(boardScene);

        boardview.DisplayBoardView();
        createControlSection();
        createPlayerInfoSection(null);
        createTitleSection();
        CreateBoardDetail();
        boardStage.show();

    }

    private void createMessageSection(VBox vBox,String message){
        Messageview messageview = new Messageview(vBox,message);
        messageview.setAlignment(Pos.CENTER);
        mainLayout.setRight(messageview);

    }
    private void createPlayerInfoSection(String message){
        PlayerInfoview playerInfo = new PlayerInfoview(controller);
        createMessageSection(playerInfo,message);
    }
    private void createControlSection(){
        Controlview controlview = new Controlview();
        controlview.getRestartButton().setOnAction(e-> resetGame());
        mainLayout.setBottom(controlview);
        controlview.getGiveUpButton().setOnAction(e -> {
            controlview.getGiveUpButton().setDisable(true);
            giveUpGame();
        });
    }
    private void resetGame(){
        board = new Board(board.getWidth(), board.getHeight());
        facade = new Facade(board);
        //facade.Start(rows,cols,);
        facade.registerObserver(this);
        this.controller = new Controller(facade,boardview);
        boardview = new Boardview(board,controller);
        update(null,null);
    }
    private void giveUpGame(){
        CurrentPlayer winnerplayer = controller.getCurrentPlayer();
        winnerplayer.next();

        EndGameview endGameview = new EndGameview(winnerplayer.getColor());
        mainLayout.setCenter(endGameview);
    }
    private void createTitleSection(){
        Titleview titleview = new Titleview();
        mainLayout.setTop(titleview);
    }

    private void createEngGame(){
        if (controller.isGameOver() ) {
            Color winColor = controller.getCurrentPlayer().getColor();
            ///montrer le tableau a la fin
            boardview.DisplayBoard();
            EndGameview endGameview = new EndGameview(winColor);
            mainLayout.setMargin(endGameview,new Insets(100,100,100,100));
            mainLayout.setCenter(endGameview);
        }else if (controller.isDrawMatch()){
            ///montrer le tableau a la fin
            boardview.DisplayBoard();
            EndGameview endGameview = new EndGameview();
            mainLayout.setMargin(endGameview,new Insets(100,100,100,100));
            mainLayout.setCenter(endGameview);
        }
    }
    private void CreateBoardDetail(){
        mainLayout.setCenter(boardview.boardDetail());
    }

    @Override
    public void update(List<Position>positionList,String message) {
        createPlayerInfoSection(message);
        CreateBoardDetail();
        createEngGame();
        boardview.DisplayBoardView();
        boardview.addRectangle(positionList);
    }
}
