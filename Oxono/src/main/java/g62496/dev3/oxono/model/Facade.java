package g62496.dev3.oxono.model;

import g62496.dev3.oxono.util.*;

import java.util.ArrayList;
import java.util.List;
/**
 * The Facade class handles interactions between the game components and manages the state of the game.
 * It acts as the central interface for the game logic.
 */
public class Facade implements Observable {
    private CommandManager commandManager = new CommandManager();
    private List<Observer> observerList = new ArrayList<>();
    private Oxono oxono;
    private List<Position>positionList;
    private BotStrategy botStrategy;
    private boolean undoTotemTurn;

    /**
     * Initializes the game facade with the given board.
     *
     * @param board the board for the game
     */
    public Facade(Board board) {
        this.oxono = new Oxono(board,this);
        botStrategy = new BoteasyStrategy(board,this);
    }
    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver(){
        return oxono.isGameOver();
    }
    /**
     * Checks if the game ended in a draw.
     *
     * @return true if the match is a draw, false otherwise
     */
    public boolean isDrawMatch(){
        return oxono.isDrawMatch();

    }
    public void Start(int width, int height){
         //board= new Board(width,height);
    }
    /**
     * Handles the first click when placing a Totem.
     *
     * @param posList a list of positions where the totem can be placed
     */
    public void checkTotemFirstClick(List<Position> posList){
        positionList =oxono.checkTotemFirstClick(posList);
        if (positionList.getFirst().dx == -1){
            String message=  "appuyer sur un totem";
            notifyObservers(positionList,message);
        }else {
            String message = "appuiez la ou vous voulez mettre votre totem";
            notifyObservers(positionList,message);
        }
    }
    /**
     * Handles the second click when placing a Totem.
     *
     * @param posList a list of positions where the totem can be placed.
     */
    public void checkTotemSecondClick(List<Position> posList){
        positionList =oxono.checkTotemSecondClick(posList);
        if (positionList.get(0).getDx()==-4 ){
            String message= "vous devez déplacer le totem horizontalement ou verticalement ";
            notifyObservers(positionList,message);
        }else if (positionList.get(0).getDx()==-3) {
            String message= "cette case est deja occuper par un jeton";
            notifyObservers(positionList,message);
        }else if (positionList.get(0).getDx()==-1) {
            positionList.removeFirst();
            String message= "vous avez avez changer de totem";
            notifyObservers(positionList,message);
        }else if(positionList.get(0).getDx()==-2) {
            positionList=null;
            String message= "vous avez annulez l'appuie du totem";
            notifyObservers(positionList,message);
        }else{
            String message= "Le mouvement a été éffectuer";
            notifyObservers(positionList,message);
        }
    }
    /**
     * Handles a click on a piece (token).
     *
     * @param posList a list of positions where the piece can be placed.
     */
    public void checkPieceClick(List<Position> posList){
        positionList =oxono.checkPiece(posList);
        if (positionList!=null){
            String message=  "vous devez placer le jeton dans les case adjaccente du totem";
            notifyObservers(positionList,message);
        }else {
            String message =  "vous devez mettre le jeton sur une des case adjacente du totem ";
            notifyObservers(positionList,message);
        }
        if (oxono.getCurrentPlayer().getColor()==Color.BLACK){
            botStrategy.play(oxono);
        }

    }
    /**
     * Moves a Totem to a specified position on the board.
     *
     * @param totem the totem to move
     * @param totemPos the target position to move the totem to
     * @param board the board where the totem is placed
     */
    public void moveTotem(Totem totem ,Position totemPos,Board board ) {
        Command command = new AddTotemCommand(board, totem, totemPos.dx, totemPos.dy);
        commandManager.Do(command);
        notifyObservers(null,null);
    }

    /**
     * Inserts a token (piece) on the board at a specified position.
     *
     * @param piece the piece (token) to place on the board
     * @param piecePos the position where the piece is placed
     * @param board the board where the piece is placed
     * @return true if the piece was successfully placed, false if the game is over or in a draw state
     */
    public boolean insertToken(Piece piece,Position piecePos,Board board ){
        Command command = new AddCommand(board, piece, piecePos.dx, piecePos.dy);
        commandManager.Do(command);

        if (isGameOver()|| isDrawMatch()){
            notifyObservers(null,null);
            return false;
        }else {
            oxono.getCurrentPlayer().next();
            notifyObservers(null,null);
            return true;
        }
    }
    /**
     * Undo the most recent move.
     */
    public void undo() {
        if (oxono.isTotemTurn()){
            commandManager.undo();
            notifyObservers(null,null);
        }
    }

    /**
     * Redo the most recent undone move.
     */
    public void redo() {
        if (oxono.isTotemTurn()){
            commandManager.redo();
            notifyObservers(null,null);
        }

    }
    /**
     * Checks if it is currently the Totem's turn.
     *
     * @return true if it is the Totem's turn, false otherwise
     */
    public boolean isTotemTurn(){
        return oxono.isTotemTurn();
    }
    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public CurrentPlayer getCurrenPlayer(){
        return oxono.getCurrentPlayer();
    }
    /**
     * Checks if it's the first click during the game when you play a totem.
     *
     * @return true if it is the first click, false otherwise
     */
    public boolean isFirsClick(){
        return oxono.isFirsClick();
    }
    /**
     * Gets the number of pink O pieces on the board.
     *
     * @return the number of pink O pieces
     */
    public int getPinkOPieces() {
        return oxono.getPinkOPieces();
    }
    /**
     * Gets the number of pink X pieces on the board.
     *
     * @return the number of pink X pieces
     */
    public int getPinkXPieces() {
        return oxono.getPinkXPieces();
    }
    /**
     * Gets the number of black O pieces on the board.
     *
     * @return the number of black O pieces
     */
    public int getBlackOPieces() {
        return oxono.getBlackOPieces();
    }
    /**
     * Gets the number of black X pieces on the board.
     *
     * @return the number of black X pieces
     */
    public int getBlackXPieces() {
        return oxono.getBlackXPieces();
    }
    /**
     * Gets the number of free spaces available on the board.
     *
     * @return the number of free spaces
     */
    public int getFreeBox() {
        return oxono.getFreeBox();
    }
    public List<Totem>getTotems(){
        return oxono.getTotems();
    }

    /**
     * Registers an observer to receive updates about the game's state.
     *
     * @param o the observer to be registered
     */
    @Override
    public void registerObserver(Observer o) {
        observerList.add(o);
    }
    /**
     * Removes an observer from the list of observers.
     *
     * @param o the observer to be removed
     */
    @Override
    public void removeObserver(Observer o) {
        observerList.remove(o);
    }
    /**
     * Notifies all registered observers of a change in the game state.
     *
     * @param positionList the list of positions involved in the change
     * @param message a message describing the change
     */
    @Override
    public void notifyObservers(List<Position>positionList,String message) {
        for(Observer o : observerList){
            o.update(positionList,message);
        }
    }

}

