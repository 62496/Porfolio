package g62496.dev3.oxono.model;
import java.util.ArrayList;
import java.util.List;

/**
 * The Oxono class represents the game state and handles the game's core logic,
 * such as turn management and checking win conditions.
 */
public class Oxono  {
    private boolean totemTurn = true;
    private Symbol CurrentSymbol;
    private Board board;
    private CurrentPlayer CurrentPlayer = new CurrentPlayer();
    private boolean firsClick = true;
    private boolean showGreenPieceBox = true;
    private Facade facade ;
    private Position pos1;
    private Position pos2;


    public Oxono(Board board,Facade facade) {
        this.board = board;
        this.facade = facade;
    }
    /**
     * Checks if the game is over based on the current board state.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver(){
        int cptColor =0;
        int cptSymbol =0;

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getTokenAt(j, i) instanceof Totem) {
                    cptColor =0;
                    cptSymbol=0;
                } else {
                    if (board.getTokenAt(j, i) != null && board.getTokenAt(j, i).getColor() == CurrentPlayer.getColor()) {
                        cptColor++;
                    } else {
                        cptColor = 0;
                    }
                    if (board.getTokenAt(j, i) != null && ((Piece) board.getTokenAt(j, i)).getType() == CurrentSymbol) {

                        cptSymbol++;
                    } else {
                        cptSymbol = 0;
                    }
                    if (cptSymbol == 4 || cptColor == 4) {
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if (board.getTokenAt(i, j) instanceof Totem) {
                    cptColor =0;
                    cptSymbol=0;
                } else {
                    if (board.getTokenAt(i, j) != null && board.getTokenAt(i, j).getColor() == CurrentPlayer.getColor()) {
                        cptColor++;
                    } else {
                        cptColor = 0;
                    }
                    if (board.getTokenAt(i, j) != null && ((Piece) board.getTokenAt(i, j)).getType() == CurrentSymbol) {
                        cptSymbol++;
                    } else {
                        cptSymbol = 0;
                    }
                    if (cptSymbol == 4 || cptColor == 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Checks the first click of the totem for validity.
     *
     * @param positionList the list of positions selected
     * @return a list of valid positions or the position of the wrong box where you click.
     */
    public List<Position> checkTotemFirstClick(List<Position> positionList) {
        if (board.getTokenAt(positionList.get(0).dy, positionList.get(0).dx) instanceof Totem) {
            firsClick = false;
            return board.checkTotemBox(positionList.get(0));
        }else {
            List<Position> posWrongBox = new ArrayList<>();
            posWrongBox.add(0,new Position(-1,-1));
            posWrongBox.add(1,positionList.removeFirst());
            return posWrongBox;
        }
    }
    /**
     * Checks the second click of the totem for validity.
     *
     * @param positionList the list of positions selected
     * @return a list of valid positions or the position of the wrong box where you click.
     */
    public List<Position> checkTotemSecondClick(List<Position> positionList){
        pos2= positionList.get(1);
        if (board.getTokenAt(pos2.dy, pos2.dx)instanceof Piece){
            List<Position> posList = board.checkTotemBox(positionList.getFirst());
            posList.add(0,new Position(-3,-3));
            posList.add(1,new Position(positionList.getLast().dx,positionList.removeLast().dy));
            return posList;
        }else if(board.getTokenAt(pos2.dy, pos2.dx) instanceof Totem) {
            if (pos2.dx != positionList.get(0).dx && pos2.dy != positionList.get(0).dy) {
                positionList.removeFirst();
                List<Position> posList = board.checkTotemBox(pos2);
                posList.add(0,new Position(-1,-1));
                return posList;
            } else {
                positionList.clear();
                firsClick=true;
                List<Position> posList = new ArrayList<>();
                posList.add(new Position(-2,-2));
                return posList;
            }
        }else if (checkMoveTotem(positionList, board.checkTotemBox(positionList.get(0)))){
            CurrentSymbol = (((Totem) board.getTokenAt(positionList.get(0).dy, positionList.get(0).dx)).getType());
            Totem totem = new Totem(CurrentSymbol, pos2);
            facade.moveTotem(totem, positionList.get(0),board);
            totemTurn = false;
            positionList.removeFirst();
            firsClick=true;
            return checkPiece(positionList);
        }else {
            List<Position> posList = board.checkTotemBox(positionList.getFirst());
            posList.add(0,new Position(-4,-4));
            posList.add(1,new Position(positionList.getLast().dx,positionList.removeLast().dy));
            return posList;
        }
    }
    /**
     * Checks the validity of a piece placement based on the selected positions.
     *
     * @param posList the list of positions selected
     * @return a list of valid positions for placing the piece
     */
    public List<Position> checkPiece(List<Position> posList){
        if (showGreenPieceBox) {
            showGreenPieceBox=false;
            return board.checkPieceBox(posList.getFirst());
        }else{
            pos1 = posList.getFirst();
            pos2 = posList.removeLast();
            if (checkMovePiece(pos2,board.checkPieceBox(pos1))) {
                Piece piece = new Piece(CurrentPlayer.getColor(), CurrentSymbol);
                if(facade.insertToken(piece, pos2,board)) {
                    totemTurn = true;
                    //CurrentPlayer.next();
                    showGreenPieceBox = true;
                    posList.clear();
                }
            } else {
                List<Position> positionList = board.checkPieceBox(pos1);
                positionList.add(0,new Position(-1,-1));
                positionList.add(1,pos2);
                return positionList;
            }
        }
        return null;
    }

    private boolean checkMoveTotem(List<Position> posList,List<Position> rightPosList) {
        while (!rightPosList.isEmpty()){
            if (rightPosList.getLast().dy == posList.getLast().dy && rightPosList.getLast().dx == posList.getLast().dx) {
                return true;
            } else {
                rightPosList.removeLast();
            }
        }
        return false;
    }
    private boolean checkMovePiece(Position position,List<Position> rightPosList) {
        while (!rightPosList.isEmpty()){
            if (rightPosList.getLast().dy == position.dy&&rightPosList.getLast().dx == position.dx) {
                return true;
            } else {
                rightPosList.removeLast();
            }
        }
        return false;
    }
    /**
     * Checks if the match is a draw.
     *
     * @return true if the match is a draw, false otherwise
     */
   public boolean isDrawMatch(){
        List<Position> rightPosListTotemX =board.checkTotemBox(board.getTotems().getFirst().getPosition());
        List<Position> rightPosListTotemO =board.checkTotemBox(board.getTotems().getFirst().getPosition());
        if ( rightPosListTotemX.isEmpty()  && rightPosListTotemO.isEmpty()){
            return true;
        }
        return false;
   }
    /**
     * Checks if it is currently the Totem's turn.
     *
     * @return true if it is the Totem's turn, false otherwise
     */
    public boolean isTotemTurn() {
        return totemTurn;
    }
    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public CurrentPlayer getCurrentPlayer() {
        return CurrentPlayer;
    }
    /**
     * Checks if it's the first click during the game when you play a totem.
     *
     * @return true if it is the first click, false otherwise
     */
    public boolean isFirsClick() {
        return firsClick;
    }
    /**
     * Gets the currentTotem that you use.
     *
     * @return the currenTotem.
     */
    public Totem getCurrentTotem() {
        Totem totem = null;
        for (int i = 0; i < 2; i++) {
            if ( board.getTotems().get(i).getType() == CurrentSymbol){
                 totem = new Totem(CurrentSymbol,  board.getTotems().get(i).getPosition());
                 break;
            }
        }
       return totem;
    }
    /**
     * Gets the number of pink O pieces on the board.
     *
     * @return the number of pink O pieces
     */
    public int getPinkOPieces() {
        return board.getPinkOPieces();
    }
    /**
     * Gets the number of pink X pieces on the board.
     *
     * @return the number of pink X pieces
     */
    public int getPinkXPieces() {
        return board.getPinkXPieces();
    }
    /**
     * Gets the number of black O pieces on the board.
     *
     * @return the number of black O pieces
     */
    public int getBlackOPieces() {
        return board.getBlackOPieces();
    }
    /**
     * Gets the number of black X pieces on the board.
     *
     * @return the number of black X pieces
     */
    public int getBlackXPieces() {
        return board.getBlackXPieces();
    }
    /**
     * Gets the number of free spaces available on the board.
     *
     * @return the number of free spaces
     */
    public int getFreeBox() {
        return board.getFreeBox();
    }
    public List<Totem>getTotems(){
        return board.getTotems();
    }

}
