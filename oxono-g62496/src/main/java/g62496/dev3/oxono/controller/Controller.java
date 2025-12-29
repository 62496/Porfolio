package g62496.dev3.oxono.controller;
import g62496.dev3.oxono.model.Position;
import g62496.dev3.oxono.model.*;
import g62496.dev3.oxono.view.Boardview;
import java.util.List;
/**
 * The Controller class manages the game logic, actions, and interactions between the model and the view.
 * It handles actions such as piece placement, undo/redo operations, and determining the current player.
 */
public class Controller {

    private Facade facade;
    private Boardview boardview;
    /**
     * Constructs a Controller with a given facade and board view.
     *
     * @param facade The facade that encapsulates the game logic.
     * @param boardview The view that displays the game board.
     */
    public Controller(Facade facade,Boardview boardview) {
        this.facade = facade;
        this.boardview = boardview;
    }

    public void checkclick(List<Position> posList){
        if (facade.isTotemTurn() && facade.isFirsClick() ){
            facade.checkTotemFirstClick(posList);
        }else if (facade.isTotemTurn() ){
             facade.checkTotemSecondClick(posList);
        }else{
            facade.checkPieceClick(posList);
        }
    }

    public CurrentPlayer getCurrentPlayer(){
        return facade.getCurrenPlayer();
    }
    public boolean isGameOver(){
        return facade.isGameOver();
    }
    public boolean isDrawMatch(){
        return facade.isDrawMatch();
    }
    public void undo(){
        facade.undo();
    }
    public void redo(){
        facade.redo();
    }
    public int getPinkOPieces() {
        return facade.getPinkOPieces();
    }

    public int getPinkXPieces() {
        return facade.getPinkXPieces();
    }

    public int getBlackOPieces() {
        return facade.getBlackOPieces();
    }

    public int getBlackXPieces() {
        return facade.getBlackXPieces();
    }
    public int getFreeBox() {
        return facade.getFreeBox();
    }
}
