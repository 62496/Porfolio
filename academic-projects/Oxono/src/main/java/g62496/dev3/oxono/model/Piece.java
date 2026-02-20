package g62496.dev3.oxono.model;
/**
 * Represents a piece in the game, which can be placed on the board by the player.
 */
public class Piece implements Token {
    private Color color;
    private Symbol Type;
    private String colorString ;

    /**
     * Initializes the piece with the given color and type.
     *
     * @param color the color of the piece
     * @param type the type of the piece (X or O)
     */
    public Piece(Color color, Symbol type) {
        this.color = color;
        this.Type = type;
    }
    /**
     * Moves the piece to the specified position on the board.
     *
     * @param board the board where the piece will be moved
     * @param dx the new x-coordinate for the piece
     * @param dy the new y-coordinate for the piece
     */
    @Override
    public void move(Board board, int dx, int dy) {


    }
    /**
     * Sets the color of the piece.
     *
     * @param color the color to set for the piece
     */
    @Override
    public void Setcolor(Color color) {
         if (color ==Color.PINK){
              colorString ="\u001B[35m";
         }else {
              colorString = "\u001B[30m";
         }
        this.color = color;
    }

    /**
     * Gets the color of the piece.
     *
     * @return the color of the piece
     */
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
         if(this.color==Color.PINK){
            String s = "\u001B[35m" + Type + "\u001B[0m";
            return s;
        }else{
            String blacks ="\u001B[30m" +Type + "\u001B[0m";
            return blacks;
        }
    }

    public Symbol getType() {
        return Type;
    }
}
