package g62496.dev3.oxono.model;


import java.util.ArrayList;
import java.util.List;
/**
 * The Board class represents the game board, containing tokens, pieces, and totems.
 * It provides methods to manage the state of the board, place tokens, and check for valid moves.
 */
public class Board  {
    private Token[][] board;
    private List<Token> pinkOPieces = new ArrayList<>();
    private List<Token> pinkXPieces = new ArrayList<>();
    private List<Token> blackOPieces = new ArrayList<>();
    private List<Token> blackXPieces = new ArrayList<>();
    private List<Totem> Totems = new ArrayList<>();
    private int freeBox ;
    private int width;
    private int height;
    /**
     * Initializes the board with the specified width and height.
     *
     * @param width  the width of the board
     * @param height the height of the board
     */
    public Board(int width, int height) {
        this.height = height;
        this.width = width;
        board = new Token[this.height ][this.width ];
        freeBox = width*height;
    }
    /**
     * Adds a piece to the board at the specified position.
     *
     * @param piece the piece to add
     * @param dx    the x-coordinate of the position
     * @param dy    the y-coordinate of the position
     */
    private void addBoardPiece(Piece piece, int dx, int dy) {
        if (board[dy][dx]==null){
            board[dy][dx] = piece;
        }else {
            throw new IllegalArgumentException("this box is not empty");
        }
    }
    /**
     * Adds a totem to the board at the specified position.
     *
     * @param totem the totem to add
     */
    private void addBoardTotem(Totem totem) {
        if (board[totem.getDy()][totem.getDx()]==null){
            board[totem.getDy()][totem.getDx()] = totem;
        }else {
            throw new IllegalArgumentException("this box is not empty");
        }
    }
    /**
     * Removes a token from the specified position on the board.
     *
     * @param dx the x-coordinate of the position
     * @param dy the y-coordinate of the position
     */
    private void removeBoard(int dx, int dy) {
            board[dy][dx] = null;
    }
    /**
     * Removes a piece from the board during an undo operation.
     *
     * @param dx    the x-coordinate of the position
     * @param dy    the y-coordinate of the position
     * @param token the token to remove
     */
    public void removePieceUndo(int dx,int dy,Token token) {
        Color color =  token.getColor();
        Symbol symbol = ((Piece) token).getType();
        if (color==Color.PINK && symbol==Symbol.X){
            pinkXPieces.remove(token);
        }
        if (color==Color.PINK && symbol==Symbol.O){
            pinkOPieces.remove(token);
        }
        if (color==Color.BLACK && symbol==Symbol.X){
            blackXPieces.remove(token);
        }
        if (color==Color.BLACK && symbol==Symbol.O){
            blackOPieces.remove(token);
        }
        freeBox++;
        board[dy][dx] = null;
    }
    /**
     * Removes a totem from the board during an undo operation.
     *
     * @param dx the x-coordinate of the position
     * @param dy the y-coordinate of the position
     */
    public void removeTotemUndo(int dx,int dy) {
        board[dy][dx] = null;
    }
    /**
     * initialise a totem on the board at the specified position.
     *
     * @param totem the totem to place
     */
    public void initialiseTotem(Totem totem) {
        if (totem.getType()==Symbol.X){
            Totems.add(0,totem);
            freeBox--;
        }else{
            Totems.add(1,totem);
            freeBox--;
        }
        board[totem.getDy()][totem.getDx()] = totem;
    }
    /**
     * Moves a totem from one position to another on the board.
     *
     * @param totem the totem to move
     * @param dx    the x-coordinate of the original position
     * @param dy    the y-coordinate of the original position
     */
    public void moveTotem(Totem totem, int dx, int dy) {
        if (totem.getType()==Symbol.X){
            Totems.removeFirst();
            Totems.add(0,totem);
        }else{
            Totems.removeLast();
            Totems.add(1,totem);
        }
        addBoardTotem(totem);
        removeBoard(dx, dy);
    }
    /**
     * Sets a piece on the board at the specified position.
     *
     * @param piece the piece to place
     * @param newDx the x-coordinate of the new position
     * @param newDy the y-coordinate of the new position
     */
    public void setPiece(Piece piece, int newDx, int newDy) {
        addBoardPiece(piece, newDx, newDy);
        if (piece.getColor()== Color.PINK){
            if ((piece).getType() == Symbol.X){
                pinkXPieces.add(piece);
            }else {
                pinkOPieces.add(piece);
            }
            freeBox--;
        }else {
            if (( piece).getType() == Symbol.X){
                blackXPieces.add(piece);
            }else{
                blackOPieces.add(piece);
            }
            freeBox--;
        }
    }
    /**
     * Gets the token at the specified position on the board.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the token at the specified position
     */
    public Token getTokenAt(int x, int y) {
        return board[x][y];
    }

    /**
     * Gets the number of pink O pieces on the board.
     *
     * @return the number of pink O pieces
     */
    public int getPinkOPieces() {
        return pinkOPieces.size();
    }
    /**
     * Gets the number of pink X pieces on the board.
     *
     * @return the number of pink X pieces
     */
    public int getPinkXPieces() {
        return pinkXPieces.size();
    }
    /**
     * Gets the number of black O pieces on the board.
     *
     * @return the number of black O pieces
     */
    public int getBlackOPieces() {
        return blackOPieces.size();
    }
    /**
     * Gets the number of black X pieces on the board.
     *
     * @return the number of black X pieces
     */
    public int getBlackXPieces() {
        return blackXPieces.size();
    }

    /**
     * Gets the number of free spaces available on the board.
     *
     * @return the number of free spaces
     */
    public int getFreeBox() {
        return freeBox;
    }
    /**
     * Gets the list of totems on the board.
     *
     * @return the list of totems
     */
    public List<Totem> getTotems() {
        return Totems;
    }
    /**
     * Checks for valid positions to place a totem near the given position.
     *
     * @param position the position to check around
     * @return a list of valid positions
     */
    public List<Position> checkTotemBox(Position position){
        List<Position> positionList = new ArrayList<>();
        int xPos = position.dx+1;
        int yPos = position.dy;

        while(xPos<this.width && getTokenAt(yPos,xPos)==null){
            positionList.add(new Position(xPos,yPos));
            xPos++;
        }
        int xNeg = position.dx-1;
        int yNeg = position.dy;
        while(xNeg>=0 && getTokenAt(yNeg,xNeg)==null  ){
            positionList.add(new Position(xNeg,yNeg));
            xNeg--;
        }
        int xVerticalPos = position.dx;
        int yVerticalPos = position.dy+1;
        while(yVerticalPos<this.height && getTokenAt(yVerticalPos,xVerticalPos)==null){
            positionList.add(new Position(xVerticalPos,yVerticalPos));
            yVerticalPos++;
        }
        int xVerticalNeg = position.dx;
        int yVerticalNeg = position.dy-1;
        while(yVerticalNeg>=0&& getTokenAt(yVerticalNeg,xVerticalNeg)==null  ){
            positionList.add(new Position(xVerticalNeg,yVerticalNeg));
            yVerticalNeg--;
        }
        if (positionList.isEmpty()){

            return checkEnclavedTotem(position);

        }else{
            return positionList;
        }
    }
    /**
     * Checks for valid positions to place a piece near the given position.
     *
     * @param position the position to check around
     * @return a list of valid positions
     */
    public List<Position> checkPieceBox(Position position){
        List<Position> positionList = new ArrayList<>();
            int dx = position.dx;
            int dy = position.dy;
            for (int i = dx + 1; i >= dx - 1; i--) {
                if (i >= 0 && i < width) {
                    if (getTokenAt(dy, i) == null) {
                        positionList.add(new Position(i,dy));
                    }
                }
            }
            for (int i = dy + 1; i >= dy - 1; i--) {
                if (i >= 0 && i < height) {
                    if (getTokenAt(i, dx) == null) {
                        positionList.add(new Position(dx,i));
                    }
                }
            }
        if (positionList.isEmpty()){
            return checkPieceEnclavedTotem();

        }else{
            return positionList;
        }
    }
    private List<Position> checkPieceEnclavedTotem() {
        List<Position> positionList = new ArrayList<>();

        for (int j = 0; j < width; j++) {
            for (int k = 0; k < height; k++) {
                if (getTokenAt(k, j) == null) {
                    positionList.add(new Position(j,k));
                }
            }
        }
        return positionList;
    }
    private List<Position> checkEnclavedTotem(Position position) {
        List<Position> positionList = new ArrayList<>();
            int xPos = position.dx +1 <width? position.dx+ 1: position.dx;
            int yPos = position.dy;

            while (xPos < this.width-1 && getTokenAt(yPos, xPos) != null) {
                xPos++;
            }
            if (xPos<this.width &&getTokenAt(yPos, xPos) == null) {
                positionList.add(new Position(xPos,yPos));
            }
            int xNeg = position.dx - 1>=0 ? position.dx-1:position.dx;
            int yNeg = position.dy;
            while (xNeg > 0 && getTokenAt(yNeg, xNeg) != null) {
                xNeg--;
            }
            if (xNeg>=0 && getTokenAt(yNeg, xNeg) == null) {
                positionList.add(new Position(xNeg,yNeg));
            }
            int xVerticalPos = position.dx;
            int yVerticalPos = position.dy+1< height ? position.dy + 1:position.dy;
            while (yVerticalPos < this.height-1 && getTokenAt(yVerticalPos, xVerticalPos) != null) {
                yVerticalPos++;
            }
            if (yVerticalPos<this.height && getTokenAt(yVerticalPos, xVerticalPos) == null) {
                positionList.add(new Position(xVerticalPos,yVerticalPos));
            }
            int xVerticalNeg = position.dx;
            int yVerticalNeg = position.dy - 1>=0?position.dy-1 : position.dy;
            while (yVerticalNeg >0 && getTokenAt(yVerticalNeg, xVerticalNeg) != null) {
                yVerticalNeg--;
            }
            if(yVerticalNeg>=0 && getTokenAt(yVerticalNeg, xVerticalNeg) == null) {
                positionList.add(new Position(xVerticalNeg,yVerticalNeg));
            }
        return positionList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}


